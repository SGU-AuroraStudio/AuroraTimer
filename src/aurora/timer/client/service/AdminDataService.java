package aurora.timer.client.service;

import aurora.timer.client.ServerURL;
import aurora.timer.client.view.util.SmartHttpUtil;
import aurora.timer.client.vo.AdminData;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.HashMap;
import java.util.logging.Logger;
//TODO:用SmartHttpUtil重写
public class AdminDataService {

    //    public static String ADMIN = "http://" + "127.0.0.1:8080" + "/timer/admin"; //本地调试用
    private static Logger logger = Logger.getLogger("admin");

    /**
     * 获取管理员数据,服务器返回的数据里时间是long类型
     *
     * @return AdminData对象
     */
    public AdminData getAdminData1() {
        HttpURLConnection connection = null;
        JSONObject object = new JSONObject();
        AdminData vo = null;
        boolean flag = false;
        try {
            URL url = new URL(ServerURL.ADMIN);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setInstanceFollowRedirects(true);//设置只作用于当前的实例
            connection.setRequestProperty("Content-Type", "application/json"); //向服务器表示我传的是json
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.connect();

            // 返回整个AdminData Json对象
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); //注意：到了这行代码才会发送请求
            StringBuffer sb = new StringBuffer("");
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();

            object = (JSONObject) JSONValue.parseWithException(sb.toString());
            vo = new AdminData((String) object.get("announcement"), (String) object.get("dutyList"), new Time((long) object.get("freeTimeStart")), new Time((long) object.get("freeTimeEnd")));

            flag = object.get("announcement") != null; // flag判断有没有获取到数据
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            logger.info("加载公告");
            return vo;
        } else {
            logger.warning("加载公告失败");
            return null;
        }
    }

    /**
     * 获取管理员数据,服务器返回的数据里时间是long类型
     * @return AdminData对象
     */
    public AdminData getAdminData(){
        String res;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.ADMIN, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.toString());
            return null;
        }
        JSONObject object = (JSONObject) JSONValue.parse(res);
        AdminData vo = new AdminData((String)object.get("announcement"),(String) object.get("dutyList"), new Time((Long)object.get("freeTimeStart")),new Time((Long)object.get("freeTimeEnd")));
        logger.info("加载公告 "+vo.getAnnouncement()+" "+vo.getDutylist());
        return vo;
    }

    public boolean uploadAdminData(AdminData vo, UserData userData){
        HashMap<String, String> params = new HashMap<>();
        params.put("announcement",vo.getAnnouncement());
        params.put("dutyList",vo.getDutylist());
        params.put("freeTimeStart",vo.getFreeTimeStart().toString());
        params.put("freeTimeEnd",vo.getFreeTimeEnd().toString());
        String res;
        try {
            res = SmartHttpUtil.sendPostForm(ServerURL.ADMIN, params, null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.toString());
            return false;
        }
        if(res.equals("true"))
            return true;
        else {
            JOptionPane.showMessageDialog(null, "上传失败\n"+res, "提示", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isFreeTime(){
        String res;
        try {
            res = SmartHttpUtil.sendGet(ServerURL.ADMIN + "?x=freeTime", null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return res.equals("true");
    }

    public boolean isFreeTime1() {
        HttpURLConnection connection = null;
        boolean flag = false;
        try {
            URL url = new URL(ServerURL.ADMIN + "?x=freeTime");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setInstanceFollowRedirects(true);//设置只作用于当前的实例
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); //注意：到了这行代码才会发送请求
            String s = reader.readLine();
            reader.close();
            flag = s.equals("true");
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {

        }
        return flag;
    }

//    public static void main(String[] args) {
//        AdminDataService adminDataService = new AdminDataService();
//        adminDataService.getAdminData();
//        AdminData vo = new AdminData();
//        vo.setAnnouncement("李四announcement");
//        vo.setDutylist("李四|李四|李四|李四|李四|李四|李四");
//    }
}

