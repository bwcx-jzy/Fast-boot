package cn.jiangzeyin.util.net.http;

import cn.jiangzeyin.util.util.StringUtil;
import cn.jiangzeyin.util.util.file.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * http 工具类
 *
 * @author jiangzeyin
 */
@SuppressWarnings("deprecation")
public class HttpUtil {

    public static String convertHttpPath(String path) {
        if (StringUtil.isEmpty(path))
            return null;
        if (path.startsWith("//"))
            return String.format("http:%s", path);
        return path;
    }

    /**
     * 下载文件
     *
     * @param fileurl url
     * @param target  target
     * @return boolean
     * @throws IOException              io
     * @throws NoSuchAlgorithmException io
     * @throws KeyStoreException        io
     * @throws KeyManagementException   io
     * @author jiangzeyin
     */
    public static boolean downloadFile(String fileurl, String target) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (StringUtil.isEmpty(fileurl))
            return false;
        fileurl = convertHttpPath(fileurl);
        assert fileurl != null;
        URL url = new URL(fileurl);
        URLConnection urlConnection = url.openConnection();
        int responseCode = 0;
        if (fileurl.startsWith("https://")) {
            return HttpClientUtils.downloadFile(fileurl, target);
        } else if (fileurl.startsWith("http://")) {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            responseCode = httpConn.getResponseCode();
        } else {
            return false;
        }
        if (responseCode == 302) {
            String redictURL = urlConnection.getHeaderField("Location");
            return downloadFile(redictURL, target);
        }
        if (responseCode != 200)
            return false;
        FileUtil.mkdirs(target);
        InputStream is = urlConnection.getInputStream();
        byte[] bs = new byte[1024];
        OutputStream os = new FileOutputStream(target);
        int len = 0;
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        is.close();
        return true;
    }

    /**
     * 简单http 请求get
     *
     * @param pageUrl  url
     * @param encoding encoding
     * @return str
     * @throws Exception e
     * @author jiangzeyin
     */
    public static String doGet(String pageUrl, String encoding) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 构建一URL对象
        URL url = new URL(pageUrl);
        // 使用openStream得到一输入流并由此构造一个BufferedReader对象
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
        String line;
        // 读取www资源
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    /**
     * http 上传文件
     *
     * @param urlStr  url
     * @param textMap map
     * @param fileMap map
     * @return s
     * @throws Exception e
     * @author jiangzeyin
     */
    public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap) throws Exception {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------" + UUID.randomUUID(); // boundary就是request头和上传文件内容的分隔符
        URL url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        // text
        if (textMap != null) {
            StringBuilder strBuf = new StringBuilder();
            for (Entry<String, String> entry : textMap.entrySet()) {
                String inputName = entry.getKey();
                String inputValue = entry.getValue();
                if (inputValue == null) {
                    continue;
                }
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"").append(inputName).append("\"\r\n\r\n");
                strBuf.append(inputValue);
            }
            out.write(strBuf.toString().getBytes());
        }
        // file
        if (fileMap != null) {
            for (Entry<String, String> entry : fileMap.entrySet()) {
                String inputName = (String) entry.getKey();
                String inputValue = (String) entry.getValue();
                if (inputValue == null) {
                    continue;
                }
                File file = new File(inputValue);
                String filename = file.getName();
                String contentType = new MimetypesFileTypeMap().getContentType(file);
                if (contentType == null || contentType.equals("")) {
                    contentType = "application/octet-stream";
                }
                StringBuilder strBuf = new StringBuilder();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"").append(inputName).append("\"; filename=\"").append(filename).append("\"\r\n");
                strBuf.append("Content-Type:").append(contentType).append("\r\n\r\n");
                out.write(strBuf.toString().getBytes());
                DataInputStream in = new DataInputStream(new FileInputStream(file));
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                in.close();
            }
        }
        byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
        out.write(endData);
        out.flush();
        out.close();
        // 读取返回数据
        StringBuilder strBuf = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            strBuf.append(line).append("\n");
        }
        res = strBuf.toString();
        reader.close();
        reader = null;
        conn.disconnect();
        conn = null;
        return res;
    }

    /**
     * @param url     url
     * @param pair    p
     * @param charset charset
     * @return r
     * @throws IOException e
     * @author jiangzeyin
     */
    public static String doPost(String url, List<NameValuePair> pair, String charset) throws IOException {
        /* 建立HTTPPost对象 */
        HttpPost httpRequest = new HttpPost(url);
        /* 添加请求参数到请求对象 */
        if (pair != null && pair.size() > 0) {
            httpRequest.setEntity(new UrlEncodedFormEntity(pair, charset));
        }
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = closeableHttpClient.execute(httpRequest);
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            /* 读返回数据 */
            return EntityUtils.toString(response.getEntity());
        }
        throw new RuntimeException("http " + status + "  " + EntityUtils.toString(response.getEntity()));
    }

    /**
     * post 请求返回jsonobject
     *
     * @param url     url
     * @param pair    p
     * @param charset c
     * @return json
     * @throws IOException io
     * @author jiangzeyin
     */
    public static JSONObject doPostJSONObject(String url, List<NameValuePair> pair, String charset) throws IOException {
        /* 建立HTTPPost对象 */
        HttpPost httpRequest = new HttpPost(url);
        /* 添加请求参数到请求对象 */
        if (pair != null && pair.size() > 0) {
            httpRequest.setEntity(new UrlEncodedFormEntity(pair, charset));
        }
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = closeableHttpClient.execute(httpRequest);
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            return JSON.parseObject(EntityUtils.toString(response.getEntity()));// new JSONObject();
        }
        throw new RuntimeException("http " + status + "  " + EntityUtils.toString(response.getEntity()));
    }

    /**
     * @param url_   url
     * @param encode encode
     * @return r
     * @throws IOException io
     * @author jiangzeyin
     */
    public static String getHtmlContent(String url_, String encode) throws IOException {
        StringBuilder contentBuffer = new StringBuilder();
        int responseCode = -1;
        HttpURLConnection con = null;
        URL url = new URL(url_);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");// IE代理进行下载
        con.setConnectTimeout(60000);
        con.setReadTimeout(60000);
        con.setInstanceFollowRedirects(true);
        // 获得网页返回信息码
        responseCode = con.getResponseCode();
        if (responseCode == 302) {
            String l = con.getHeaderField("Location");
            return getHtmlContent(l, encode);
            // System.out.println(l);
        }
        //System.out.println(responseCode);
        if (responseCode == -1) {
            con.disconnect();
            return null;
        }
        if (responseCode >= 400) { // 请求失败
            con.disconnect();
            return null;
        }
        InputStream inStr = con.getInputStream();
        InputStreamReader istreamReader = new InputStreamReader(inStr, encode);
        BufferedReader buffStr = new BufferedReader(istreamReader);
        String str = null;
        while ((str = buffStr.readLine()) != null)
            contentBuffer.append(str);
        inStr.close();
        con.disconnect();
        return contentBuffer.toString();
    }

}
