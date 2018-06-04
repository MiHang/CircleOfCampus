package team.circleofcampus.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Jaye Li on 2017/11/30.
 */
public class SharedPreferencesUtil {

    /**
     * 设置用户账号
     * @param context
     * @param account
     */
    public static void setAccount (Context context, String account) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account", account);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户账号
     * @param context
     * @return
     */
    public static String getAccount (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getString("account",null);
    }

    /**
     * 设置用户ID
     * @param context
     * @param uid - 用户ID
     */
    public static void setUID (Context context, int uid) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("uId", uid);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户ID
     * @param context
     * @return
     */
    public static int getUID (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getInt("uId",0);
    }

}
