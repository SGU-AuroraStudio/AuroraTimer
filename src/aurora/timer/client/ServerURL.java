package aurora.timer.client;

/**
 * Created by hao on 17-1-25.
 */
public class ServerURL {
    //服务器的地址加端口
    public static final String HOST = "192.168.1.17:8090";
//    public static final String HOST = "210.38.206.110:8090";
//    public static final String HOST = "127.0.0.1:8080";
    //跳转的登陆页面，在页面完成注册
    public static final String REGISTERURL = "http://" + HOST + "/timer/pages/register.html";
    //登陆验证的url
    public static final String LOGINURL = "http://" + HOST + "/timer/login";
    //注册表地址
    public static final String PREPATH = "com.aurora.timer";
    //计时地址
    public static final String TIMER = "http://" + HOST + "/timer/timer";
    //别问我为什么在这里放字，任性！～
    public static final String ABOUT = "———— All Hai Lelouch! ————";
    //获取本周时间的地址
    public static final String THISWEEKTIME = "http://" + HOST + "/timer/thisWeek";
    //获取UserDataById
    public static final String FINDBYID = "http://" + HOST + "/timer/findById";
}
