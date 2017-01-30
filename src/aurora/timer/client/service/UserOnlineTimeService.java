package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.vo.UserOnlineTime;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by hao on 17-1-25.
 */
public class UserOnlineTimeService {
    public void startTimer(String id) {
        TimerYeah yeah = new TimerYeah(id);
        Thread thread = new Thread(yeah, "timer");
        thread.start();
    }

    public Vector<UserOnlineTime> getThisWeekTime() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ServerURL.THISWEEKTIME);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json"); //向服务器表示我传的是json
            connection.connect();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
