package team.circleofcampus.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;

public class ForgetPwdActivity extends AppCompatActivity {

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.forget_pwd_email_edit_text)
    protected EditText emailEdit;
    @BindView(R.id.forget_verification_code_edit_text)
    protected EditText verificationCode;
    @BindView(R.id.forget_verification_code_btn)
    protected Button verificationCodeBtn;
    @BindView(R.id.forget_new_pwd_edit_text)
    protected EditText newPwdEdit;
    @BindView(R.id.forget_confirm_pwd_edit_text)
    protected EditText confirmPwdEdit;
    @BindView(R.id.forget_reset_pwd_btn)
    protected Button resetPwdBtn;

    private boolean isGetVerificationCode = false; // 是否获取了验证码
    private int countDown = 60; // 倒计时60秒

    // handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (0x0001 == msg.what) {
                countDown --;
                verificationCodeBtn.setText(countDown + "s后获取");
                if (countDown == 1) { // 倒计时结束
                    isGetVerificationCode = false;
                }
            } else if (0x0002 == msg.what) {
                countDown = 60;
                verificationCodeBtn.setBackgroundResource(R.drawable.shape_email_code_btn);
                verificationCodeBtn.setText("获取验证码");
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        headerTitle.setText("重置密码");
    }

    /**
     * 获取验证码按钮点击事件
     */
    @OnClick(R.id.forget_verification_code_btn)
    public void onClickVerificationCodeBtn() {
        if (!isGetVerificationCode) {
            isGetVerificationCode = true;
            verificationCodeBtn.setBackgroundResource(R.drawable.shape_code_unable_btn);
            // 计时线程
            new Thread() {
                @Override
                public void run() {
                    while (isGetVerificationCode) {
                        try {
                            handler.sendEmptyMessage(0x0001);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(0x0002);
                }
            }.start();
        }
    }

    /**
     * 标题栏返回按钮图标点击事件
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon() {
        onBackPressed();
    }
}
