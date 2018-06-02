package team.circleofcampus.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.adapter.RegisterSpinnerAdapter;
import team.circleofcampus.model.RegisterSpinnerItem;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.register_user_name_edit_text)
    protected EditText userNameEdit;
    @BindView(R.id.register_gender_male_rb)
    protected RadioButton maleRB;
    @BindView(R.id.register_gender_female_rb)
    protected RadioButton femaleRB;
    @BindView(R.id.register_email_edit_text)
    protected EditText emailEdit;
    @BindView(R.id.register_verification_code_edit_text)
    protected EditText verificationCodeEdit;
    @BindView(R.id.register_verification_code_btn)
    protected Button verificationCodeBtn;
    @BindView(R.id.register_pwd_edit_text)
    protected EditText pwdEdit;
    @BindView(R.id.register_confirm_pwd_edit_text)
    protected EditText confirmPwdEdit;
    @BindView(R.id.register_campus_spinner)
    protected Spinner campusSpinner;
    @BindView(R.id.register_faculty_spinner)
    protected Spinner facultySpinner;
    @BindView(R.id.register_btn)
    protected Button registerBtn;

    // 校园下拉菜单部分
    private RegisterSpinnerAdapter campusSpinnerAdapter;
    private ArrayList<RegisterSpinnerItem> campusItems = new ArrayList<RegisterSpinnerItem>();

    // 院系下拉菜单部分
    private RegisterSpinnerAdapter facultySpinnerAdapter;
    private ArrayList<RegisterSpinnerItem> facultyItems = new ArrayList<RegisterSpinnerItem>();

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
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        headerTitle.setText("新用户注册");

        // 校园下拉菜单部分
        campusItems.add(new RegisterSpinnerItem(1, "成都职业技术学院"));
        campusSpinnerAdapter = new RegisterSpinnerAdapter(RegisterActivity.this, campusItems);
        campusSpinner.setAdapter(campusSpinnerAdapter);

        // 院系下拉菜单部分
        facultyItems.add(new RegisterSpinnerItem(1, "软件分院"));
        facultyItems.add(new RegisterSpinnerItem(2, "财经分院"));
        facultyItems.add(new RegisterSpinnerItem(3, "旅游分院"));
        facultyItems.add(new RegisterSpinnerItem(4, "工商管理与房地产分院"));
        facultyItems.add(new RegisterSpinnerItem(4, "医护分院"));
        facultySpinnerAdapter = new RegisterSpinnerAdapter(RegisterActivity.this, facultyItems);
        facultySpinner.setAdapter(facultySpinnerAdapter);
    }

    /**
     * 获取验证码按钮点击事件
     */
    @OnClick(R.id.register_verification_code_btn)
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
     * @param view
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon(View view) {
        onBackPressed();
    }
}
