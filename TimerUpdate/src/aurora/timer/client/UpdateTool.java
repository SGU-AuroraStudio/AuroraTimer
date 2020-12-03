package aurora.timer.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by hao on 17-4-28.
 */
public class UpdateTool {
    public static void main(String args[]) {
//        String newFileName = "df";
//        String oldFileName = "AuroraTimer.jar";
        String newFileName = args[0];
        String oldFileName = null;
        try {
            oldFileName = args[1];
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("没有传入oldFileName");
        }
        if(oldFileName == null)
            oldFileName = "AuroraTimer.jar";
        System.out.println("新版："+newFileName);
        System.out.println("旧版："+oldFileName);
        JFrame FRAME = new JFrame("检查更新");
        JTextArea textArea = new JTextArea(" 正在打开新计时器...\n "+ newFileName);
        FRAME.setContentPane(textArea);
        int width = 270;
        int height = 190;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setVisible(true);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FRAME.dispose();
        boolean isSuss = false;
        int i = 0;
        while (!isSuss) {
            try {
                File oldFile = new File(oldFileName);
                File newFile = new File(newFileName);
                if (!newFile.exists()) {
                    System.out.println("新版不存在");
                    System.exit(-1);
                }
                if (oldFile.exists()) {
                    oldFile.delete();
                    System.out.println("删除旧版");
                }
                newFile.renameTo(new File(oldFileName));
                System.out.println("修改新版名为："+oldFileName);
                System.out.println("正在启动"+oldFileName);
                Runtime.getRuntime().exec("java -jar " + oldFileName);
                isSuss = true;
            } catch (Exception e) {
                System.out.println("try:" + i++);
                isSuss = false;
                e.printStackTrace();
            }
        }
    }
}
