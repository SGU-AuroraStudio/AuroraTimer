package aurora.timer.client.view.version;

import aurora.timer.client.ServerURL;
import aurora.timer.client.UpdateTool;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by hao on 17-4-18.
 * 自动更新装置
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

        //删除更新替换工具，以免碍眼
        File updateTool = new File("UpdateTool.jar");
        if (updateTool.exists()) {
            updateTool.delete();
        }

        textArea.append("正在检查更新....\n");

        try {
            url = new URL(checkNewUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String temp;
            while ((temp = reader.readLine()) != null) {
                stringBuffer.append(temp);
            }

            JSONObject netVersion = (JSONObject) JSONValue.parse(stringBuffer.toString());
            Properties locVersion = new Properties();
            locVersion.load(getClass().getResourceAsStream("version.properties"));

            textArea.append("当前版本为： " + locVersion.get("version") + "\n最新版本为： " + netVersion.get("version") + "\n");
            String[] updateInfo = ((String) netVersion.get("des")).split(",");
            textArea.append("公告：\n");
            for (int i = 0; i < updateInfo.length; i++) {
                textArea.append("- " + updateInfo[i] + "\n");
            }

            if (netVersion.get("version").equals(locVersion.get("version"))) {
                returnObject.put("status", "new");

                textArea.append("已经是最新版本\n");

            } else {
                returnObject.put("status", "old");
                returnObject.put("version", netVersion.get("version"));

            }

        } catch (ConnectException exception) {
            textArea.append("发送请求失败，请检查网络连接或者服务器运行情况\n");
        } catch (Exception exception) {
            exception.printStackTrace();
            returnObject.put("status", "err");
            exception.printStackTrace();
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

    public void update(String version) {
        String updateUrl = ServerURL.SOFT_URL + "/Timer" + version + ".jar";
        String toolUrl = ServerURL.SOFT_URL + "/UpdateTool.jar";
        URL url = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        byte[] buffer = new byte[1024];

        textArea.append("正在下载更新....\n");

        File newTimer = new File("Timer" + version + ".jar");
        File updateTool = new File("UpdateTool.jar");
//        //更新删除本地文件
//        File localFile = new File("AuroraTimer.jar");
//        if (localFile.exists()) {
//            localFile.renameTo(new File("clean"));
//        }

        try {
            if (!newTimer.exists()) {
                newTimer.createNewFile();
            } else {
                newTimer.delete();
                newTimer.createNewFile();
            }
            //下载更新
            url = new URL(updateUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            outputStream = new FileOutputStream(newTimer);

            int size = 0;
            while ((size = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
                outputStream.flush();
            }
            //下载UpdateTool.jar
            url = new URL(toolUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            outputStream = new FileOutputStream(updateTool);

            while ((size = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
                outputStream.flush();
            }

//            System.out.println("替换旧版本结果：" + newTimer.renameTo(new File("AuroraTimer.jar")));
            textArea.append("下载完毕。");
            boolean flag = false;
            while (!flag) {
                try {
                    String oldFileName = new java.io.File(Update.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .getPath())
                            .getName();
                    Runtime.getRuntime().exec("java -jar UpdateTool.jar " + newTimer.getName() + " " + oldFileName + " && del UpdateTool.jar");
//                    UpdateTool.main(new String[] {newTimer.getName(),oldFileName}); //调试用
                    flag = true;
                } catch (Exception e) {

                }
            }
            System.exit(666);

        } catch (FileNotFoundException connectException) {
            textArea.append("无法访问到新版本，请检查服务器上是否存在源文件\n");
            connectException.printStackTrace();
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
