package coc.team.home.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import coc.team.home.R;
import coc.team.home.http.HttpHelper;

/**
 * 好友列表界面
 */
public class UserInfoActivity extends AppCompatActivity {


    HttpHelper helper;
    private TextView header_left_text;
    private ImageView header_left_image;
    private TextView header_title;
    private TextView header_right_text;
    private ImageView header_right_image;
    private ImageView Icon;
    private TextView NickName;
    private TextView UserName;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        header_title.setText("好友资料");
        header_left_image.setVisibility(View.VISIBLE);
        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ContactActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                finish();
            }
        });
        Intent intent = getIntent();
        String nick = intent.getStringExtra("NickName");
        if (nick != null) {
            NickName.setText(nick);
        } else {
            NickName.setText("暂无备注");
        }
        GetServerData();

    }

    /**
     * 查询服务器数据
     */
    public void GetServerData() {
        helper = new HttpHelper(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.getUserInfoByAccount("87654321@qq.com");
                account.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(s);

                            if (jsonObject.getString("result").equals("success")) {
                                account.setText(jsonObject.getString("email"));
                                UserName.setText("昵称:" + jsonObject.getString("userName"));
                                department.setText(jsonObject.getString("facultyName"));
                                college.setText(jsonObject.getString("campusName"));

                                if (jsonObject.has("gender")) {
                                    if (jsonObject.getString("gender").equals("male")) {
                                        sex.setText("男");
                                    } else {
                                        sex.setText("女");
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                });
            }
        });
    }




    private void initView() {
        header_left_text = (TextView) findViewById(R.id.header_left_text);
        header_left_image = (ImageView) findViewById(R.id.header_left_image);
        header_title = (TextView) findViewById(R.id.header_title);
        header_right_text = (TextView) findViewById(R.id.header_right_text);
        header_right_image = (ImageView) findViewById(R.id.header_right_image);
        Icon = (ImageView) findViewById(R.id.Icon);
        NickName = (TextView) findViewById(R.id.NickName);
        UserName = (TextView) findViewById(R.id.UserName);
        account = (TextView) findViewById(R.id.account);
        sex = (TextView) findViewById(R.id.sex);
        college = (TextView) findViewById(R.id.college);
        department = (TextView) findViewById(R.id.department);
        send_btn = (Button) findViewById(R.id.send_btn);

    }
}
