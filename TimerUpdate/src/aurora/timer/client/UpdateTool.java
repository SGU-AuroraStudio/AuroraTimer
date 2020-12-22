package aurora.timer.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by hao on 17-4-28.
 * Updated by Yao on 20-12-04.
 */
public class UpdateTool {
    // 传入[0]newFileName [1]oldFileName
    private String newFileName, oldFileName;
    private File newFile, oldFile;
    private JTextArea textArea;
    private JFrame FRAME;

    UpdateTool(String tNewFileName, String tOldFileName) {
        showDialog();
        setFileName(tNewFileName, tOldFileName);
        coverOldFile();
    }

    private void setFileName(String tNewFileName, String tOldFileName) {
        newFileName = tNewFileName;
        oldFileName = tOldFileName;
        textArea.append("旧版名称：" + oldFileName + "\n");
        // 没有传入旧版名称，尝试这些名称
        String[] tryOldNames = {"AuroraTimer.jar", "TimerAlpha.jar", "Timer4.0.jar"};
        if (oldFileName == null) {
            textArea.append("没有传入旧版名称\n正在尝试可能的老版本名称...\n");
            oldFileName = "AuroraTimer.jar";
            boolean hasOldFile = false;
            for (String tryOldName : tryOldNames) {
                oldFile = new File(tryOldName);
                if (oldFile.length() > 1000) {
                    oldFileName = tryOldName;
                    hasOldFile = true;
                    textArea.append("找到老版本：" + oldFileName);
                    break;
                }
            }
        }
    }

    private void showDialog() {
        //提示窗口
        FRAME = new JFrame("更新工具");
        textArea = new JTextArea();
        FRAME.setContentPane(textArea);
        int width = 270;
        int height = 190;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setVisible(true);
    }

    private void coverOldFile() {
        boolean isSuss = false;
        int i = 0;
        while (!isSuss) {
            oldFile = new File(oldFileName);
            newFile = new File(newFileName);
            try {
                if (!newFile.exists()) {
                    System.out.println("新版不存在");
                    System.exit(-1);
                }
                if (oldFile.exists()) {
                    oldFile.delete();
                    System.out.println("删除旧版");
                }
                textArea.append("新计时器名称：" + oldFileName + "\n");
                newFile.renameTo(new File(oldFileName));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                textArea.append("正在打开新计时器...\n ");
                FRAME.dispose();
                Runtime.getRuntime().exec("java -jar " + oldFileName);
                isSuss = true;
            } catch (Exception e) {
                System.out.println("try:" + i++);
                isSuss = false;
                e.printStackTrace();
            }
        }


    }

    /**
     * main
     *
     * @param args args[0] new fileName;args[1]oldFileName
     */
    public static void main(String args[]) {
//        String newFileName = "df";
//        String oldFileName = "AuroraTimer.jar";
        // 传入[0]newFileName [1]oldFileName
        String getArgs1 = null;
        try {
            getArgs1 = args[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        UpdateTool updateTool = new UpdateTool(args[0], getArgs1);
    }
}
