package aurora.timer.client.vo;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by hao on 16-12-1.
 */
public class UserOnlineTime {
    private String ID;
    private Date todayDate; //今天的日期
    private Time lastOnlineTime; //今天最后在线时间
    private Long todayOnlineTime; //今天在线总毫秒数

    public UserOnlineTime() {
        this("null", new Date(0), new Time(0), Long.decode("0"));
    }

    public UserOnlineTime(String ID, Date todayDate, Time lastOnlineTime, Long todayOnlineTime) {
        setID(ID);
        setTodayDate(todayDate);
        setLastOnlineTime(lastOnlineTime);
        setTodayOnlineTime(todayOnlineTime);
    }

    /**
     * 设置今天的日期
     * @param todayDate 今天的日期，年月日格式
     */
    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    /**
     * 返回今天的日期
     * @return 今天的日期
     */
    public Date getTodayDate() {
        return todayDate;
    }

    /**
     * 设置用户的ID
     * @param ID ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * 设置今天最后在线的时间
     * @param lastOnlineTime 今天最后在线的时间time格式
     */
    public void setLastOnlineTime(Time lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    /**
     * 设置今天在线总时间
     * @param todayOnlineTime 今日在线的时间,单位毫秒
     */
    public void setTodayOnlineTime(Long todayOnlineTime) {
        this.todayOnlineTime = todayOnlineTime;
    }

    /**
     * 返回用户的ID
     * @return ID
     */
    public String getID() {
        return ID;
    }

    /**
     * 返回用户最后在线时间
     * @return 用户今天最后在线时间，time格式
     */
    public Time getLastOnlineTime() {
        return lastOnlineTime;
    }

    /**
     * 返回用户今日在线总时间
     * @return 今日在线总时间，单位毫秒
     */
    public Long getTodayOnlineTime() {
        return todayOnlineTime;
    }

}
