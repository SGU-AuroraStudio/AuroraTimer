package aurora.timer.client.view;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by hao on 17-4-18.
 */
public class Main {
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
                throw new Error("An instance of the application is running.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        makeSingle("Timer");
        OpenCheckForm.main(new String[2]);

    }
}
