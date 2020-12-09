package aurora.timer.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by hao on 17-4-28.
 * Updated by Yao on 20-12-04.
 */
public class UpdateTool {
    // 传入[0]newFileName [1]oldFileName
    private String newFileName,oldFileName;
    private File newFile,oldFile;
    private JTextArea textArea;
    private JFrame FRAME;
    UpdateTool(String tNewFileName,String tOldFileName){
        showDialog();
        setFileName(tNewFileName,tOldFileName);
        Thread coverOldFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                coverOldFile();
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FRAME.dispose();
            }

        });
        coverOldFileThread.start();

    }

    private void setFileName(String tNewFileName, String tOldFileName){
        newFileName = tNewFileName;
        oldFileName = tOldFileName;
        // 没有传入旧版名称，尝试这些名称
        String[] tryOldNames = {"AuroraTimer.jar","TimerAlpha.jar","Timer4.0.jar"};
        if(oldFileName == null) {
            textArea.append("没有传入旧版名称\n正在尝试可能的老版本名称...\n");
            System.out.println("没有传入旧版名称\n正在尝试可能的老版本名称...\n");
            textArea.repaint();
            boolean hasOldFile = false;
            for (String tryOldName : tryOldNames) {
                oldFile = new File(tryOldName);
                if (oldFile.length() > 1000) {
                    oldFileName = tryOldName;
                    hasOldFile = true;
                    textArea.append("    找到老版本："+oldFileName);
                    System.out.println("    找到老版本："+oldFileName);
                    textArea.repaint();
                    break;
                }
            }
            if(!hasOldFile) {
                oldFileName = "AuroraTimer.jar";
                textArea.append("没找到老版本。新建文件：" + oldFileName);
                System.out.println("没找到老版本。新建文件：" + oldFileName);
                textArea.repaint();
            }
        }
        oldFile = new File(oldFileName);
        newFile = new File(newFileName);
    }

    private void showDialog(){
        //提示窗口
        FRAME = new JFrame("更新工具");
        textArea = new JTextArea(" 正在打开新计时器...\n "+ oldFileName);
        FRAME.setContentPane(textArea);
        int width = 270;
        int height = 190;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(false);
        FRAME.setVisible(true);
    }

    private void coverOldFile(){
        boolean isSuss = false;
        int i = 0;
        while (!isSuss) {
            try {
                if (!newFile.exists()) {
                    System.out.println("新版不存在");
                    System.exit(-1);
                }
                if (oldFile.exists()) {
                    oldFile.delete();
                    System.out.println("删除旧版");
                }
                textArea.append("正在覆盖文件："+oldFileName);
                System.out.println("正在覆盖文件："+oldFileName);
                newFile.renameTo(new File(oldFileName));
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

    public static void main(String args[]) {
//        String newFileName = "df";
//        String oldFileName = "AuroraTimer.jar";
        // 传入[0]newFileName [1]oldFileName
        String getArgs1 = null;
        try{
            getArgs1 = args[1];
        }catch (Exception e){
            e.printStackTrace();
        }
        UpdateTool updateTool = new UpdateTool(args[0],getArgs1);
    }
}
