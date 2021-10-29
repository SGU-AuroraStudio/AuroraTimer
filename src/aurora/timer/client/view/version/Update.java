package aurora.timer.client.view.version;

import aurora.timer.client.UpdateTool;
import aurora.timer.client.vo.base.Constants;
import aurora.timer.client.vo.base.ServerURL;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by hao on 17-4-18.
 * Updated by Zheng on 21-10-27.
 * 自动更新装置
 */
//TODO:代码太乱了，得重写
public class Update{
    private JTextArea textArea = null;
    public Update(JTextArea textArea) {
        this.textArea = textArea;
    }

    public JSONObject checkNew() {
//        System.out.println("检查更新！");

        String checkNewUrl = ServerURL.CHECK_VERSION_URL;
        URL url = null;
        BufferedReader reader = null;
        HttpURLConnection httpURLConnection = null;
        JSONObject returnObject = new JSONObject();
        StringBuffer stringBuffer = new StringBuffer("");

//        //TODO:删除更新替换工具，以免碍眼。怎么试都不能在更新完成后删除。。。

//        File updateTool = new File("UpdateTool.jar");
//        if (updateTool.exists()){
//            updateTool.delete();
//
//        }


        textArea.append("正在检查更新....\n");
        // 判断updateTool.jar运行结束时再删除相关文件
        File mark = new File("update.auroradata");
        int sleepCnt = 0;
        while(mark.exists()){
            sleepCnt++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(sleepCnt>3){
                break; //更新超时，下次启动再删除升级工具
            }
        }

        File updateTool = new File("UpdateTool.jar");
        if (updateTool.exists()){
            updateTool.delete();
        }
        if(mark.exists()) {
            mark.delete();
        }


        try {
            url = new URL(checkNewUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
            String temp;
            while ((temp = reader.readLine()) != null) {
                stringBuffer.append(temp);
            }
            JSONObject netVersion = (JSONObject) JSONValue.parse(stringBuffer.toString());
            textArea.append("当前版本为： " + Constants.locVersion.get("version") + "\n最新版本为： " + netVersion.get("version") + "\n");
            String[] updateInfo = ((String) netVersion.get("des")).split(",");
            textArea.append("公告：\n");
            for (int i = 0; i < updateInfo.length; i++) {
                textArea.append("- " + updateInfo[i] + "\n");
            }
            if (netVersion.get("version").equals(Constants.locVersion.get("version"))) {
                returnObject.put("status", "new");
                textArea.append("已经是最新版本\n");
            } else {
                returnObject.put("status", "old");
                returnObject.put("version", netVersion.get("version"));
            }
        } catch (Exception exception) {
            textArea.append("发送请求失败，请检查网络连接或者服务器运行情况\n");
            exception.printStackTrace();
            returnObject.put("status", "err");
            exception.printStackTrace();
        } finally {
            try {
                if(reader!=null)
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
        URL url;
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
            if (newTimer.exists())
                newTimer.delete();
            newTimer.createNewFile();
            //下载更新
            System.out.println("下载"+newTimer);
            url = new URL(updateUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            outputStream = new FileOutputStream(newTimer);

            int size = 0;
            while ((size = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
                outputStream.flush();
            }
            //下载UpdateTool.jar
            System.out.println("下载UpdateTool.jar");
            url = new URL(toolUrl);
            bufferedInputStream = new BufferedInputStream(url.openStream());
            outputStream = new FileOutputStream(updateTool);

            while ((size = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
                outputStream.flush();
            }
            textArea.append("下载完毕。");
            boolean flag = false;
            while (!flag) {
                String oldFileName = new java.io.File(Update.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                        .getName();
//                    String oldFileName = Update.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                oldFileName = java.net.URLDecoder.decode(oldFileName,"utf-8"); // 不这样会乱码，原本是URL编码，%e5%b7啥啥啥的
                newTimer.renameTo(new File(oldFileName));
                flag = true;
                Runtime.getRuntime().exec("java -jar UpdateTool.jar " + newTimer.getName() + " " + oldFileName);

                //                    UpdateTool.main(new String[] {newTimer.getName(),oldFileName}); //调试用
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(200);
        } finally {
            try {
                assert bufferedInputStream != null;
                assert outputStream != null;
                bufferedInputStream.close();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //更新后启动新计时器，旧计时器关闭
        System.exit(666);
    }


}
