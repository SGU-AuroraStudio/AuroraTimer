package aurora.timer.client.service;

import aurora.timer.client.vo.AdminData;
import aurora.timer.client.vo.UserData;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;

public class AdminDataService {

    public static String ADMIN = "http://" + "127.0.0.1:8080" + "/timer/admin"; //本地调试用

    /**
     * 获取管理员数据,服务器返回的数据里时间是long类型
     *
     * @return AdminData对象
     */
    public AdminData getAdminData() {
        HttpURLConnection connection = null;
        JSONObject object = new JSONObject();
        AdminData vo = null;
        boolean flag = false;
        try {
            URL url = new URL(ADMIN);
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
        if (flag)
            return vo;
        else
            return null;
    }

    public boolean uploadAdminData(AdminData vo, UserData userData) {
        HttpURLConnection connection = null;
        boolean flag = false;
        try {
            URL url = new URL(ADMIN+"?id="+userData.getID()+"&password="+userData.getPassWord());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);//设置是否向HttpUrlConnction输出，因为这个是POST请求，参数要放在http正文内，因此需要设为true，默认情况下是false
            connection.setDoInput(true);//设置是否向HttpUrlConnection读入，默认情况下是true
            connection.setUseCaches(false);//POST请求不能使用缓存（POST不能被缓存）
            connection.setInstanceFollowRedirects(true);//设置只作用于当前的实例
            connection.connect();

            // post参数要用String形式
            String param = String.format("id=%s&password=%s&announcement=%s&dutyList=%s&freeTimeStart=%s&freeTimeEnd=%s", vo.getId(), vo.getPassword(), vo.getAnnouncement(), vo.getDutylist(), vo.getFreeTimeStart().getTime(), vo.getFreeTimeEnd().getTime());
            OutputStream out = connection.getOutputStream();
            out.write(param.getBytes());
            out.flush();
            out.close();

            //这里只会返回一行字符串"true"或者"false"
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); //注意：到了这行代码才会发送请求
            String str;
            str = reader.readLine();
            reader.close();
            flag = str.equals("true");
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean isFreeTime() {
        HttpURLConnection connection = null;
        boolean flag = false;
        try {
            URL url = new URL(ADMIN + "x=freeTime");
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

