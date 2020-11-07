package aurora.timer.client.vo;

import java.sql.Time;

public class AdminData {
    private String announcement;
    private String dutylist;
    private Time freeTimeStart;
    private Time freeTimeEnd;

    public AdminData() {
        freeTimeStart = new Time((long) 39600000);
        freeTimeEnd = new Time((long) 32400000);
    }

    public AdminData(String announcement, String dutylist) {
        this.announcement = announcement;
        this.dutylist = dutylist;
    }

    public AdminData(String announcement, String dutylist, Time freeTimeStart, Time freeTimeEnd) {
        this.announcement = announcement;
        this.dutylist = dutylist;
        this.freeTimeStart = freeTimeStart;
        this.freeTimeEnd = freeTimeEnd;
    }

    /**
     * 获得公告内容
     *
     * @return 返回公告内容
     */
    public String getAnnouncement() {
        return announcement;
    }

    /**
     * 设置公告内容
     *
     * @param announcement 公告内容
     */
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    /**
     * 获得值日表
     *
     * @return 返回值日表
     */
    public String getDutylist() {
        return dutylist;
    }

    /**
     * 设置值日表
     *
     * @param dutylist 值日表
     */
    public void setDutylist(String dutylist) {
        this.dutylist = dutylist;
    }

    public Time getFreeTimeStart() {
        return freeTimeStart;
    }

    public void setFreeTimeStart(Time freeTimeStart) {
        this.freeTimeStart = freeTimeStart;
    }

    public Time getFreeTimeEnd() {
        return freeTimeEnd;
    }

    public void setFreeTimeEnd(Time freeTimeEnd) {
        this.freeTimeEnd = freeTimeEnd;
    }
}
