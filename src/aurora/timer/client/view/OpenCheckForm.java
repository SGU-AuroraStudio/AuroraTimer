package aurora.timer.client.view;

import aurora.timer.client.view.util.SaveBg;
import aurora.timer.client.view.version.Update;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by hao on 17-4-19.
 */
public class OpenCheckForm {
    private static JFrame FRAME;
    private JPanel parent;
    private JTextArea InfoPane;

    public static void main(String[] args) {
        //预加载默认背景图
        new Thread() {
            @Override
            public void run() {
                InputStream bg1 = getClass().getResourceAsStream("bg1.png");
                String bgPath1 = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg1.png";
                InputStream bg2 = getClass().getResourceAsStream("bg2.png");
                String bgPath2 = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg2.png";
                InputStream bg3 = getClass().getResourceAsStream("bg3.png");
                String bgPath3 = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg3.png";
                try {
                    SaveBg.saveBg(bgPath1, bg1, true);
                    SaveBg.saveBg(bgPath2, bg2, true);
                    SaveBg.saveBg(bgPath3, bg3, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
        FRAME = new JFrame("检查更新");
        OpenCheckForm form = new OpenCheckForm();
        FRAME.setContentPane(form.parent);
        FRAME.setSize(270,190);
        FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setAlwaysOnTop(true);
        FRAME.setVisible(true);
        FRAME.setAlwaysOnTop(false);
        //检查更新
        Update update = new Update(form.InfoPane);
        JSONObject checkObject = update.checkNew();
        int updateStatus = -1;
        //如果当前版本是旧版
        if (checkObject.get("status").equals("old")) {
            String[] option = {"更新", "不更"};
            updateStatus = JOptionPane.showOptionDialog(null, "已检测到新版本:" +
                            checkObject.get("version") + "，是否更新？", "提示", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
            if (updateStatus == 0) {
                String newVersion = (String) checkObject.get("version");
                update.update(newVersion);
            }
        }
        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FRAME.dispose();
    }
}
