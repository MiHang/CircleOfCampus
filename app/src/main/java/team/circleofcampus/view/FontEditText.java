package team.circleofcampus.view;

import android.content.Context;
import android.util.AttributeSet;

import team.circleofcampus.util.FontUtil;

/**
 * 应用微软雅黑字体的EditText
 */
public class FontEditText extends android.support.v7.widget.AppCompatEditText {

    public FontEditText(Context context) {
        super(context);
        init(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
