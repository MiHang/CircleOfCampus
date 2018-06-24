package team.circleofcampus.http;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import team.circleofcampus.util.StorageUtil;

/**
 * 下载图片
 */
public class ImageRequest {

    /**
     * 下载图片
     * @param url
     * @return
     */
    public static Runnable downloadImage(final String url) {
        return new Runnable() {
            private String imageUrl = url;
            @Override
            public void run() {
                Log.e("tag", "download image : " + HttpRequest.URL + url);

                String storagePath = StorageUtil.getStorageDirectory(); // 获取内置存储卡路径
                String[] tempPath = imageUrl.split("/");
                String catalogPath = tempPath[0] + "/" + tempPath[1] + "/";
                String filename = tempPath[2];

                File file = new File(storagePath + catalogPath);
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdirs();
                    Log.e("tag", "创建路径：" + storagePath + catalogPath);
                }

                // 文件不存在时，下载
                File imageFile = new File(storagePath + catalogPath + filename);
                if (!imageFile.exists()) {
                    byte[] bytes = HttpRequest.downloadImg(HttpRequest.URL + url);
                    if (bytes != null) {
                        try {
                            FileOutputStream fos = new FileOutputStream(storagePath + catalogPath + filename);
                            fos.write(bytes);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

}
