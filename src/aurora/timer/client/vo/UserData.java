package aurora.timer.client.vo;

import java.io.Serializable;

/**
 * 用户的基本信息,还要加备注
 * Created by hao on 16-12-1.
 */
public class UserData implements Serializable {
    private String nickName;
    private String ID;
    private String passWord;
    private String telNumber;
    private String shortTelNumber;
    private String displayURL;
    private String bgUrl;
    private Boolean loginStatus; //是不是已经登录
    private Boolean isLeave; //是不是已经离开不再纳入统计
    private Boolean isAdmin; //是否是管理员

    public UserData() {
        this("null", "0", "0", "0", "0",
                "0", Boolean.FALSE, Boolean.FALSE);
    }

    public UserData(String nickName, String ID, String passWord) {
        this(nickName, ID, passWord, "0", "0",
                "0", Boolean.FALSE, Boolean.FALSE);
    }


    public UserData(String nickName, String ID, String passWord, String telNumber, String shortTelNumber,
                    String displayURL, Boolean loginStatus, Boolean isLeave) {
        setNickName(nickName);
        setID(ID);
        setPassWord(passWord);
        setTelNumber(telNumber);
        setShortTelNumber(shortTelNumber);
        setDisplayURL(displayURL);
        setLoginStatus(loginStatus);
        setIsLeave(isLeave);
    }

    /**
     * 设置密码
     *
     * @param passWord 这是密码。。
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * 取得密码
     *
     * @return 密码
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * 设置登录状态，默认为false
     *
     * @param loginStatus 用户是否在线标记,防止重复登录。当然，这没法用，删了麻烦
     */
    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    /**
     * 取得用户在线状态
     *
     * @return false
     */
    public boolean getLoginStatus() {
        return loginStatus;
    }

    /**
     * 输入管理密码来设置一个账号是否还在工作室,离开的将不显示
     *
     * @param leave true为离开，false为在工作室中
     */
    public void setIsLeave(boolean leave) {
        isLeave = leave;
    }

    /**
     * 取得用户是否在工作室
     *
     * @return 用户是否在工作室
     */
    public boolean getIsLeave() {
        return isLeave;
    }

    /**
     * 设置是否是管理员
     *
     * @param isAdmin 是否是管理员
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * 取得用户是否是管理员
     *
     * @return 用户是否是管理员
     */
    public boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * 设置用户的ID,要求设置为学号
     *
     * @param ID ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * 取得用户的昵称
     *
     * @return 返回用户昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 取得用户的ID
     *
     * @return 返回用户的ID
     */
    public String getID() {
        return ID;
    }

    /**
     * 取得用户的电话号码
     *
     * @return 返回用户的电话号码
     */
    public String getTelNumber() {
        return telNumber;
    }

    /**
     * 取得用户的短号
     *
     * @return 返回用户短号
     */
    public String getShortTelNumber() {
        return shortTelNumber;
    }

    /**
     * 取得用户头像的URL
     *
     * @return 返回用户头像的地址
     */
    public String getDisplayURL() {
        return displayURL;
    }

    /**
     * 设置用户的昵称
     *
     * @param nickName 昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 设置用户的电话号码
     *
     * @param telNumber 用户的电话号码
     */
    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    /**
     * 设置用户的短号
     *
     * @param shortTelNumber 用户的短号，默认0
     */
    public void setShortTelNumber(String shortTelNumber) {
        this.shortTelNumber = shortTelNumber;
    }

    //TODO:用户头像
    /**
     * 设置用户的头像地址
     *
     * @param displayURL 头像的URL
     */
    public void setDisplayURL(String displayURL) {
        this.displayURL = displayURL;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }
}
