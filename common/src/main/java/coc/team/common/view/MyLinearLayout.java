package coc.team.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import coc.team.common.R;

/**
 * 自定义点击效果的LinearLayout
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        setBackground(getResources().getDrawable(R.drawable.linear_selector));
    }


    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
