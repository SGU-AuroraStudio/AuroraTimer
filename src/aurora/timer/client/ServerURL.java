package aurora.timer.client;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-1-25.
 */
public class ServerURL {
    //服务器的地址加端口
//    public static final String HOST = "192.168.1.17:8090";
    //服务器外网地址
//    public static final String HOST = "47.107.77.200:8080";
    public static String HOST;
    static {
//        Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
//        ServerURL.HOST = preferences.get("host", "47.107.77.200:8080");
//        HOST = "192.168.1.218:8080";
        HOST = "127.0.0.1:8080";
//        HOST = "47.99.134.104:8083";

    }

//    public static final String HOST = "127.0.0.1:8080";
    //跳转的登陆页面，在页面完成注册
    public static String REGISTER_URL = "http://" + HOST + "/timer/pages/register.html";
    //登陆验证的url
    public static String LOGIN_URL = "http://" + HOST + "/timer/login";
    //注册表地址
    public static final String PRE_PATH = "com.aurora.timer";
    //计时地址
    public static String TIMER = "http://" + HOST + "/timer/timer";
    //管理员地址
    public static String ADMIN = "http://" + HOST + "/timer/admin";

    public static final String ABOUT = "———— All Hai Lelouch! ————";
    //获取本周时间的地址
    public static String THIS_WEEK_TIME = "http://" + HOST + "/timer/lastXWeek";
    //获取学期时间的地址
    public static String THIS_TERM_TIME = "http://" + HOST + "/timer/termTime";
    //获取UserDataById
    public static String FIND_BY_ID = "http://" + HOST + "/timer/findById";
    //获取插件的文件夹
    public static final String PLUGIN_HOME = "res" + File.pathSeparator + "plugins";
    //背景图片的文件夹(作废，打包成jar后访问jar内部资源的方式不一样)
    ///public static String BG_PATH = "res" + File.separator + "bg1.png";
    //背景图片地址
    public static String BG = "http://" + HOST + "/timer/bg";
    //检查新版本
    public static String CHECK_VERSION_URL = "http://" + HOST + "/timer/soft/ver.json";
    //    public static final String CHECK_VERSION_URL = "http://" + HOST + "/timer/version";
    //获得软件本体的路径
    public static String SOFT_URL = "http://" + HOST + "/timer/soft"; // + TimerX.X.jar
}
