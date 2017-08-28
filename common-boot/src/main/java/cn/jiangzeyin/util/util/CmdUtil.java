package cn.jiangzeyin.util.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 系统操作工具类
 *
 * @author jiangzeyin
 */
public final class CmdUtil {
    /**
     * @param savePath savePath
     * @throws Exception e
     * @author jiangzeyin
     */
    public static void backDatabaseTool(String savePath) throws Exception {
//		// 加载配置
//		String path = System.getProperty("catalina.base") + File.separator + "conf" + File.separator + "jdbc.properties";
//		Properties properties = new Properties();
//		properties.load(new BufferedInputStream(new FileInputStream(path)));
//		String rootPath = properties.getProperty("cd");
//		if (StringUtil.isEmpty(rootPath)) {
//			ServiceErrorLog logsss = new ServiceErrorLog();
//			logsss.setName("备份数据错误,配置数据库路径有错误");
//			logsss.setType(ServiceErrorLog.SystemInfo);
//			logsss.setMark("请正确配置路径");
//			logsss.add();
//		}
//		rootPath = MPooledDataSource.util.decrypt(rootPath);
//		if (!new File(rootPath).exists()) {
//			ServiceErrorLog logsss = new ServiceErrorLog();
//			logsss.setName("备份数据错误,数据库路径有错误");
//			logsss.setType(ServiceErrorLog.SystemInfo);
//			logsss.setMark("没有找到：" + rootPath);
//			logsss.add();
//		}
//		String hostIP = properties.getProperty("ab");
//		if (StringUtil.isEmpty(hostIP)) {
//			ServiceErrorLog logsss = new ServiceErrorLog();
//			logsss.setName("备份数据错误,数据库ip有错误");
//			logsss.setType(ServiceErrorLog.SystemInfo);
//			logsss.setMark("ip错误");
//			logsss.add();
//		}
//		hostIP = MPooledDataSource.util.decrypt(hostIP);
//		// 加载所有数据库
//		Enumeration<?> enu2 = properties.propertyNames();
//		HashMap<String, DatabasInfo> databaNames = new HashMap<String, DatabasInfo>();
//		while (enu2.hasMoreElements()) {
//			String key = (String) enu2.nextElement();
//			String[] n = StringUtil.StringToArray(key, ".");
//			if (n == null || n.length != 3)
//				continue;
//			DatabasInfo info = databaNames.get(n[0]);
//			if (info == null) {
//				info = new DatabasInfo();
//				info.setName("yoke_" + n[0]);
//				info.setIp(hostIP);
//			}
//			String value = properties.getProperty(key);
//			if (n[2].equalsIgnoreCase("username")) {
//				value = MPooledDataSource.util.decrypt(value.trim());
//				info.setUser(value);
//			} else if (n[2].equalsIgnoreCase("password")) {
//				value = MPooledDataSource.util.decrypt(value.trim());
//				info.setPwd(value);
//			} else {
//				continue;
//			}
//			databaNames.put(n[0], info);
//		}
//		File saveFile = new File(savePath);
//		if (!saveFile.exists()) {// 如果目录不存在
//			saveFile.mkdirs();// 创建文件夹
//		}
//		if (!savePath.endsWith(File.separator)) {
//			savePath = savePath + File.separator;
//		}
//		StringBuilder stringBuilder = new StringBuilder();
//		for (DatabasInfo info : databaNames.values()) {
//			stringBuilder.setLength(0);
//			stringBuilder.append(rootPath);
//			stringBuilder.append("mysqldump").append(" --opt").append(" -h").append(info.getIp());
//			stringBuilder.append(" --user=").append(info.getUser()).append(" --password=").append(info.getPwd()).append(" --lock-all-tables=true");
//			String fileName = info.getName() + DateUtil.getCurrentTime() + ".sql";
//			stringBuilder.append(" --result-file=").append(savePath + fileName).append(" --defaults-character-set=utf8 ").append(info.getName());
//			ServiceErrorLog logsss = new ServiceErrorLog();
//			try {
//				String restut = CmdExec(stringBuilder.toString());
//				logsss.setName("备份数据");
//				logsss.setType(ServiceErrorLog.SystemInfo);
//				logsss.setMark(restut);
//			} catch (Exception e) {
//				// TODO: handle exception
//				logsss.setMark(StringUtil.fromException(e));
//			} finally {
//				logsss.add();
//			}
//		}
//		// 发布到七牛云
    }

    /**
     * 运行命令
     *
     * @param cmdline cmd
     * @return str
     * @throws IOException io
     * @author jiangzeyin
     */
    public static String CmdExec(String cmdline) throws IOException {
        String line;
        StringBuffer info = new StringBuffer();
        Process p = Runtime.getRuntime().exec(cmdline);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            info.append(line).append(System.lineSeparator());
        }
        input.close();
        return info.toString();
    }
}
