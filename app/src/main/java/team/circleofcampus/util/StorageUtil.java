package team.circleofcampus.util;

import android.os.Environment;

/**
 * 存储路径
 */
public class StorageUtil {

    /**
     * 获取内置存储卡路径
     * @return
     */
    public static String getStorageDirectory() {
        return Environment.getExternalStorageDirectory().getPath() + "/campus of circle/";
    }

    /**
     * 获取上传图片的临时路径
     * @return
     */
    public static String getUploadImgTempPath () {
        return getStorageDirectory() + "temp/";
    }

}
