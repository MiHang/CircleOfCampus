package coc.team.home.model;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * 消息列表 javabean
 */

public class UserMsg implements Comparable<UserMsg>{
    String UserName;//用户名
    String Sex;//性别
    String Msg;//消息
    Date date;//时间
    int Num;//消息条数

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    @Override
    public int compareTo(@NonNull UserMsg userMsg) {
        int i=this.getDate().compareTo(userMsg.getDate());
        if (i==0){
            return this.getUserName().compareTo(userMsg.getUserName());
        }
        return i;
    }
}
