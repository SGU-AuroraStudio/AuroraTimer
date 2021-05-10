package aurora.timer.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

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
        //兼容旧版本，旧版本如果是中文名会传进来一个URL编码的字符串
        try {
            tOldFileName = URLDecoder.decode(tOldFileName,"utf-8"); // 不这样会乱码，原本是URL编码，%e5%b7啥啥啥的
        }catch (Exception e){
            e.printStackTrace();
        }
        oldFileName = tOldFileName;
//        textArea.append("旧版名称：" + oldFileName + "\n");
        // 没有传入旧版名称，尝试这些名称
        String[] tryOldNames = {"AuroraTimer.jar", "TimerAlpha.jar", "Timer4.0.jar"};
        if (oldFileName == null) {
            textArea.append("没有传入旧版名称\n正在尝试可能的老版本名称...\n");
            oldFileName = "AuroraTimer.jar";
            for (String tryOldName : tryOldNames) {
                oldFile = new File(tryOldName);
                if (oldFile.length() > 1000) {
                    oldFileName = tryOldName;
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
        textArea.setEditable(false);
        FRAME.setSize(270,190);
        FRAME.setContentPane(textArea);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
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
                //删除旧版
                if (newFile.exists() && oldFile.exists() && !oldFileName.equals(newFileName)) {
                    if(oldFile.delete()) {
                        //把新版改名为旧版的名字
                        if (newFile.renameTo(oldFile))
                            newFileName = oldFileName;
                    }
                }
                textArea.append("新计时器名称：" + newFileName + "\n");
                textArea.append("正在打开新计时器...\n ");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FRAME.dispose();
                isSuss = true;
                Runtime.getRuntime().exec("java -jar " + newFileName);
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
