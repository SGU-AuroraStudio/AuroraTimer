package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hao on 17-1-25.
 */
public class UserDataService {
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
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

}
