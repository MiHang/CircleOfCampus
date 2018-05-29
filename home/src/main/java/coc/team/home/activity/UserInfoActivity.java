package coc.team.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import coc.team.home.R;

/**
 * 好友列表界面
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView user;
    private TextView title;
    private ImageView Icon;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private Button send_btn;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        title.setText("好友资料");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        user = (TextView) findViewById(R.id.user);
        title = (TextView) findViewById(R.id.title);
        Icon = (ImageView) findViewById(R.id.Icon);
        account = (TextView) findViewById(R.id.account);
        sex = (TextView) findViewById(R.id.sex);
        college = (TextView) findViewById(R.id.college);
        department = (TextView) findViewById(R.id.department);
        send_btn = (Button) findViewById(R.id.send_btn);

        send_btn.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:

                break;
        }
    }
}
