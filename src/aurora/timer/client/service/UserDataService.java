package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.view.until.MultipartUtility;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by hao on 17-1-25.
 */
public class UserDataService {
    private static Logger logger = Logger.getLogger("UserDataService");

    public boolean LoginService(UserData vo) {
        HttpURLConnection connection = null;
        boolean flag = false;
        try {
            URL url = new URL(ServerURL.LOGIN_URL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json"); //向服务器表示我传的是json
            connection.connect();

            JSONObject object = new JSONObject();
            object.put("id", vo.getID());
            object.put("pwd", vo.getPassWord());

            OutputStream out = connection.getOutputStream();
            out.write(object.toJSONString().getBytes());
            out.flush();
            out.close();

            //这里只会返回一行字符串"true"或者"false"
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); //注意：到了这行代码才会发送请求
            String s = reader.readLine();
            reader.close();

            connection.disconnect();
            flag = s.equals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public JSONObject findById(String id) {
        HttpURLConnection connection = null;
        JSONObject object = null;
        try {
            URL url = new URL(ServerURL.FIND_BY_ID + "?id=" + id);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer("");
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();

            object = (JSONObject) JSONValue.parse(sb.toString());

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.err.println(ex);
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
            connection.setReadTimeout(3000);
            connection.connect();
            bg = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bg;
    }

}
