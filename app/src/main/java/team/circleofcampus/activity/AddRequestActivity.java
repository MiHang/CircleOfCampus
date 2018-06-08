package team.circleofcampus.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.R;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.view.FontTextView;

public class AddRequestActivity extends AppCompatActivity implements OnItemClickListener, OnDismissListener {

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
    EditText edit;
    private AlertView mAlertViewExt;//窗口拓展例子
    private FontTextView NickName;
    SharedPreferencesUtil sharedPreferencesUtil;
    String user;
    boolean isFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rquest);
        initView();
        Intent intent = getIntent();
        final String friend = intent.getStringExtra("user2");
        if (friend == null) {
            finish();
        }
         user=sharedPreferencesUtil.getAccount(this);
         helper = new HttpHelper(this);

            //查询两人是否好友
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final String s = helper.QueryIsFriend(user, friend);

                    account.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (!s.equals("")) {

                                    if (jsonObject.getString("result").equals("no")) {//不是好友
                                        send_btn.setText("添加好友");
                                        NickName.setVisibility(View.GONE);
                                        Toast.makeText(AddRequestActivity.this, "不是好友", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(AddRequestActivity.this, "是好友", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    });

                }
            });




        mAlertViewExt = new AlertView("提示", "请填写你的申请理由",
                "取消", null, new String[]{"完成"}, AddRequestActivity.this, AlertView.Style.Alert, AddRequestActivity.this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.alertext_form, null);
        edit = (EditText) extView.findViewById(R.id.edit);
        mAlertViewExt.addExtView(extView);

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
                if (send_btn.getText().equals("发送消息")){
                    Intent intent=new Intent(AddRequestActivity.this,ChatActivity.class);
                    startActivity(intent);
                    finish();
                }else{//添加好友

                    mAlertViewExt.show();
                }


            }
        });


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.getUserInfoByAccount(friend);
                account.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(s);

                            if (jsonObject.getString("result").equals("success")) {
                                account.setText(jsonObject.getString("email"));
                                UserName.setText(jsonObject.getString("userName"));
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
        UserName = (TextView) findViewById(R.id.UserName);
        account = (TextView) findViewById(R.id.account);
        sex = (TextView) findViewById(R.id.sex);
        college = (TextView) findViewById(R.id.college);
        department = (TextView) findViewById(R.id.department);
        send_btn = (Button) findViewById(R.id.send_btn);
        NickName = (FontTextView) findViewById(R.id.NickName);

    }

    @Override
    public void onItemClick(Object o, int position) {

        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            final String name = edit.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "您未填写申请原因", Toast.LENGTH_SHORT).show();

            } else {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("提示");
                dialog.setMessage("提交申请中");
                dialog.show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String s = helper.requestAddFriend(user, account.getText().toString(), name);
                        account.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (!s.equals("")) {
                                        isFlag=true;
                                        if (jsonObject.getInt("deal") == 0) {
                                            Toast.makeText(getApplicationContext(), "请勿重复提交好友申请", Toast.LENGTH_LONG).show();
                                        } else {
                                            if (jsonObject.getString("result").equals("success")) {
                                                Toast.makeText(getApplicationContext(), "好友提交申请成功", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "好友提交申请失败", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "好友提交申请失败", Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFlag){
                            dialog.dismiss();
                        }

                    }
                },5000);

            }
        }
    }

    @Override
    public void onDismiss(Object o) {
    }

}
