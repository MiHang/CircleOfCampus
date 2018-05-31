package coc.team.home.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import coc.team.home.R;
import coc.team.home.http.HttpHelper;

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
    EditText etName;
    private AlertView mAlertViewExt;//窗口拓展例子

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rquest);
        initView();
        //查询两人是否好友
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.QueryIsFriend("12","12");
                account.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (!s.equals("")){
                                if (jsonObject.getString("result").equals("yes")){

                                }else{

                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                });
            }
        });

        //        Intent intent = getIntent();
//        String Account = intent.getStringExtra("Account");
//        if (Account == null) {
//            finish();
//        }


        mAlertViewExt = new AlertView("提示", "请填写你的申请理由",
                "取消", null, new String[]{"完成"}, AddRequestActivity.this, AlertView.Style.Alert,AddRequestActivity. this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.alertext_form,null);
        etName = (EditText) extView.findViewById(R.id.etName);
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
                //拓展窗口
                mAlertViewExt.show();
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        final String s = helper.getUserInfoByAccount("jaye@163.com");
//                        account.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                try {
//                                    JSONObject jsonObject = new JSONObject(s);
//
//                                    if (jsonObject.getString("result").equals("success")){
//                                        account.setText(jsonObject.getString("email"));
//                                        UserName.setText(jsonObject.getString("userName"));
//                                        department.setText(jsonObject.getString("facultyName"));
//                                        college.setText(jsonObject.getString("campusName"));
//
//                                        if (jsonObject.has("gender")){
//                                            if (jsonObject.getString("gender").equals("male")){
//                                                sex.setText("男");
//                                            }else{
//                                                sex.setText("女");
//                                            }
//                                        }
//                                    }else{
//                                        Toast.makeText(getApplicationContext(),"查询失败",Toast.LENGTH_LONG).show();
//                                    }
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//
//                            }
//
//                        });
//                    }
//                });
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

    @Override
    public void onItemClick(Object o,int position) {

        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if(o == mAlertViewExt && position != AlertView.CANCELPOSITION){
            final String name = etName.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(this, "您未填写申请原因", Toast.LENGTH_SHORT).show();

            } else{
                final ProgressDialog dialog=new ProgressDialog(this);
                dialog.setTitle("提示");
                dialog.setMessage("提交申请中");
                dialog.show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String s = helper.requestAddFriend("jaye@163.com","demo@163.com",name);
                        account.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (!s.equals("")){
                                        if (jsonObject.getInt("deal")==0){
                                            Toast.makeText(getApplicationContext(),"请勿重复提交好友申请",Toast.LENGTH_LONG).show();
                                        }else{
                                            if (jsonObject.getString("result").equals("success")){
                                                Toast.makeText(getApplicationContext(),"好友提交申请成功",Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(),"好友提交申请失败",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),"好友提交申请失败",Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                    }
                });
//                Toast.makeText(this, "申请原因:"+name, Toast.LENGTH_SHORT).show();


            }
        }
    }

    @Override
    public void onDismiss(Object o) {}

}
