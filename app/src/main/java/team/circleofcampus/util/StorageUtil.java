package team.circleofcampus.util;

import android.os.Environment;

import java.io.File;

/**
 * 存储路径
 */
public class StorageUtil {

    /**
     * 删除指定文件夹下所有文件
     * @param path - 文件夹完整绝对路径
     * @return 成功返回true, 失败返回false
     */
    public static boolean delAllFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) { // 目录不存在或者不是文件夹
            return false;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) { // 删除文件
                temp.delete();
            } else if (temp.isDirectory()) { // 删除文件夹则使用递归
                delAllFile(path + "/" + tempList[i]); // 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件夹
     * @param folderPath - 文件夹路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
