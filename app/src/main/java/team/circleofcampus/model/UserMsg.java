package team.circleofcampus.model;

import android.support.annotation.NonNull;

/**
 * 消息列表 javabean
 */
public class UserMsg implements Comparable<UserMsg>{
    String Account;//账号
    String UserName;
    String Sex;
    String Msg;//消息
    String date;//时间
    int  Amount;//消息条数

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    @Override
    public int compareTo(@NonNull UserMsg userMsg) {
        int i=userMsg.getDate().compareTo(this.getDate());

        return i;
    }
}
