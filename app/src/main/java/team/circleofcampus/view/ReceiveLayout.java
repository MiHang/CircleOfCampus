package team.circleofcampus.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 消息接收布局
 */
public class ReceiveLayout extends LinearLayout {
    public ReceiveLayout(Context context) {
        super(context);
    }

    public ReceiveLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReceiveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ReceiveLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
