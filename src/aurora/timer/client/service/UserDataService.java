package aurora.timer.client.service;

import aurora.timer.client.vo.base.ServerURL;
import aurora.timer.client.service.util.SmartHttpUtil;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by hao on 17-1-25.
 */
public class UserDataService {
    private static Logger logger = Logger.getLogger("UserDataService");

    /**
     * 登录
     * @param vo 账号密码
     * @return boolean
     */
    public boolean LoginService(UserData vo) {
        Map<String,String> params = new HashMap<>();
        params.put("id", vo.getID());
        params.put("password", vo.getPassWord()); //以前是pwd
        String res;
        try {
            res = SmartHttpUtil.sendPostForm(ServerURL.LOGIN_URL, params, null);
//            res = SmartHttpUtil.sendPostJson(ServerURL.LOGIN_URL, JSONObject.toJSONString(params), null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return res.equals("true");
    }

    /**
     * 根据id查找用户信息，包括计时
     * @param id id
     * @return JSONObject
     */
    public JSONObject findById(String id){
        String res;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.FIND_BY_ID + "?id=" + id, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return (JSONObject) JSONValue.parse(res);
    }

    public boolean uploadBg(File bg) {
        boolean flag = false;
        String res;
        try {
            LinkedList<File> files = new LinkedList<>();
            files.add(bg);
            res = SmartHttpUtil.sendPostMultipart(ServerURL.BG, null, null, files);
        } catch (Exception ex) {
            ex.printStackTrace();
            //弹窗让SmartHttpUtil弹了，这里就不弹了
            return false;
        }
        return res.equals("true");
    }

    /**
     * 根据url下载图片，网络图片也可以，要求路径是文件
     * @param urlStr
     * @return
     */
    public InputStream getBg(String urlStr) {
        HttpURLConnection connection;
        InputStream bg = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(10000);
            //带上cookie
            connection.setRequestProperty("Cookie", SmartHttpUtil.JSESSIONID_COOKIE);
            connection.connect();
            bg = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bg;
    }

}
