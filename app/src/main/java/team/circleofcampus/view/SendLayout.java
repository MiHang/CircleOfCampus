package team.circleofcampus.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 消息发送item布局
 */
public class SendLayout extends LinearLayout {
    public SendLayout(Context context) {
        super(context);
    }

    public SendLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SendLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SendLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
