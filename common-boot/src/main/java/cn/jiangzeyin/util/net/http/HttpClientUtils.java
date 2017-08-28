package cn.jiangzeyin.util.net.http;

import cn.jiangzeyin.util.util.StringUtil;
import cn.jiangzeyin.util.util.file.FileUtil;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求工具类
 *
 * @author huaieli
 */
public class HttpClientUtils {
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    /**
     * 发送HTTP POST请求,支持带多个String参数
     *
     * @param url      链接
     * @param paramMap 参数
     * @return str
     * @throws Exception e
     */
    public static String sendHttpPost(String url, Map<String, String> paramMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        return sendHttpPost(url, paramMap, httpclient);
    }

    /**
     * 发送HTTP POST请求,支持多个参数(注：多个参数需拼接)
     *
     * @param url    链接
     * @param params 参数(格式 key1%3Dvalue1%26key2%3Dvalue22)
     * @return str
     * @throws Exception e
     */
    public static String sendHttpPost(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        return sendHttpPost(url, params, httpclient);
    }

    /**
     * 发送HTTP POST请求,支持带一个文件参数
     *
     * @param url  链接
     * @param file 文件
     * @return str
     * @throws Exception e
     */
    public static String sendHttpPost(String url, File file) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);

            InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);

            httpPost.setEntity(reqEntity);

            return sendHttpPost(httpPost, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP POST请求(客户端采用二进制流发送,服务端采用二进制流接收)
     *
     * @param url              链接
     * @param binaryStreamsStr 参数
     * @return str
     * @throws Exception e
     */
    public static String sendHttpPostByStream(String url, String binaryStreamsStr) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);

            HttpEntity reqEntity = new ByteArrayEntity(binaryStreamsStr.getBytes(Consts.UTF_8), ContentType.APPLICATION_JSON);

            httpPost.setEntity(reqEntity);

            return sendHttpPost(httpPost, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送POST请求,支持带多个String参数和多个文件参数
     *
     * @param url     链接
     * @param paraMap 参数集合
     * @param fileMap 文件集合
     * @return str
     * @throws Exception e
     */
    public static String sendHttpPostByFile(String url, Map<String, String> paraMap, Map<String, File> fileMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (String key : paraMap.keySet()) {
                builder.addPart(key, new StringBody(paraMap.get(key), ContentType.TEXT_PLAIN));
            }
            for (String fileStr : fileMap.keySet()) {
                builder.addPart(fileStr, new FileBody(fileMap.get(fileStr)));
            }

            HttpEntity reqEntity = builder.build();

            httpPost.setEntity(reqEntity);

            return sendHttpPost(httpPost, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP GET请求,不带参数(注：可将参数加在url后面)
     *
     * @param url 链接
     * @return str
     * @throws Exception e
     */
    public static String sendHttpGet(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        try {
            return sendHttpGet(url, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP GET请求,不带参数,返回byte数组
     *
     * @param url 链接
     * @return byte
     * @throws Exception e
     */
    public static byte[] sendHttpGetResByte(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        try {
            return sendHttpGetResByte(url, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP GET请求,支持多个参数(注：多个参数需拼接)
     *
     * @param url    链接
     * @param params 参数(格式:key1%3Dvalue1%26key2%3Dvalue2)
     * @return str
     * @throws Exception e
     */
    public static String sendHttpGet(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        return sendHttpGet(url, params, httpclient);
    }

    /**
     * 发送HTTPS GET请求,支持多个参数(注：多个参数需拼接)
     *
     * @param url    链接
     * @param params 参数(格式:key1%3Dvalue1%26key2%3Dvalue2)
     * @return str
     * @throws Exception e
     */
    public static String sendHttpsGet(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpsClient(url);
        return sendHttpGet(url, params, httpclient);
    }

    /**
     * 发送HTTPS GET请求,不带参数(注：可将参数加在url后面)
     *
     * @param url 链接
     * @return str
     * @throws Exception e
     */
    public static String sendHttpsGet(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpsClient(url);
        try {
            return sendHttpGet(url, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTPS POST请求,支持带多个String参数
     *
     * @param url      链接
     * @param paramMap 参数
     * @return str
     * @throws Exception e
     */
    public static String sendHttpsPost(String url, Map<String, String> paramMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpsClient(url);
        return sendHttpPost(url, paramMap, httpclient);
    }

    /**
     * 发送HTTPS POST请求,支持多个参数(注：多个参数需拼接)
     *
     * @param url    链接
     * @param params 参数(格式:key1%3Dvalue1%26key2%3Dvalue2)
     * @return str
     * @throws Exception e
     */
    public static String sendHttpsPost(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClientUtils.getHttpsClient(url);
        return sendHttpPost(url, params, httpclient);
    }

    /**
     * 发送HTTP GET请求
     */
    private static String sendHttpGet(String url, String params, CloseableHttpClient httpclient) throws Exception {
        try {
            String sb = url +
                    "?" +
                    params;

            return sendHttpGet(sb, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP POST请求
     *
     * @param httpclient cl
     * @param paramMap   map
     * @param url        ur;
     * @return str
     * @throws Exception e
     */
    private static String sendHttpPost(String url, Map<String, String> paramMap, CloseableHttpClient httpclient) throws Exception {
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();

            for (String key : paramMap.keySet()) {
                nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            return sendHttpPost(httpPost, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送HTTP POST请求
     *
     * @param url        url
     * @param httpclient c
     * @return str
     * @throws Exception e
     */
    private static String sendHttpPost(String url, String params, CloseableHttpClient httpclient) throws Exception {
        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new StringEntity(params, Consts.UTF_8));

            return sendHttpPost(httpPost, httpclient);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 获取HttpClient
     *
     * @return closeablehttpclient
     * @throws KeyManagementException   key
     * @throws NoSuchAlgorithmException key
     * @throws KeyStoreException        key
     */
    private static CloseableHttpClient getHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 信任所有
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                (chain, authType) -> true).build();
        // ALLOW_ALL_HOSTNAME_VERIFIER:这个主机名验证器基本上是关闭主机名验证的,实现的是一个空操作，
        // 并且不会抛出javax.net.ssl.SSLException异常。
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }

    /**
     * 获取HTTPS HttpClient
     *
     * @param url url
     * @return closeableHttpClient
     * @throws Exception e
     */
    private static CloseableHttpClient getHttpsClient(String url) throws Exception {
        PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(new URL(url));
        DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLHostnameVerifier(hostnameVerifier)
                .build();

        return httpclient;
    }

    /**
     * 发送HTTP POST请求
     *
     * @param httpclient client
     * @param httpPost   post
     * @return str
     * @throws Exception e
     */
    private static String sendHttpPost(HttpPost httpPost, CloseableHttpClient httpclient) throws Exception {
        httpPost.setConfig(requestConfig);
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Consts.UTF_8);
        }
    }

    /**
     * 发送HTTP GET请求
     *
     * @param httpclient client
     * @param url        url
     * @return str
     * @throws Exception e
     */
    private static String sendHttpGet(String url, CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Consts.UTF_8);
        }
    }

    /**
     * 发送HTTP GET请求
     *
     * @param httpclient client
     * @param url        url
     * @return byte[]
     * @throws Exception e
     */
    private static byte[] sendHttpGetResByte(String url, CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toByteArray(entity);
        }
    }

    /**
     * @param url    url
     * @param target target
     * @return boolean
     * @throws IOException              io
     * @throws NoSuchAlgorithmException io
     * @throws KeyStoreException        io
     * @throws KeyManagementException   io
     */
    public static boolean downloadFile(String url, String target) throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        if (!StringUtil.convertNULL(url).startsWith("https://"))
            return false;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        CloseableHttpResponse response = httpclient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200)
            return false;
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        FileUtil.mkdirs(target);
        byte[] bs = new byte[1024];
        OutputStream os = new FileOutputStream(target);
        int len = 0;
        while ((len = inputStream.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        inputStream.close();
        return true;
    }

    public static String post(String url, List<NameValuePair> pairList) throws Exception {
        if (!StringUtil.convertNULL(url).startsWith("https://"))
            return null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(pairList));
        httpPost.setConfig(requestConfig);
        CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
        CloseableHttpResponse response = httpclient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200)
            return null;
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

}
