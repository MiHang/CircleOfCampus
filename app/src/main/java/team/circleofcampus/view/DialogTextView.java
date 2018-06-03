package team.circleofcampus.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 点击效果
 */
public class DialogTextView extends TextView{

    public DialogTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //在按下事件中设置滤镜
                setFilter();
//                setTextColor(Color.WHITE);
                break;
            case MotionEvent.ACTION_UP:
                //由于捕获了Touch事件，需要手动触发Click事件
                performClick();
//                setTextColor(Color.BLACK);
            case MotionEvent.ACTION_CANCEL:
                //在CANCEL和UP事件中清除滤镜
                removeFilter();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     *   设置滤镜
     */
    private void setFilter() {
        //先获取设置的src图片
        Drawable drawable=getBackground();
        //当src图片为Null，获取背景图片
        if (drawable==null) {
            drawable=getBackground();
        }
        if(drawable!=null){
            //设置滤镜
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);;
        }
    }
    /**
     *   清除滤镜
     */
    private void removeFilter() {
        //先获取设置的src图片
        Drawable drawable=getBackground();
        //当src图片为Null，获取背景图片
        if (drawable==null) {
            drawable=getBackground();
        }
        if(drawable!=null){
            //清除滤镜
            drawable.clearColorFilter();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[2];
            if (drawableLeft != null) {

                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }

}