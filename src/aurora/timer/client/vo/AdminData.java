package aurora.timer.client.vo;

import java.sql.Time;

public class AdminData {
    private String announcement;
    private Object[] dutylist;
    private Time freeTimeStart;
    private Time freeTimeEnd;

    public AdminData(){

    }

    public AdminData(String announcement, Object[] dutylist) {
        this.announcement = announcement;
        this.dutylist = dutylist;
    }

    /**
     * 获得公告内容
     * @return 返回公告内容
     */
    public String getAnnouncement() {
        return announcement;
    }

    /**
     * 设置公告内容
     * @param announcement 公告内容
     */
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    /**
     * 获得值日表
     * @return 返回值日表
     */
    public Object[] getDutylist() {
        return dutylist;
    }

    /**
     * 设置值日表
     * @param dutylist 值日表
     */
    public void setDutylist(Object[] dutylist) {
        this.dutylist = dutylist;
    }
}
