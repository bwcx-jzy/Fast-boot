package cn.jiangzeyin.util.util.images;

import cn.jiangzeyin.util.util.RandomUtil;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片操作工具类
 *
 * @author jiangzeyin
 */
public final class ImageUtil {

    /**
     * 生成二维码
     *
     * @return r
     * @throws IOException io
     * @author jiangzeyin
     */
    public static Object[] getRandomCode() throws IOException {
        int width = 90;// 定义图片的width
        int height = 35;// 定义图片的height
        int codeCount = 4;// 定义图片上显示验证码的个数
        int xx = 15;
        int fontHeight = 35;
        int codeY = 30;
        Object[] obj = new Object[2];
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Graphics2D gd = buffImg.createGraphics();
        // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();

        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.WHITE);
        gd.drawRect(0, 0, width - 1, height - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            int x = RandomUtil.rand.nextInt(width);
            int y = RandomUtil.rand.nextInt(height);
            int xl = RandomUtil.rand.nextInt(12);
            int yl = RandomUtil.rand.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf((char) RandomUtil.getCodeAscll());
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = RandomUtil.rand.nextInt(180);
            green = RandomUtil.rand.nextInt(200);
            blue = RandomUtil.rand.nextInt(150);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            if (i == 0)
                gd.drawString(code, 5, codeY);
            else
                gd.drawString(code, (i + 1) * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        obj[0] = buffImg;
        obj[1] = randomCode.toString();
        return obj;
    }

    /**
     * 判断图片是否可以正常打开
     *
     * @param path path
     * @return boolean
     */
    public static boolean ImageIsTrue(String path) {
        try {
            BufferedImage bi = ImageIO.read(new File(path));
            if (bi == null)
                return false;
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取图片信息
     *
     * @param imgUrl url
     * @return r
     * @throws IOException io
     * @author jiangzeyin
     */
    public static BufferedImage getBufferedImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        if (con.getResponseCode() == 302) {
            String redictURL = con.getHeaderField("Location");
            return getBufferedImage(redictURL);
            // con = (HttpURLConnection) new URL(redictURL).openConnection();
        }
        InputStream is = url.openStream();
        BufferedImage img = ImageIO.read(is);
        is.close();
        con.disconnect();
        return img;
    }

    /**
     * 下载图片
     *
     * @param imgUrl  url
     * @param imgdist d
     * @param width   w
     * @param height  h
     * @return boolean
     * @throws IOException io
     * @author jiangzeyin
     */
    public static boolean downloadImageUrl(String imgUrl, String imgdist, int width, int height) throws IOException {
        BufferedImage image = getBufferedImage(imgUrl);
        if (width == -1 || height == -1)
            return writeImage(image, imgdist);
        reduceImg(image, imgdist, width, height);
        return true;
    }

    /**
     * 下载图片
     *
     * @param imgUrl  url
     * @param imgdist i
     * @return boolean
     * @throws IOException io
     * @author jiangzeyin
     */
    public static boolean downloadImageUrl(String imgUrl, String imgdist) throws IOException {
        return downloadImageUrl(imgUrl, imgdist, -1, -1);
    }

    /**
     * 压缩图片
     *
     * @param src        src
     * @param imgdist    d
     * @param widthdist  w
     * @param heightdist h
     * @throws ImageFormatException fe
     * @throws IOException          io
     * @author jiangzeyin
     */
    public static void reduceImg(BufferedImage src, String imgdist, int widthdist, int heightdist) throws ImageFormatException, IOException {
        BufferedImage tag = new BufferedImage((int) widthdist, (int) heightdist, BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null);
        FileOutputStream out = new FileOutputStream(imgdist);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(tag);
        out.close();
    }

    /**
     * 写图片
     *
     * @param image    img
     * @param fileName fileName
     * @return boolean
     * @throws IOException io
     * @author jiangzeyin
     */
    public static boolean writeImage(BufferedImage image, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        ImageIO.write(image, "JPEG", file);
        return true;
    }

    /**
     * 压缩图片(保持图片比例)
     *
     * @param originalFile 原图片
     * @param resizedFile  目标图片
     * @param newWidth     新的宽度
     * @param quality      压缩率
     * @throws IOException io
     */
    public static void resize(File originalFile, File resizedFile, int newWidth, float quality) throws IOException {
        if (quality > 1) {
            throw new IllegalArgumentException("Quality has to be between 0 and 1");
        }

        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());
        Image i = ii.getImage();
        Image resizedImage = null;

        int iWidth = i.getWidth(null);
        int iHeight = i.getHeight(null);

        if (iWidth > iHeight) {
            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);
        } else {
            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);
        }

        // This code ensures that all the pixels in the image are loaded.
        Image temp = new ImageIcon(resizedImage).getImage();

        // Create the buffered image.
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Copy image to buffered image.
        Graphics g = bufferedImage.createGraphics();

        // Clear background and paint the image.
        g.setColor(Color.white);
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
        g.drawImage(temp, 0, 0, null);
        g.dispose();

        // Soften.
        float softenFactor = 0.05f;
        float[] softenArray = {0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0};
        Kernel kernel = new Kernel(3, 3, softenArray);
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bufferedImage = cOp.filter(bufferedImage, null);

        // Write the jpeg to a file.
        FileOutputStream out = new FileOutputStream(resizedFile);

        // Encodes image as a JPEG data stream
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);

        param.setQuality(quality, true);

        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufferedImage);
    } // Example usage

    public static int getWidth(String path) throws IOException {
        File picture = new File(path);
        if (!picture.exists())
            throw new IllegalArgumentException(path + " 不存在");
        BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
        //System.out.println(String.format("%.1f", picture.length() / 1024.0));
        return sourceImg.getWidth();
        //System.out.println(sourceImg.getHeight());
    }

    public static int getHeight(String path) throws IOException {
        File picture = new File(path);
        if (!picture.exists())
            throw new IllegalArgumentException(path + " 不存在");
        BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
        //System.out.println(String.format("%.1f", picture.length() / 1024.0));
        //return sourceImg.getWidth();
        return sourceImg.getHeight();
    }

    public static boolean checkPx(String path, int width, int height) throws IOException {
        File picture = new File(path);
        if (!picture.exists())
            throw new IllegalArgumentException(path + " 不存在");
        return checkPx(new FileInputStream(picture), width, height);
    }

    public static boolean checkPx(InputStream inputStream, int width, int height) throws IOException {
        if (inputStream == null)
            throw new IllegalArgumentException("is null");
        BufferedImage sourceImg = ImageIO.read(inputStream);
        if (sourceImg.getWidth() != width)
            return false;
        if (sourceImg.getHeight() != height)
            return false;
        return true;
    }

}
