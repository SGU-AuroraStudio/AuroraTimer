package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.vo.UserOnlineTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by hao on 17-1-25.
 */
public class UserOnlineTimeService {
//    public TimerYeah startTimer(String id) {
//        TimerYeah yeah = new TimerYeah(id);
//        Thread thread = new Thread(yeah, "timer");
//        thread.start();
//        return yeah;
//    }

    public Vector<UserOnlineTime> getLastXWeekTime(int lastXWeek) {
        Vector<UserOnlineTime> voVector = new Vector<>();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ServerURL.THIS_WEEK_TIME + "?x=" + lastXWeek);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("accept", "application/json"); //向服务器表示我要的是json
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"GBK")); //修改编码，解决“鹏，濠”字乱码。需要在服务端设置resp为GBK，在Main2Form里的填入表格那也需要改
            StringBuffer buffer = new StringBuffer("");
            String temp;
            while ((temp = reader.readLine()) != null) {
                buffer.append(temp);
            }
            reader.close();
            connection.disconnect();
            JSONObject object = (JSONObject) JSONValue.parse(buffer.toString());
            Set<String> keys = object.keySet(); //获取键集合
            Iterator<String> keyIt = keys.iterator();
            UserOnlineTime vo;
            JSONObject oTemp;
            while (keyIt.hasNext()) {
                vo = new UserOnlineTime();
                oTemp = (JSONObject) object.get(keyIt.next());
                vo.setID((String) oTemp.get("id"));
                vo.setTodayOnlineTime(Long.decode((String) oTemp.get("time")));
                vo.setTermOnlineTime(Long.parseLong((String) oTemp.get("termTime")));
                vo.setName((String) oTemp.get("name"));
                voVector.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voVector;
    }

}
