package coc.team.home.model;

/**
 * 消息列表 javabean
 */

public class UserMsg {
    int icon;//头像
    String UserName;//用户名
    String Msg;//消息
    String Time;//时间
    String Num;//消息条数

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }
}
