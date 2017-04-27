package aurora.timer.client.view.until;

import java.io.*;

/**
 * Created by hao on 17-4-27.
 */
public class AutoOpen {
    private static final int WINDOWS_10 = 1;
    private static final int LINUX = 2;
    private static final int WINDOWS_OT = 3;

    private static int judgeOs() {
        String osName = System.getProperty("os.name");
        if (osName.equals("Windows 10")) {
            return WINDOWS_10;
        } else if (osName.equals("Linux")) {
            return LINUX;
        } else if (osName.contains("Windows")) {
            return WINDOWS_OT;
        }
        return -1;
    }

    public static void autoOpen() {
        int os = judgeOs();
        String jarPath = new File("AuroraTimer.jar").getAbsolutePath();
        File file;
        OutputStreamWriter outputStreamWriter;
        switch (os) {
            case WINDOWS_10:try {
                file = new File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\StartUp\\TimerStar.bat");
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                outputStreamWriter = new FileWriter(file);
                outputStreamWriter.write("java -jar " + jarPath);
                outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;

            case LINUX:break;
            case WINDOWS_OT:break;
            default:System.err.println("未找到该系统");
        }
    }

    public static void cancelAuto() {
        int os = judgeOs();
        switch (os) {
            case WINDOWS_10:break;
            case LINUX:break;
            case WINDOWS_OT:break;
            default:System.err.println("未找到该系统");
        }
    }
}
