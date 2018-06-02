package team.circleofcampus.view;

import android.content.Context;
import android.util.AttributeSet;

import team.circleofcampus.util.FontUtil;

/**
 * Created by Jaye Li on 2018/6/2.
 */
public class FontRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    public FontRadioButton(Context context) {
        super(context);
        init(context);
    }

    public FontRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FontRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
