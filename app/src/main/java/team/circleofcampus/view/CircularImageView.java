package team.circleofcampus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 圆形头像框
 */
public class CircularImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint paint = new Paint();

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable != null) {

            Bitmap rawBitmap =((BitmapDrawable)drawable).getBitmap();

            //处理Bitmap 转成正方形
            Bitmap newBitmap = cutToSquare(rawBitmap);

            //将newBitmap 转换成圆形
            Bitmap circleBitmap = cutToCircular(newBitmap);

            Rect rect = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
            paint.reset();

            //绘制到画布上
            canvas.drawBitmap(circleBitmap, rect, rect, paint);

        } else {
            super.onDraw(canvas);
        }
    }


    /**
     * 按比例缩放图片
     * @param bitmap - 位图
     * @return Bitmap
     */
    private Bitmap scaleBitmap(Bitmap bitmap){

        int width = this.getWidth();

        //一定要强转成float 不然有可能由于精度不够 出现 scale为0 的错误
        float scale = (float)width / (float)bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 将原始图像裁剪成正方形
     * @param bitmap  - 位图
     * @return Bitmap
     */
    private Bitmap cutToSquare(Bitmap bitmap){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //获取宽度
        int minWidth = (width > height) ? height : width;

        //计算正方形的范围
        int leftTopX = (width - minWidth)/2;
        int leftTopY = (height - minWidth)/2;

        //裁剪成正方形
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,leftTopX,leftTopY,minWidth,minWidth,null,false);

        return  scaleBitmap(newBitmap);
    }


    /**
     * 将图片转换为圆形
     * @param bitmap - 位图
     * @return Bitmap
     */
    private Bitmap cutToCircular(Bitmap bitmap) {

        //指定为 ARGB_4444 能够减小图片大小
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        // 画矩形
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0,bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true); // 抗锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充透明色

        // 画圆
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);

        // 定义PorterDuffXfermode对象
        // 在两者相交的地方绘制源图像，并且绘制的效果会受到目标图像对应地方透明度的影响
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        paint.setXfermode(porterDuffXfermode);

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}

/*
public class CircularImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint paint = new Paint();

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable != null) {

            Bitmap rawBitmap =((BitmapDrawable)drawable).getBitmap();

            //处理Bitmap 转成正方形
            Bitmap newBitmap = cutToSquare(rawBitmap);

            //将newBitmap 转换成圆形
            Bitmap circleBitmap = cutToCircular(newBitmap);

            Rect rect = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
            paint.reset();

            //绘制到画布上
            canvas.drawBitmap(circleBitmap, rect, rect, paint);

        } else {
            super.onDraw(canvas);
        }
    }
    */


    /**
     * 按比例缩放图片
     * @param bitmap - 位图
     * @return Bitmap
     */
    /*
    private Bitmap scaleBitmap(Bitmap bitmap){

        int width = getWidth();

        //一定要强转成float 不然有可能由于精度不够 出现 scale为0 的错误
        float scale = (float)width / (float)bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    */


    /**
     * 将原始图像裁剪成正方形
     * @param bitmap  - 位图
     * @return Bitmap
     */
    /*
    private Bitmap cutToSquare(Bitmap bitmap){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //获取宽度
        int minWidth = (width > height) ? height : width;

        //计算正方形的范围
        int leftTopX = (width - minWidth)/2;
        int leftTopY = (height - minWidth)/2;

        //裁剪成正方形
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,leftTopX,leftTopY,minWidth,minWidth,null,false);

        return  scaleBitmap(newBitmap);
    }*/


    /**
     * 将图片转换为圆形
     * @param bitmap - 位图
     * @return Bitmap
     *//*
    private Bitmap cutToCircular(Bitmap bitmap) {

        //指定为 ARGB_4444 能够减小图片大小
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        // 画矩形
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0,bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true); // 抗锯齿
        canvas.drawARGB(0, 0, 0, 0);

        // 画圆
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);

        // 着色，即为以上画出的圆添加图片像素
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}*/
