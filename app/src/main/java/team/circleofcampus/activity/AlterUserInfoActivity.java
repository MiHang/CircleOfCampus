package team.circleofcampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;

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

    private int uId = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            userName.setText(jsonParam.getString("userName"));
            String gender = jsonParam.getString("gender");
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
