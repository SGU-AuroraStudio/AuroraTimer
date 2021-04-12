package aurora.timer.client.service;

import aurora.timer.client.vo.base.ServerURL;
import aurora.timer.client.view.util.SmartHttpUtil;
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
//TODO:用SmartHttpUtil重写
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
        params.put("password", vo.getPassWord());
        String res;
        try {
            res = SmartHttpUtil.sendPostForm(ServerURL.LOGIN_URL, params, null);
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
            // 一个现成的工具类，用multipart/form-data同时传id和图片的inpustream给服务器
//            MultipartUtility multipart = new MultipartUtility(ServerURL.BG, "UTF-8");
//            multipart.addFormField("id", id);
//            multipart.addFormField("password", password);
//            multipart.addFilePart("file", bg);
//            List<String> response = multipart.finish();
            LinkedList<File> files = new LinkedList<>();
            files.add(bg);
            res = SmartHttpUtil.sendPostMultipart(ServerURL.BG, null, null, files);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        if (res.equals("true")) {
            logger.info("上传背景图片成功");
            JOptionPane.showMessageDialog(null, "上传成功\n"+res, "提示", JOptionPane.ERROR_MESSAGE);
        } else {
            logger.warning("上传背景图片失败");
            JOptionPane.showMessageDialog(null, "上传失败\n"+res, "提示", JOptionPane.ERROR_MESSAGE);
        }
        return res.equals("true");
    }

    public InputStream getBg(String urlStr) {
        HttpURLConnection connection = null;
        InputStream bg = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(10000);
            connection.connect();
            bg = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bg;
    }

}
