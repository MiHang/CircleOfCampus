package coc.team.login;

import android.content.Context;
import android.renderscript.RenderScript;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/5/28/028.
 */
public class ButtonEditTextTest extends EditText{






    private Button mButton;
    private int mButtonHeightPadding=10;
    private int mButtonWeightPadding=10;



    public ButtonEditTextTest(Context context) {
        super(context);
        init();
    }
    public ButtonEditTextTest(Context context, AttributeSet attrs){
        super(context,attrs);
        init();

    }
    public ButtonEditTextTest(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs);
        init();
    }

    private void init() {
        mButton = new Button(getContext());
        mButton.setText("登陆");
        mButton.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v){
                Toast.makeText(getContext(),"登陆",Toast.LENGTH_SHORT).show();
            }

        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getY()  > mButtonHeightPadding && event.getY() < getHeight() - mButtonHeightPadding &&
                    event.getX()>getWidth() - mButtonWeightPadding - mButton.getMeasuredWidth()
                    &&
                    event.getX ( ) < getWidth() - mButtonWeightPadding) {
            return mButton.dispatchTouchEvent(event);


            }
            return super.dispatchTouchEvent(event);
        }
    }
}