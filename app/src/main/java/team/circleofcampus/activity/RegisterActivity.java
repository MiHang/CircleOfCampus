package team.circleofcampus.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import team.circleofcampus.R;
import team.circleofcampus.adapter.RegisterSpinnerAdapter;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.model.RegisterSpinnerItem;
import team.circleofcampus.util.EmailUtil;

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

    private LoadingDialog loadingDialog; // 加载对话框;

    // handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (0x0000 == msg.what) {
                getCampuses();
            } else if (0x0001 == msg.what) {
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
                Toast.makeText(RegisterActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                isGetVerificationCode = false;
            } else if (0x0004 == msg.what) {
                Toast.makeText(RegisterActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
            } else if (0x0005 == msg.what) {
                Toast.makeText(RegisterActivity.this, "验证码发送失败，请检查邮箱是否正确", Toast.LENGTH_SHORT).show();
                isGetVerificationCode = false;
            } else if (0x0006 == msg.what) {
                Toast.makeText(RegisterActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
            } else if (0x0007 == msg.what) {
                userNameEdit.requestFocus();
                Toast.makeText(RegisterActivity.this, "此用户名已被占用", Toast.LENGTH_SHORT).show();
            } else if (0x0009 == msg.what) { // 更新学校下拉框
                campusSpinnerAdapter.notifyDataSetChanged();
                getFacultyItems(campusItems.get(0).getItemId());
            } else if (0x0010 == msg.what) { // 更新院系下拉框
                facultySpinnerAdapter.notifyDataSetChanged();
            } else if (0x0011 == msg.what) { // 注册成功
                if (loadingDialog != null) {
                    loadingDialog.loadSuccess();
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                sleep(3000);
                                handler.sendEmptyMessage(0x0014);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            } else if (0x0012 == msg.what) { // 注册失败
                if (loadingDialog != null) {
                    loadingDialog.setFailedText((String)msg.obj);
                    loadingDialog.loadFailed();
                }
            } else if (0x0013 == msg.what) { // 开始注册
                register();
            } else if (0x0014 == msg.what) { // 跳转到登陆页面
                toLogin();
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

        // 默认设置性别为男
        maleRB.setChecked(true);

        // 校园下拉菜单部分
        campusSpinnerAdapter = new RegisterSpinnerAdapter(RegisterActivity.this, campusItems);
        campusSpinner.setAdapter(campusSpinnerAdapter);
        // 学校下拉列表Item选择改变时的监听事件
        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 改变选择的学校，重新获取院系信息
                getFacultyItems((int)id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 获取支持注册的所有学校
        getCampuses();

        // 院系下拉菜单部分
        facultySpinnerAdapter = new RegisterSpinnerAdapter(RegisterActivity.this, facultyItems);
        facultySpinner.setAdapter(facultySpinnerAdapter);

        // 用户名输入框焦点监听
        userNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // 失去焦点
                    if (!"".equals(userNameEdit.getText().toString().trim())) {
                        isUsableName(0x0008);
                    }
                }
            }
        });
    }

    /**
     * 页面跳转 - 跳转到登陆页面
     */
    protected void toLogin() {
        onBackPressed();
    }

    /**
     * 获取支持注册的所有学校
     */
    private void getCampuses() {
        new Thread() {
            @Override
            public void run() {
                String result = LoginRequest.getCampuses();
                if (result != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        campusItems.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = new JSONObject(jsonArray.get(i).toString());
                            campusItems.add(new RegisterSpinnerItem(json.getInt("campusId"),
                                    json.getString("campusName")));
                        }
                        handler.sendEmptyMessage(0x0009);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        sleep(10000);
                        handler.sendEmptyMessage(0x0000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 获取院系信息
     * @param campusId - 学校ID
     */
    private void getFacultyItems(final int campusId) {
        new Thread() {
            @Override
            public void run() {
                String result = LoginRequest.getFaculties(campusId);
                if (null != result) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        facultyItems.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = new JSONObject(jsonArray.get(i).toString());
                            facultyItems.add(new RegisterSpinnerItem(json.getInt("facultyId"),
                                    json.getString("facultyName")));
                        }
                        handler.sendEmptyMessage(0x0010);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0006);
                }
            }
        }.start();
    }

    /**
     * 判断用户名是否可用
     */
    private void isUsableName(final int what){
        // 联网线程
        new Thread() {
            @Override
            public void run() {
                String result = LoginRequest.getUsableName(userNameEdit.getText().toString().trim());
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");
                        if ("occupy".equals(result)) {
                            handler.sendEmptyMessage(0x0007);
                        } else {
                            handler.sendEmptyMessage(what);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0006);
                }
            }
        }.start();
    }

    /**
     * 注册
     */
    private void register() {

        // 加载对话框
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText("正在注册")
                .setSuccessText("注册成功")
                .show();

        new Thread() {
            @Override
            public void run() {
                String gender = null;
                if (maleRB.isChecked()) gender = "male";
                else if (femaleRB.isChecked()) gender = "female";
                String result = LoginRequest.register(
                        userNameEdit.getText().toString().trim(),
                        gender,
                        emailEdit.getText().toString().trim(),
                        pwdEdit.getText().toString().trim(),
                        verificationCodeEdit.getText().toString().trim(),
                        (int)facultySpinner.getSelectedItemId());
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");

                        Message msg = new Message();
                        if ("success".equals(result)) {
                            handler.sendEmptyMessage(0x0011);
                        } else if ("error".equals(result)) {
                            msg.what = 0x0012;
                            msg.obj = "注册失败";
                            handler.sendMessage(msg);
                        } else if ("no_code".equals(result) || "code_error".equals(result)) {
                            msg.what = 0x0012;
                            msg.obj = "验证码错误，请重新获取验证码";
                            handler.sendMessage(msg);
                        } else if ("timeout".equals(result)) {
                            msg.what = 0x0012;
                            msg.obj = "验证码超时，请重新获取验证码";
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0006);
                }
            }
        }.start();
    }

    /**
     * 注册按钮点击事件
     */
    @OnClick(R.id.register_btn)
    public void onClickRegisterBtn() {

        if ("".equals(userNameEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(emailEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(verificationCodeEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(pwdEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (userNameEdit.getText().toString().trim().length() < 3
                || userNameEdit.getText().toString().trim().length() > 10) {
            Toast.makeText(RegisterActivity.this, "用户名必须在[3,10]位之间", Toast.LENGTH_SHORT).show();
            return;
        } else if (pwdEdit.getText().toString().trim().length() < 6
                || pwdEdit.getText().toString().trim().length() > 12) {
            Toast.makeText(RegisterActivity.this, "密码必须在[6,12]位之间", Toast.LENGTH_SHORT).show();
            return;
        } else if (!pwdEdit.getText().toString().trim().equals(confirmPwdEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        } else {
            isUsableName(0x0013);
        }
    }

    /**
     * 获取验证码按钮点击事件
     */
    @OnClick(R.id.register_verification_code_btn)
    public void onClickVerificationCodeBtn() {

        if ("".equals(emailEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "请先填写邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        } else if (!EmailUtil.isValidEmail(emailEdit.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "邮箱地址格式不正确！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isGetVerificationCode) {
            isGetVerificationCode = true;
            verificationCodeBtn.setBackgroundResource(R.drawable.shape_code_unable_btn);
            // 联网线程
            new Thread() {
                @Override
                public void run() {
                    String result = LoginRequest.getRegisterVerificationCode(emailEdit.getText().toString().trim());
                    if (result != null) {
                        try {
                            JSONObject json = new JSONObject(result);
                            result = json.getString("result");
                            if ("registered".equals(result)) {
                                handler.sendEmptyMessage(0x0003);
                            } else if ("success".equals(result)) {
                                handler.sendEmptyMessage(0x0004);
                            } else if ("error".equals(result) || "invalid".equals(result)) {
                                handler.sendEmptyMessage(0x0005);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(0x0006);
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
     * @param view
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon(View view) {
        onBackPressed();
    }
}
