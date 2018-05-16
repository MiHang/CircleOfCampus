package coc.team.home.model;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * 消息列表 javabean
 */

public class UserMsg implements Comparable<UserMsg>{
    String Account;//账号
    String Msg;//消息
    Date date;//时间
    int  Amount;//消息条数

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
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

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    @Override
    public int compareTo(@NonNull UserMsg userMsg) {
        int i=this.getDate().compareTo(userMsg.getDate());

        return i;
    }
}
