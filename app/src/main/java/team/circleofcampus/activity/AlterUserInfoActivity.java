package team.circleofcampus.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import team.circleofcampus.http.UserRequest;
import team.circleofcampus.util.SharedPreferencesUtil;

public class AlterUserInfoActivity extends AppCompatActivity {

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.alter_email_edit_text)
    protected EditText idEdit;
    @BindView(R.id.alter_user_name_edit_text)
    protected EditText userName;
    @BindView(R.id.alter_gender_male_rb)
    protected RadioButton maleRb;
    @BindView(R.id.alter_gender_female_rb)
    protected RadioButton femaleRb;
    @BindView(R.id.alter_campus_edit_text)
    protected EditText campusEdit;
    @BindView(R.id.alter_faculty_edit_text)
    protected EditText facultyEdit;

    private LoadingDialog loadingDialog;

    private int uId = 0;
    private String gender;
    private String userNameStr;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : {
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    Toast.makeText(AlterUserInfoActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                } break;
                case 0x0002 : { // 修改成功
                    if (loadingDialog != null) {
                        loadingDialog.loadSuccess();
                    }
                    SharedPreferencesUtil.setUserInfoUpdate(AlterUserInfoActivity.this, true);
                    onBackPressed();
                } break;
                case 0x0003 : { // 用户名被占用
                    if (loadingDialog != null) {
                        loadingDialog.setFailedText("此用户名已被占用");
                        loadingDialog.loadFailed();
                    }
                    userName.requestFocus(); // 用户名文本框获取焦点
                } break;
                case 0x0004 : { // 修改失败
                    if (loadingDialog != null) {
                        loadingDialog.setFailedText("修改失败");
                        loadingDialog.loadFailed();
                    }
                } break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_user_info);
        ButterKnife.bind(this);
        headerTitle.setText("修改信息");

        // 接收传递来的json参数并进行初始化操作
        try {
            Intent intent = getIntent();
            JSONObject jsonParam = new JSONObject(intent.getStringExtra("param"));
            uId = jsonParam.getInt("uId");
            idEdit.setText(jsonParam.getString("email"));
            userNameStr = jsonParam.getString("userName");
            userName.setText(userNameStr);
            gender = jsonParam.getString("gender");
            if (gender.equals("female")) {
                femaleRb.setChecked(true);
            } else {
                maleRb.setChecked(true);
            }
            campusEdit.setText(jsonParam.getString("campusName"));
            facultyEdit.setText(jsonParam.getString("facultyName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存按钮点击事件
     */
    @OnClick(R.id.alter_user_info_btn)
    public void onClickAlterBtn() {

        if (userName.getText().toString().trim().equals("")) {
            Toast.makeText(AlterUserInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        } else if (userName.getText().toString().trim().equals(userNameStr) &&
                ((gender.equals("female") && femaleRb.isChecked()) || (gender.equals("male") && maleRb.isChecked()))) {
            Toast.makeText(AlterUserInfoActivity.this, "您未做任何修改", Toast.LENGTH_SHORT).show();
        } else {
            // 加载对话框
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setLoadingText("正在修改")
                    .setSuccessText("修改成功")
                    .closeSuccessAnim()
                    .closeFailedAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

            new Thread(){
                @Override
                public void run() {
                    String gender = null;
                    if (maleRb.isChecked()) gender = "male";
                    else if (femaleRb.isChecked()) gender = "female";
                    String result = UserRequest.updateUserInfo(uId, userName.getText().toString().trim(), gender);
                    if (null != result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            result = json.getString("result");
                            if (result.equals("success")) {
                                handler.sendEmptyMessage(0x0002);
                            } else if (result.equals("occupy")) {
                                handler.sendEmptyMessage(0x0003);
                            } else {
                                handler.sendEmptyMessage(0x0004);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(0x0001);
                    }
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
