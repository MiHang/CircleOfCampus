package team.circleofcampus.util;

import android.os.Environment;

/**
 * 存储路径
 */
public class StorageUtil {

    /**
     * 获取上传图片的临时路径
     * @return
     */
    public static String getUploadImgTempPath () {
        // 获取内置存储卡
        String path = Environment.getExternalStorageDirectory().getPath();
        // 如果存放校园圈临时数据的文件夹不存则创建
        path += "/campus of circle/temp/"; // 保存校园圈临时数据的根路径
        return path;
    }

}
