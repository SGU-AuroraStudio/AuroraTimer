package aurora.timer.client.service;

/**
 * Created by hao on 17-1-25.
 */
public class UserOnlineTimeService {
    public void startTimer(String id) {
        TimerYeah yeah = new TimerYeah(id);
        Thread thread = new Thread(yeah, "timer");
        thread.start();
    }
}
