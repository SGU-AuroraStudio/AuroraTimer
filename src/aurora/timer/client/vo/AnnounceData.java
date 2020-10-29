package aurora.timer.client.vo;

public class AnnounceData {
    private String announcement;
    private Object[] dutylist;

    public AnnounceData(){

    }

    public AnnounceData(String announcement, Object[] dutylist) {
        this.announcement = announcement;
        this.dutylist = dutylist;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Object[] getDutylist() {
        return dutylist;
    }

    public void setDutylist(Object[] dutylist) {
        this.dutylist = dutylist;
    }
}
