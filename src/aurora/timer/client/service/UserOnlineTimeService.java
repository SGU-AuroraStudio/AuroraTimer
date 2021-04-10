package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.view.util.SmartHttpUtil;
import aurora.timer.client.vo.UserOnlineTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by hao on 17-1-25.
 */
//TODO:用SmartHttpUtil重写
public class UserOnlineTimeService {

    public List<UserOnlineTime> getLastXWeekTime(int lastXWeek) {
        List<UserOnlineTime> list = new ArrayList<>();
        String res = null;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.THIS_WEEK_TIME + "?x=" + lastXWeek, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        JSONObject object = (JSONObject) JSONValue.parse(res);
        Set<String> keys = object.keySet();
        for (String key : keys) {
            UserOnlineTime vo = new UserOnlineTime();
            JSONObject oTemp = (JSONObject) object.get(key);
            vo.setID((String) oTemp.get("id"));
            vo.setTodayOnlineTime((Long)oTemp.get("time"));
            vo.setTermOnlineTime((Long) oTemp.get("termTime"));
            vo.setName((String) oTemp.get("name"));
            list.add(vo);
        }
        return list;
    }

}
