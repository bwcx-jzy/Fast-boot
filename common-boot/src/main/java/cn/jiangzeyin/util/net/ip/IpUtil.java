package cn.jiangzeyin.util.net.ip;

import cn.jiangzeyin.util.util.StringUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/14.
 */
public class IpUtil {
//
//    public static void main(String[] args) {
//        try {
//            InetAddress address1 = InetAddress.getByName("www.wodexiangce.cn");//获取的是该网站的ip地址，比如我们所有的请求都通过nginx的，所以这里获取到的其实是nginx服务器的IP地
//            String hostAddress1 = address1.getHostAddress();//124.237.121.122
//            InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");//根据主机名返回其可能的所有InetAddress对象
//            for (InetAddress addr : addresses) {
//                System.out.println(addr);//www.baidu.com/14.215.177.38
//                //www.baidu.com/14.215.177.37
//            }
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }

    public static String getHostAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
        return address.getHostAddress();//192.168.0.121
    }

    public static boolean isConnect(String httpPort) {
        if (StringUtil.isEmpty(httpPort)) {
            return false;
        }
        String ip = httpPort.substring(httpPort.indexOf("://") + 3);
        String[] ipInfo = ip.split(":");
        return IpUtil.isConnect(ipInfo[0], StringUtil.parseInt(ipInfo[1]));
    }

    public static boolean isConnect(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
