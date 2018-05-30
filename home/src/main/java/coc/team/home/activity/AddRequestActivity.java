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

public class AddRequestActivity extends AppCompatActivity  {

    private TextView header_left_text;
    private ImageView header_left_image;
    private TextView header_title;
    private TextView header_right_text;
    private ImageView header_right_image;
    private ImageView Icon;
    private TextView UserName;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private Button send_btn;
    HttpHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rquest);
        initView();

        //        Intent intent = getIntent();
//        String Account = intent.getStringExtra("Account");
//        if (Account == null) {
//            finish();
//        }

        header_left_image.setVisibility(View.VISIBLE);
        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        header_title.setText("添加好友");
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String s = helper.getUserInfoByAccount("jaye@163.com");
                        account.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject jsonObject = new JSONObject(s);

                                    if (jsonObject.getString("result").equals("success")){
                                        account.setText(jsonObject.getString("email"));
                                        UserName.setText(jsonObject.getString("userName"));
                                        department.setText(jsonObject.getString("facultyName"));
                                        college.setText(jsonObject.getString("campusName"));

                                        if (jsonObject.has("gender")){
                                            if (jsonObject.getString("gender").equals("male")){
                                                sex.setText("男");
                                            }else{
                                                sex.setText("女");
                                            }
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),"查询失败",Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                    }
                });
            }
        });

        helper = new HttpHelper(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.getUserInfoByAccount("jaye@163.com");
                account.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(s);

                            if (jsonObject.getString("result").equals("success")){
                                account.setText(jsonObject.getString("email"));
                                UserName.setText(jsonObject.getString("userName"));
                                department.setText(jsonObject.getString("facultyName"));
                                college.setText(jsonObject.getString("campusName"));

                                if (jsonObject.has("gender")){
                                    if (jsonObject.getString("gender").equals("male")){
                                        sex.setText("男");
                                    }else{
                                        sex.setText("女");
                                    }
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"查询失败",Toast.LENGTH_LONG).show();
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
        UserName = (TextView) findViewById(R.id.UserName);
        account = (TextView) findViewById(R.id.account);
        sex = (TextView) findViewById(R.id.sex);
        college = (TextView) findViewById(R.id.college);
        department = (TextView) findViewById(R.id.department);
        send_btn = (Button) findViewById(R.id.send_btn);


    }


}
