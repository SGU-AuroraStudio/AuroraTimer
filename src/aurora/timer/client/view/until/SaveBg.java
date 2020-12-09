package aurora.timer.client.view.until;

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
        int f;
        while ((f = fi.read()) != -1) {
            fo.write(f);
        }
        fo.flush();
        fo.close();
        fi.close();
        return true;
    }
}
