package aurora.timer.client.view.util;

import java.io.*;

/**
 * Created by hao on 17-4-27.
 */
public class AutoOpen {
    private static final int WINDOWS_10 = 1;
    private static final int LINUX = 2;
    private static final int WINDOWS_OT = 3;
    private static String autoDir;

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
            case WINDOWS_10:
                try {
                    autoDir = "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\StartUp\\TimerStar.vbs";
                    file = new File("TimerStar.bat");
                    File startFile = new File("timer.vbs");
                    startFile.createNewFile();
                    if (!file.exists()) {
                        file.createNewFile();
                    } else {
                        file.delete();
                        file.createNewFile();
                    }
                    //将执行计时器的bat放入系统的临时文件夹内
                    outputStreamWriter = new FileWriter(file);
                    outputStreamWriter.write("java -jar " + formatPath(jarPath));
                    outputStreamWriter.close();

                    outputStreamWriter = new FileWriter(startFile);
                    outputStreamWriter.write("createobject(\"wscript.shell\").run \"\"\"" + file.getAbsolutePath() + "\"\"\",0 ");
                    System.out.println("createobject(\"wscript.shell\").run \"\"\"");
//                System.out.println("createobject(\"wscript.shell\").run \"" + file.getAbsolutePath() + "\",0 ");
                    outputStreamWriter.close();

                    //调用cmd命令来复制启动的vbs文件
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("cmd /c copy " + formatPath(startFile.getAbsolutePath()) + " " + formatPath(autoDir));
//                System.out.println("cmd /c copy " + formatPath(startFile.getAbsolutePath()) + " " + formatPath(autoDir));

//                startFile.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case LINUX:
                break;
            case WINDOWS_OT:
                break;
            default:
                System.err.println("未找到该系统");
        }
    }

    public static void cancelAuto() {
        int os = judgeOs();
        switch (os) {
            case WINDOWS_10:
                break;
            case LINUX:
                break;
            case WINDOWS_OT:
                break;
            default:
                System.err.println("未找到该系统");
        }
    }

    private static String formatPath(String path) {
        if (path == null) return "";
        return path.replaceAll(" ", "\" \"");
    }
}
