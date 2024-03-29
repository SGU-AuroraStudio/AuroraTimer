package aurora.timer.client.service.util;

//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
import aurora.timer.client.vo.base.ServerURL;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:[ HttpUtils ]
 * @Author: tarzan Liu
 * @Company: 洛阳图联科技有限公司
 * @Date: 2019/10/31 9:11
 *
 */
//TODO:设置连接超时
public class SmartHttpUtil {
    public static String JSESSIONID_COOKIE=""; //格式是JSESSIONID=xxxxxx
    private static boolean canShowDialog = true; //防止多次弹窗
    public static JDialog dialog;
    /**
     * 方法描述: 发送get请求
     *
     * @param url
     * @param params
     * @param header
     * @Return {@link String}
     * @throws
     * @date 2020年07月27日 09:10:10
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        HttpGet httpGet = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<String> mapList = new ArrayList<>();
            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    mapList.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            if (mapList.size() > 0) {
                url = url + "?";
                String paramsStr = String.join("", mapList);
                url = url + paramsStr;
            }
            httpGet = new HttpGet(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpGet.setConfig(config);
            httpGet.setHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //带上cookie
            httpGet.addHeader("Cookie",JSESSIONID_COOKIE);
            if (header != null) {
                for (Entry<String, String> entry : header.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                throw new RuntimeException("连接服务器失败\n请求失败"+statusCode);
            } else {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            if (url.contains(ServerURL.TIMER) && !body.equals("true")){
                throw new RuntimeException("连接服务器成功，但是上传时间失败\n" + body);
            }
        } catch (Exception e) {
            //防止多次弹窗
            if(canShowDialog) {
                //多线程弹窗，不阻塞线程
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        canShowDialog=false;
                        //不用showMessageDialog，用自定义弹窗，创建全局变量，重连上就在TimeYear自动关掉该弹窗
                        JOptionPane jOptionPane = new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                        dialog = jOptionPane.createDialog(null,"错误");
                        dialog.show();
//                        JOptionPane.showMessageDialog(null, "连接服务器失败\n", "提示", JOptionPane.ERROR_MESSAGE);
                        canShowDialog=true;
                    }
                });
                throw e;
            }

        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return body;
    }

    /**
     * 方法描述: 发送post请求-json数据
     *
     * @param url
     * @param json
     * @param header
     * @Return {@link String}
     * @throws
     * @date 2020年07月27日 09:10:54
     */
    public static String sendPostJson(String url, String json, Map<String, String> header) throws Exception {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpPost.setConfig(config);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //带上cookie
            httpPost.addHeader("Cookie", JSESSIONID_COOKIE);
            if (header != null) {
                for (Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                throw new RuntimeException("请求失败"+statusCode);
            } else {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "连接服务器失败\n"+e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return body;
    }

    //TODO:新写的方法，别的地方有旧的方法没删，有点乱。
    /**
     * 方法描述: 发送post请求-form表单数据
     *
     * @param url
     * @param header
     * @Return {@link String}
     * @throws
     * @date 2020年07月27日 09:10:54
     */
    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
            httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpPost.setConfig(config);
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //带上cookie
            httpPost.addHeader("Cookie", JSESSIONID_COOKIE);
            if (header != null) {
                for (Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            List<NameValuePair> nvps = new ArrayList<>();
            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);

            //看看有没有set-cookie
            Header[] responseHeaders = response.getHeaders("Set-Cookie");
            if(responseHeaders.length>0) {
                JSESSIONID_COOKIE = "JSESSIONID=" + responseHeaders[0].getElements()[0].getValue();
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                throw new RuntimeException("请求失败"+statusCode);
            } else {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "连接服务器失败\n"+e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return body;
    }

    public static String sendPostMultipart(String url, Map<String, String> params, Map<String, String> header, List<File> files) throws Exception {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpPost.setConfig(config);
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //设置header
            httpPost.addHeader("Cookie", JSESSIONID_COOKIE);
            if (header != null) {
                for (Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            //添加params
            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
                }
            }
            //添加files
            if(files!=null) {
                for (File file : files) {
                    builder.addBinaryBody(
                            "file",
                            file,
                            ContentType.parse(URLConnection.guessContentTypeFromName(file.getName())),
                            file.getName()
                    );
//                    builder.setMimeSubtype(URLConnection.guessContentTypeFromName(file.getName()));
                }
            }

            //设置参数到请求对象中
            String boundary = "===" + System.currentTimeMillis() + "===";
            builder.setBoundary(boundary);
            httpPost.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
            httpPost.setEntity(builder.build());

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                throw new RuntimeException("请求失败"+statusCode);
            } else {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "连接服务器失败\n"+e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
            throw e;
        }finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return body;
    }

}