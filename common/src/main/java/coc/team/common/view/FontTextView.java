package coc.team.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import coc.team.common.util.FontUtil;

/**
 * 应用微软雅黑字体的textview
 */
@SuppressLint("AppCompatCustomView")
public class FontTextView extends TextView {

    public FontTextView (Context context) {
        super(context);
        init(context);
    }

    public FontTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FontTextView (Context context, AttributeSet attrs, int defSyle) {
        super(context, attrs, defSyle);
        init(context);
    }

    /***
     * 设置字体
     *
     * @return
     */
    public void init(Context context) {
        setTypeface(FontUtil.setFont(context));
    }
}
