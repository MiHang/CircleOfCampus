package team.circleofcampus.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import team.circleofcampus.util.StorageUtil;

/**
 * 下载图片
 */
public class DownloadImage {

    /**
     * 下载图片
     * @param urls
     */
    public static void downloadImages(final ArrayList<String> urls) {
        new Thread(){
            @Override
            public void run() {
                for (String url : urls) {

                    String storagePath = StorageUtil.getStorageDirectory(); // 获取内置存储卡路径
                    String[] tempPath = url.split("/");
                    String catalogPath = "/" + tempPath[0] + "/" + tempPath[1] + "/";
                    String filename = tempPath[2];

                    File file = new File(storagePath + catalogPath);
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdirs();
                        System.out.println("创建路径：" + storagePath + catalogPath);
                    }

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
        }.start();
    }
}
