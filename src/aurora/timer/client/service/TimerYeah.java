package aurora.timer.client.service;

import aurora.timer.client.vo.base.Constants;
import aurora.timer.client.vo.base.ServerURL;
import aurora.timer.client.service.util.SmartHttpUtil;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * Created by hao on 17-1-30.
 */
public class TimerYeah {
    //    private String id;
    private static final Logger logger = Logger.getLogger("timer");

//    @Override
//    public void run() {
//        this.isStop = true;
//        logger.info("开始计时");
//        while (isStop) {
//            addTime(this.id);
//            long sleepTime = 5 * 60 * 1000;
//            try {
//                Thread.sleep(sleepTime);
//                logger.fine("是的，我正在计时");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null, "后台计时崩了\n", "提示", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//        logger.info("后台计时结束");
//    }
//
//    public TimerYeah() {
//        this("0");
//    }

//    public TimerYeah(String id) {
//        this.id = id;
//    }

    public static boolean addTime(String id){
        String res;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.TIMER + "?id=" + id + "&ver=" + Constants.locVersion.get("version"), null, null);
            if(res.equals("true")){
                logger.info("上传时间");
                //自动关闭弹窗。如果之前有连接失败过，弹窗了，就把它关了
                if(SmartHttpUtil.dialog!=null) {
                    SmartHttpUtil.dialog.dispose();
                    SmartHttpUtil.dialog=null;
                }
                return true;
            }else if(res.equals("")){
                logger.warning("重连失败");
                return false;
            }else {
                logger.warning("加时返回错误信息：" + res);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
