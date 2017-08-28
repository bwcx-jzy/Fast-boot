package cn.jiangzeyin.util;

import cn.jiangzeyin.system.log.LogType;
import cn.jiangzeyin.system.log.SystemLog;
import cn.jiangzeyin.util.util.images.ImageUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/17.
 */
public class RandomCodeUtil {
    /**
     * @param session  session
     * @param response res
     * @param name     name
     */
    public static void drawRandomCode(HttpSession session, HttpServletResponse response, String name) {
        try {
            Object[] obj = ImageUtil.getRandomCode();
            // 将四位的验证码保存到Session中。
            session.setAttribute(name, obj[1].toString());
            // 禁止图像缓存。
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            // 将图像输出到Servlet输出流中。
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write((BufferedImage) obj[0], "jpeg", sos);
        } catch (IOException e) {
            SystemLog.LOG(LogType.CONTROL_ERROR).error("生成二维码失败", e);
        }
    }
}
