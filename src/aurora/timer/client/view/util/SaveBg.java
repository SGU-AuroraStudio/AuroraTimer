package aurora.timer.client.view.util;

import javax.sound.sampled.Line;
import java.io.*;

public class SaveBg {
    public static boolean saveBg(String bgPath, InputStream bg, boolean forceSave) throws IOException {
        if(bgPath == null || bg==null)
            return false;
        File file = new File(bgPath);
        //已经存在就直接返回
        if(file.exists() && !forceSave)
            return true;
        BufferedInputStream fi = new BufferedInputStream(bg);
        FileOutputStream fo = new FileOutputStream(file);
        BufferedOutputStream foo = new BufferedOutputStream(fo);
        int f;
        while ((f = fi.read()) != -1) {
//            fo.write(f);
            foo.write(f);
        }
//        fo.flush();
//        fo.close();
        foo.flush();
        foo.close();
        fi.close();
        return true;
    }
}
