package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.vo.UserData;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;

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
            URL url = new URL(ServerURL.LOGINURL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.connect();

            JSONObject object = new JSONObject();
            object.put("id",vo.getID());
            object.put("pwd",vo.getPassWord());

            OutputStream out = connection.getOutputStream();
            out.write(object.toJSONString().getBytes());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = reader.readLine();
            reader.close();

            connection.disconnect();
            flag = s.equals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main (String args[]) {
        UserDataService service = new UserDataService();
        UserData vo = new UserData();
        vo.setID("15115072044");
        String pwd = DigestUtils.md5Hex("123456").toString();
        System.out.println(pwd);
        vo.setPassWord(pwd);
        service.LoginService(vo);
    }
}
