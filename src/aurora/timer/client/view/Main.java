package aurora.timer.client.view;

import aurora.timer.client.ServerURL;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-4-18.
 * Updated by Yao on 20-12-02.
 */
public class Main {
//    public static boolean canConnect = false;

    // 在应用程序的main方法里调用此函数保证程序只有一个实例在运行.
    public static void makeSingle(String singleId) {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock lock = null;

        try {
            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
            File sf = new File(System.getProperty("java.io.tmpdir") + File.separator + singleId + ".single");
            sf.deleteOnExit();
            sf.createNewFile();

            raf = new RandomAccessFile(sf, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();

            if (lock == null) {
                // 如果没有得到锁，则程序退出.
                // 没有必要手动释放锁和关闭流，当程序退出时，他们会被关闭的.
                JOptionPane.showMessageDialog(null, "有一个计时器正在运行中", "错误", JOptionPane.ERROR_MESSAGE);
                throw new Error("An instance of the application is running.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setIp() {
        Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
        try {
            //测试连接
            String checkUrl = ServerURL.CHECK_VERSION_URL;
            HttpURLConnection httpURLConnection = null;
            URL url = new URL(checkUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            httpURLConnection.disconnect();
//            canConnect = true;
        } catch (Exception e) {
            e.printStackTrace();
            //自定义弹窗
            JOptionPane jOptionPane = new JOptionPane("连接服务器失败");
            jOptionPane.setWantsInput(true);
            jOptionPane.setInitialSelectionValue(ServerURL.HOST);
            JDialog dialog = jOptionPane.createDialog("错误");
            dialog.show();
            dialog.dispose();

            String getHost = (String) jOptionPane.getInputValue();
//            String getHost = JOptionPane.showInputDialog(new JFrame(), "连接服务器失败", ServerURL.HOST);
            //在关掉输入框后getHost="uninitializedValue"
            if(getHost.equals("uninitializedValue"))
                System.exit(11);
            if (getHost.length() != 0) {
                ServerURL.setHost(getHost);
                preferences.put("host", getHost);
                setIp();
            }
//            //因为是递归，如果canConnect定义在函数里，永远是false，所以定义为static，用getHost.equals("uninitializedValue")代替。
//            if (!canConnect)
//                System.exit(11);
        }
    }

    public static void main(String[] args) {
        makeSingle("Timer");
        setIp();
        OpenCheckForm.main(new String[2]);
        LoginForm.main(new String[1]);
    }
}