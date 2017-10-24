//package cn.jiangzeyin.util.net.ftp;
//
//import cn.jiangzeyin.util.system.util.UtilSystemCache;
//import org.apache.commons.net.ftp.FTPClient;
//
//import java.io.IOException;
//
///**
// * 自定义有序ftp 连接
// *
// * @author jiangzeyin
// */
//public class YokeFtp extends FTPClient {
//    private int order;
//    private int schemeId;
//    private int cfileType;
//
//    public int getCfileType() {
//        return cfileType;
//    }
//
//    public void setCfileType(int cfileType) {
//        this.cfileType = cfileType;
//    }
//
//    public int getSchemeId() {
//        return schemeId;
//    }
//
//    public void setSchemeId(int schemeId) {
//        this.schemeId = schemeId;
//    }
//
//    public int getOrder() {
//        return order;
//    }
//
//    public void setOrder(int order) {
//        this.order = order;
//    }
//
//    /**
//     * @param fileType file
//     * @return boolean
//     * @author jiangzeyin
//     */
//    @Override
//    public boolean setFileType(int fileType) {
//        // TODO Auto-generated method stub
//        try {
//            if (fileType == cfileType)
//                return true;
//            setCfileType(fileType);
//            return super.setFileType(fileType);
//        } catch (IOException e) {
//            // TODO: handle exception
//            UtilSystemCache.getInstance().LOG_ERROR().error("fileType 失败", e);
//            setCfileType(-1);
//            return false;
//        }
//    }
//
//
//}
