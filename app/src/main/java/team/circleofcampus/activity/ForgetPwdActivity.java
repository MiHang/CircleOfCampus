package team.circleofcampus.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.util.EmailUtil;

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

    private boolean isGetVerificationCode = false; // 是否获取了验证码
    private int countDown = 60; // 倒计时60秒

    private LoadingDialog loadingDialog;

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
            } else if (0x0003 == msg.what) {
                if (loadingDialog != null) {
                    loadingDialog.close();
                }
                Toast.makeText(ForgetPwdActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
            } else if (0x0004 == msg.what) {
                Toast.makeText(ForgetPwdActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
            } else if (0x0005 == msg.what) {
                Toast.makeText(ForgetPwdActivity.this, "验证码发送失败，请检查邮箱是否正确", Toast.LENGTH_SHORT).show();
                isGetVerificationCode = false;
            } else if (0x0006 == msg.what) { // 密码重置成功
                loadingDialog.loadSuccess();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(2000);
                            handler.sendEmptyMessage(0x0010);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else if (0x0007 == msg.what) { // 密码重置失败
                loadingDialog.setFailedText("密码重置失败, 请检查邮箱是否输入正确");
                loadingDialog.loadFailed();
            } else if (0x0008 == msg.what) { // 验证码超时
                loadingDialog.setFailedText("验证码超时");
                loadingDialog.loadFailed();
            } else if (0x0009 == msg.what) { // 验证码错误
                loadingDialog.setFailedText("验证码错误");
                loadingDialog.loadFailed();
            } else if (0x0010 == msg.what) { // 页面返回 - 返回登陆页面
                onBackPressed();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loadingDialog != null) {
            loadingDialog.close();
        }
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
     * 重置密码按钮点击事件监听
     */
    @OnClick(R.id.forget_reset_pwd_btn)
    public void onClickResetPwdBtn() {
        if ("".equals(emailEdit.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(verificationCode.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(newPwdEdit.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (!newPwdEdit.getText().toString().trim().equals(confirmPwdEdit.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 加载对话框
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setLoadingText("重置密码中")
                    .setSuccessText("密码重置成功")
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

            // 联网线程
            new Thread() {
                @Override
                public void run() {
                    String result = LoginRequest.resetPwd(
                            emailEdit.getText().toString().trim(),
                            newPwdEdit.getText().toString().trim(),
                            verificationCode.getText().toString().trim());
                    if (null != result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            result = json.getString("result");
                            if ("success".equals(result)) { // 重置密码成功
                                handler.sendEmptyMessage(0x0006);
                            } else if ("error".equals(result)) { // 重置密码失败
                                handler.sendEmptyMessage(0x0007);
                            } else if ("timeout".equals(result)) { // 验证码超时
                                handler.sendEmptyMessage(0x0008);
                            } else { // 验证码错误
                                handler.sendEmptyMessage(0x0009);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(0x0003);
                    }
                }
            }.start();
        }
    }

    /**
     * 获取验证码按钮点击事件
     */
    @OnClick(R.id.forget_verification_code_btn)
    public void onClickVerificationCodeBtn() {

        if ("".equals(emailEdit.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "请先填写邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        } else if (!EmailUtil.isValidEmail(emailEdit.getText().toString().trim())) {
            Toast.makeText(ForgetPwdActivity.this, "邮箱地址格式不正确！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isGetVerificationCode) {
            isGetVerificationCode = true;
            verificationCodeBtn.setBackgroundResource(R.drawable.shape_code_unable_btn);
            // 联网线程
            new Thread() {
                @Override
                public void run() {
                    String result = LoginRequest.getForgotPwdVerificationCode(emailEdit.getText().toString().trim());
                    if (result != null) {
                        try {
                            JSONObject json = new JSONObject(result);
                            result = json.getString("result");
                            if ("success".equals(result)) {
                                handler.sendEmptyMessage(0x0004);
                            } else {
                                handler.sendEmptyMessage(0x0005);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(0x0003);
                    }
                }
            }.start();
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
