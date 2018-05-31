package coc.team.common.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * 字体工具
 */
public class FontUtil {

    private static String fongUrl = "font/msyhl.ttc";
    private static Typeface tf;

    /***
     * 设置字体
     * @return Typeface
     */
    public static Typeface setFont(Context context) {
        if(tf==null){
            tf = Typeface.createFromAsset(context.getAssets(), fongUrl);
        }
        return tf;
    }
}