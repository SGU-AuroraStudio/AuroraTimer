package aurora.timer.client.service;

import aurora.timer.client.ServerURL;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by hao on 17-1-30.
 */
public class TimerYeah implements Runnable {
    private String id;
    public boolean isStop;
    private static Logger logger = Logger.getLogger("timer");

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.isStop = true;
        logger.info("开始计时");
        while (isStop) {
            addTime(this.id);
            long sleepTime = 5 * 60 * 1000;
            try {
                Thread.sleep(sleepTime);
                logger.fine("是的，我正在计时");
            } catch (InterruptedException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "夭寿了，后台计时居然崩了！快叫周浩过来找bug！\n" +
                        ServerURL.REGISTER_URL, "提示", JOptionPane.ERROR_MESSAGE);
            }
        }
        logger.info("后台计时结束");
    }

    public TimerYeah() {
        this("0");
    }

    public TimerYeah(String id) {
        this.id = id;
    }

    public static boolean addTime(String id) {
        boolean flag = false;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ServerURL.TIMER + "?id=" + id);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String req = reader.readLine();

            if (req.equals("true")) {
                flag = true;
                logger.info("上传时间");
            } else {
                JOptionPane.showMessageDialog(null, "上传时间失败。。。\n" +
                        ServerURL.REGISTER_URL, "提示", JOptionPane.ERROR_MESSAGE);
            }

            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
