package cn.jiangzeyin.util.net.ftp;

import cn.jiangzeyin.util.system.util.UtilSystemCache;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * ftp 连接池
 *
 * @author jiangzeyin
 */
public class FTPClientPool {

    private static ConcurrentHashMap<Integer, BlockingQueue<YokeFtp>> poolMap = new ConcurrentHashMap<Integer, BlockingQueue<YokeFtp>>();
    private static ConcurrentHashMap<Integer, Integer> poolNumber = new ConcurrentHashMap<Integer, Integer>();

    /**
     * @param publishScheme p
     * @return ftp
     * @throws InterruptedException i
     * @throws IOException          io
     * @author jiangzeyin
     */
    public static YokeFtp poll(FtpInfo publishScheme) throws InterruptedException, IOException {
        BlockingQueue<YokeFtp> blockingQueue = getQueue(publishScheme);
        YokeFtp ftp = blockingQueue.poll(publishScheme.getTimeOut(), publishScheme.getTimeUnit());
        if (ftp == null || !ftp.isConnected()) {
            // 释放连接
            FtpUtil.closeConnection(ftp);
            // 添加新的连接
            addBlockIngQueue(publishScheme);
            // 重新获取
            return poll(publishScheme);
        }
        return ftp;
    }

    /**
     * 取一个连接 没有就一直等待
     *
     * @param publishScheme p
     * @return ftp
     * @throws InterruptedException i
     * @throws IOException          io
     * @author jiangzeyin
     */
    public static YokeFtp take(FtpInfo publishScheme) throws InterruptedException, IOException {
        BlockingQueue<YokeFtp> blockingQueue = getQueue(publishScheme);
        YokeFtp ftp = blockingQueue.take();
        if (ftp == null || !ftp.isConnected()) {
            // 释放连接
            FtpUtil.closeConnection(ftp);
            // 添加新的连接
            addBlockIngQueue(publishScheme);
            // 重新获取
            return take(publishScheme);
        }
        return ftp;
    }

    /**
     * 释放资源
     *
     * @param ftp ftp
     * @return r
     * @author jiangzeyin
     */
    public static boolean release(YokeFtp ftp) {
        FtpUtil.closeConnection(ftp);
        return true;
    }

    /**
     * 释放资源
     *
     * @param ftps ftp
     * @return boolean
     * @author jiangzeyin
     */
    public static boolean release(YokeFtp[] ftps) {
        if (ftps == null)
            return false;
        for (YokeFtp yokeFtp : ftps) {
            if (!release(yokeFtp))
                return false;
        }
        return true;
    }

    /**
     * 获取连接池队列
     *
     * @param publishScheme scheme
     * @return b
     * @author jiangzeyin
     */
    private static BlockingQueue<YokeFtp> getQueue(FtpInfo publishScheme) {
        BlockingQueue<YokeFtp> blockingQueue = poolMap.get(publishScheme.getId());
        if (blockingQueue == null) {
            blockingQueue = initPublish(publishScheme);
        }
        return blockingQueue;
    }

    /**
     * 初始化发布方案的连接池
     *
     * @param publishScheme s
     * @return r
     * @author jiangzeyin
     */
    private static BlockingQueue<YokeFtp> initPublish(FtpInfo publishScheme) {
        int maxConnects = publishScheme.getMaxConnects();
        BlockingQueue<YokeFtp> blockingQueue = new PriorityBlockingQueue<YokeFtp>(maxConnects, new FtpClientComparator());// 初始化队列容量
        for (int i = 0; i < maxConnects; i++) {
            YokeFtp ftpClient;
            try {
                ftpClient = FtpUtil.getFtpClient(publishScheme);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // SystemLog.Log(LogType.systemInfo, );
                UtilSystemCache.getInstance().LOG_ERROR().error("初始化ftp连接失败", e);
                continue;
            }
            // ftpClient.setOrder(i);
            // ftpClient.setSchemeId(publishScheme.getId());
            // addNumber(publishScheme.getId());
            // blockingQueue.add(ftpClient);
            addToQueur(blockingQueue, ftpClient);
        }
        // 添加到缓存池中
        poolMap.put(publishScheme.getId(), blockingQueue);
        return blockingQueue;
    }

    /**
     * @param blockingQueue b
     * @param ftp           ftp
     * @author jiangzeyin
     */
    private static void addToQueur(BlockingQueue<YokeFtp> blockingQueue, YokeFtp ftp) {
        if (ftp == null)
            return;
        synchronized (blockingQueue) {
            Integer integer = poolNumber.get(ftp.getSchemeId());
            if (integer == null)
                integer = 0;
            else
                integer += 1;
            ftp.setOrder(integer);
            poolNumber.put(ftp.getSchemeId(), integer);
            blockingQueue.add(ftp);
        }
    }

    /**
     * @param publishScheme p
     * @throws IOException io
     * @author jiangzeyin
     */
    private static void addBlockIngQueue(FtpInfo publishScheme) throws IOException {
        BlockingQueue<YokeFtp> blockingQueue = getQueue(publishScheme);
        YokeFtp ftpClient = FtpUtil.getFtpClient(publishScheme);
        if (ftpClient == null)
            throw new RuntimeException("ftp 创建失败");
        // Integer integer = poolNumber.get(publishScheme.getId());
        // ftpClient.setOrder(integer + 1);
        // ftpClient.setSchemeId(publishScheme.getId());
        // blockingQueue.add(ftpClient);
        addToQueur(blockingQueue, ftpClient);
    }
}
