package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.view.util.MultipartUtility;
import aurora.timer.client.view.util.SmartHttpUtil;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by hao on 17-1-25.
 */
//TODO:用SmartHttpUtil重写
public class UserDataService {
    private static Logger logger = Logger.getLogger("UserDataService");

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

    public JSONObject findById(String id){
        String res;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.FIND_BY_ID + "?id=" + id, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        JSONObject object = (JSONObject) JSONValue.parse(res);
        return object;
    }

    public boolean uploadBg(String id, String password, InputStream bg) {
        HttpURLConnection connection = null;
        boolean flag = false;
        try {
            // 一个现成的工具类，用multipart/form-data同时传id和图片的inpustream给服务器
            MultipartUtility multipart = new MultipartUtility(ServerURL.BG, "UTF-8");
            multipart.addFormField("id", id);
            multipart.addFormField("password", password);
            multipart.addFilePart(id + "_bg", bg);
            List<String> response = multipart.finish();
//            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                flag = line.equals("true");
//                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (flag) {
            logger.info("上传背景图片成功");
        } else {
            logger.warning("上传背景图片失败");
        }
        return flag;
    }

    public InputStream findBgByid(String id, String password) {
        HttpURLConnection connection = null;
        InputStream bg = null;
        try {
            URL url = new URL(ServerURL.BG+"?id="+id+"&password="+password);
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
