package aurora.timer.client.view;

import aurora.timer.client.view.version.Update;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by hao on 17-4-19.
 */
public class OpenCheckForm {
    private static JFrame FRAME;
    private JPanel parent;
    private JTextArea InfoPane;

    public static void main(String[] args) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FRAME = new JFrame("检查更新");
                OpenCheckForm form = new OpenCheckForm();
                FRAME.setContentPane(form.parent);
                int width = 270;
                int height = 190;
                FRAME.setBounds((d.width-width)/2, (d.height-height)/2, width, height);
                FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                FRAME.setResizable(false);
                FRAME.setVisible(true);

                //检查更新
                Update update = new Update(form.InfoPane);
                JSONObject checkObject = update.checkNew();
                int updateStatus = -1;
                if (checkObject.get("status").equals("old")) {
                    String[] option = {"更新", "不更"};
                    updateStatus = JOptionPane.showOptionDialog(null, "已检测到新版本，是否更新？", "提示", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
                }
                if (updateStatus == 0) {
                    String newVersion = (String) checkObject.get("version");
                    update.update(newVersion);
                    try {
                        Runtime.getRuntime().exec("java -jar AuroraTimer.jar");
                    } catch (IOException e) {
                        System.err.println("OPEN TIMER EXCEPTION");
                        e.printStackTrace();
                    }
                } else {
                    LoginForm.main(new String[0]);
                }

            }
        });
        Thread thisThread = Thread.currentThread();
        try {
            thisThread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FRAME.dispose();
    }
}
