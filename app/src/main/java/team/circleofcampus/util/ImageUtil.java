package team.circleofcampus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 处理图片的工具类
 * @author Jaye Li
 */
public class ImageUtil {

    /**
     * 图片质量压缩
     * @param bitmap
     * @param quality - 质量 100 表示不压缩
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap, int quality) {

        if (bitmap == null) return bitmap;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //质量压缩方法，这里quality表示压缩质量，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 图片尺寸压缩
     * @param bitmap
     * @param rqsW - 压缩后的宽
     * @param rqsH - 压缩后的高
     * @return Bitmap
     */
    public static Bitmap sizeCompress(Bitmap bitmap, int rqsW, int rqsH) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmapTemp = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; // be=1 表示不缩放
        if (w > h && w > rqsW) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / rqsW);
        } else if (w < h && h > rqsH) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / rqsH);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmapTemp = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmapTemp;
    }
}
