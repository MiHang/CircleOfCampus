package coc.team.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 自定义EditText
 * 添加搜索与删除图片
 */

public class MyEditText extends android.support.v7.widget.AppCompatEditText implements  View.OnTouchListener {
    int LeftDrawble= R.drawable.search;

    public MyEditText(Context context) {
        super(context);

    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setSelection(getText().length());
        this.setCompoundDrawablesWithIntrinsicBounds(LeftDrawble,0,0,0);
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getText().length() > 0) {//显示删除按钮图片
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, R.drawable.delete, 0);
                } else {//隐藏删除按钮图片
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
                }
            }
        });
        this.setOnTouchListener(this);

    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Drawable drawable =this.getCompoundDrawables()[2];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getText().length() > 0) {
                setText(getText().subSequence(0, getText().length() - 1));
                setSelection(getText().length());
            }

        }
        return false;
    }
}
