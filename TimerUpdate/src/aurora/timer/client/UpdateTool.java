package aurora.timer.client;

import java.io.File;
import java.io.IOException;

/**
 * Created by hao on 17-4-28.
 */
public class UpdateTool {
    public static void main(String args[]) {
        String newFileName = args[0];
//        String newFileName = "df";
        String oldFileName = "AuroraTimer.jar";
        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean isSuss = false;
        int i = 0;
        while (!isSuss) {
            try {
                File oldFile = new File(oldFileName);
                File newFile = new File(newFileName);
                if (!newFile.exists()) {
                    System.exit(-1);
                }
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                newFile.renameTo(new File(oldFileName));
                Runtime.getRuntime().exec("java -jar AuroraTimer.jar");
                isSuss = true;
            } catch (Exception e) {
                System.out.println("try:" + i++);
                isSuss = false;
                e.printStackTrace();
            }
        }
    }
}
