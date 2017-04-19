package aurora.timer.client.view.version;

import aurora.timer.client.ServerURL;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by hao on 17-4-18.
 */
public class Update {
    private JTextArea textArea = null;

    public Update(JTextArea textArea) {
        this.textArea = textArea;
    }

    public JSONObject checkNew() {
        String checkNewUrl = ServerURL.CHECK_VERSION_URL;
        URL url = null;
        BufferedReader reader = null;
        HttpURLConnection httpURLConnection = null;
        JSONObject returnObject = new JSONObject();
        StringBuffer stringBuffer = new StringBuffer("");
        byte[] buffer = new byte[1000];

        textArea.append("正在检查更新....\n");

        try {
            url = new URL(checkNewUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String temp;
            while ((temp=reader.readLine())!=null) {
                stringBuffer.append(temp);
            }

            JSONObject netVersion = (JSONObject) JSONValue.parse(stringBuffer.toString());
            Properties locVersion = new Properties();
            locVersion.load(getClass().getResourceAsStream("version.properties"));

            textArea.append("当前版本为： " + locVersion.get("version") + "\n最新版本为： " + netVersion.get("version") + "\n");

            if (netVersion.get("version").equals(locVersion.get("version"))) {
                returnObject.put("status", "new");

                textArea.append("已经是最新版本\n");

            } else  {
                returnObject.put("status", "old");
                returnObject.put("version", netVersion.get("version"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            returnObject.put("status", "err");
        } finally {
            try {
                reader.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnObject;
    }

    public void update(String version){
        String updateUrl = ServerURL.UPDATE_URL + "/Timer" + version + ".jar";
        URL url = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        byte[] buffer = new byte[1024];

        textArea.append("正在下载更新....\n");

        File newTimer = new File("Timer" + version + ".jar");

        try {
            if (!newTimer.exists()) {
                newTimer.createNewFile();
            } else {
                newTimer.delete();
                newTimer.createNewFile();
            }
            url = new URL(updateUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            outputStream = new FileOutputStream(newTimer);

            int size = 0;
            while ((size=bufferedInputStream.read(buffer))!=-1) {
                outputStream.write(buffer, 0, size);
                outputStream.flush();
            }

            textArea.append("更新完毕，请手动删除旧版本。");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(200);
        } finally {
            try {
                bufferedInputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
