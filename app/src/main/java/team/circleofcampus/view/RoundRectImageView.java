package team.circleofcampus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import team.circleofcampus.R;

/**
 * 圆矩形 ImageView 附带点击效果
 */

public class RoundRectImageView extends android.support.v7.widget.AppCompatImageView {
     int radius=90;
    private Paint paint;
    boolean isFlag = false;
    Drawable drawable ;

    Bitmap b ;
    public RoundRectImageView(Context context) {
        this(context,null); init();
        init();


    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init();

    }


    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }
    public void init(){
        paint  = new Paint();

    }


    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (isFlag == pressed) {
            return;
        }
        isFlag = pressed;
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.click_overlay_color), PorterDuff.Mode.MULTIPLY);

        if (isFlag) {
            paint.setColorFilter(colorFilter);
        } else {
            paint.setColorFilter(null);
        }
        invalidate();
    }

    /**
     * 绘制圆角矩形图片
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        drawable= getDrawable();
        if (null != drawable) {

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            b= getRoundBitmap(bitmap, radius);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0,0,getWidth(),getHeight());
            paint.reset();

            canvas.drawBitmap(b, rectSrc, rectDest, paint);

        }else{

                super.onDraw(canvas);


        }
    }

    /**
     * 获取圆角矩形图片方法
     * @param bitmap
     * @param roundPx,一般设置成14
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;


    }
}
