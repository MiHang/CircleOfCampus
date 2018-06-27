package team.circleofcampus.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SharedPreferencesUtil {

    /**
     * 设置用户信息是否更新
     * @param context
     * @param isUserInfoUpdate
     */
    public static void setUserInfoUpdate(Context context, boolean isUserInfoUpdate) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserInfoUpdate", isUserInfoUpdate);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户信息是否更新
     * @param context
     * @return
     */
    public static boolean isUserInfoUpdate (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getBoolean("isUserInfoUpdate", false);
    }

    /**
     * 设置网络是否可用
     * @param context
     * @param isNetworkAvailable
     */
    public static void setNetworkAvailable(Context context, boolean isNetworkAvailable) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isNetworkAvailable", isNetworkAvailable);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户是否发布新的社团圈信息
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getBoolean("isNetworkAvailable", true);
    }

    /**
     * 设置用户是否发布新的社团圈信息
     * @param context
     * @param isPublishNewCircle
     */
    public static void setPublishedNewCircle(Context context, boolean isPublishNewCircle) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isPublishNewCircle", isPublishNewCircle);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户是否发布新的社团圈信息
     * @param context
     * @return
     */
    public static boolean isPublishedNewCircle (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getBoolean("isPublishNewCircle", false);
    }

    /**
     * 设置我发布的社团圈的记录大小
     * @param context
     * @param size
     */
    public static void setMyPublishSocietyCircleCount (Context context, int size) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("myPublishSocietyCircleCount", size);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取我发布的社团圈的记录大小
     * @param context
     * @return
     */
    public static int getMyPublishSocietyCircleCount (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getInt("myPublishSocietyCircleCount", 0);
    }

    /**
     * 设置校园圈的记录大小
     * @param context
     * @param size
     */
    public static void setCampusCircleCount (Context context, int size) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("campusCircleCount", size);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取校园圈的记录大小
     * @param context
     * @return
     */
    public static int getCampusCircleCount (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getInt("campusCircleCount", 0);
    }

    /**
     * 设置社团圈的记录大小
     * @param context
     * @param size
     */
    public static void setSocietyCircleCount (Context context, int size) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("societyCircleCount", size);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取社团圈的记录大小
     * @param context
     * @return
     */
    public static int getSocietyCircleCount (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getInt("societyCircleCount", 0);
    }

    /**
     * 设置用户是否拥有发布社团圈的权限
     * @param context
     * @param isAuthorized
     */
    public static void setAuthorized (Context context, boolean isAuthorized) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAuthorized", isAuthorized);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取用户是否拥有发布社团圈的权限
     * @param context
     * @return
     */
    public static boolean isAuthorized (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getBoolean("isAuthorized", false);
    }

    /**
     * 设置登陆账号记录
     * @param context
     * @param records
     */
    public static void setLandingRecord (Context context, String records) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("records", records);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取登陆时间
     * @param context
     * @return
     */
    public static String getLandingRecord (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getString("records", "[]");
    }


    /**
     * 设置登陆时间
     * @param context
     * @param loginTime
     */
    public static void setLoginTime (Context context, long loginTime) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("login_time", loginTime);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取登陆时间
     * @param context
     * @return
     */
    public static long getLoginTime (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getLong("login_time", 0);
    }

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

    /**
     * 设置程序是否是第一次运行
     * @param context
     * @param isFirstRun
     */
    public static void setFirstRun (Context context, boolean isFirstRun) {
        //实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstRun", isFirstRun);//保存数据
        editor.commit();//提交当前数据
    }

    /**
     * 获取程序是否是第一次运行
     * @param context
     * @return
     */
    public static boolean isFirstRun (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return preferences.getBoolean("isFirstRun", true);
    }

}
