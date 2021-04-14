package aurora.timer.client.vo.base;

import aurora.timer.client.service.TimerYeah;

import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * @Author Yao
 * @Date 2021/4/12 0:07
 * @Description
 */
public class Constants {
    public static final int backAddTimeDelay = 5 * 60 * 1000;
    public static final int checkTimerDelay = 24 * 60 * 1000;
    public static Preferences preferences = null;
    public static final Properties locVersion = new Properties();
    static {
        try {
            locVersion.load(TimerYeah.class.getResourceAsStream("/aurora/timer/client/view/version/version.properties"));
            preferences= Preferences.userRoot().node(ServerURL.PRE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
