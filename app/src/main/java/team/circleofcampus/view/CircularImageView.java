package team.circleofcampus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import team.circleofcampus.R;

/**
 * 圆形头像框
 */
public class CircularImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint paint = new Paint();
    boolean isFlag = false;

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        //在按下事件中设置滤镜
                        isFlag = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isFlag = false;

                    default:
                        break;
                }
                invalidate();
                return true;
            }
        });
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 将头像按比例缩放
     *
     * @param bitmap
     * @return
     */
    private Bitmap scaleBitmap(Bitmap bitmap) {
        int width = getWidth();
        //一定要强转成float 不然有可能因为精度不够 出现 scale为0 的错误
        float scale = (float) width / (float) bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    /**
     * 将原始图像裁剪成正方形
     */
    private Bitmap dealRawBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //获取宽度
        int minWidth = width > height ? height : width;
        //计算正方形的范围
        int leftTopX = (width - minWidth) / 2;
        int leftTopY = (height - minWidth) / 2;
        //裁剪成正方形
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, leftTopX, leftTopY, minWidth, minWidth, null, false);
        return scaleBitmap(newBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap rawBitmap = ((BitmapDrawable) drawable).getBitmap();

            //处理Bitmap 转成正方形
            Bitmap newBitmap = dealRawBitmap(rawBitmap);
            //将newBitmap 转换成圆形
            Bitmap circleBitmap = getCircleBitmap(newBitmap);

            final Rect rect = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
            paint.reset();
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.click_overlay_color), PorterDuff.Mode.MULTIPLY);
            paint.setAntiAlias(true);
            paint.setDither(true);
            if (isFlag) {
                paint.setColorFilter(colorFilter);
            }

            //绘制到画布上
            canvas.drawBitmap(circleBitmap, rect, rect, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取圆形图片
     *
     * @param bitmap
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {

        //指定为 ARGB_4444 可以减小图片大小
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
