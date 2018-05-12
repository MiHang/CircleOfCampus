package coc.team.home.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

/**
 * Created by 惠普 on 2018-01-14.
 */

public class Contact implements Serializable,Comparable<Contact> {
    private String Account;//账号
    private String LastName;//姓氏首字母
    private String UserIcon;
    private String UserName;


    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserIcon() {
        return UserIcon;
    }

    public void setUserIcon(String userIcon) {
        UserIcon = userIcon;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }



    @Override
    public int compareTo(@NonNull Contact contact) {//按照用户名字母顺序排序
        String s1 = this.getLastName().toString();
        String s2 =contact.getLastName().toString();

        return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
    }
}
