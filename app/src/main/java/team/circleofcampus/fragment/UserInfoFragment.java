package team.circleofcampus.fragment;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.Interface.MoreFragmentListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.activity.ContactActivity;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.view.FontTextView;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

/**
 * "我的"界面
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener , OnItemClickListener, OnDismissListener{

    private AlertView mAlertView; // 避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
    HttpHelper helper;
    private CircleImageView Icon;
    private FontTextView NickName;
    private FontTextView UserName;
    private FontTextView account;
    private FontTextView sex;
    private FontTextView college;
    private FontTextView department;
    private Button send_btn;
    private View view;
    String name;
    String username;
    private Button delete_btn;
    SharedPreferencesUtil sharedPreferencesUtil;
    String email;
    JSONObject jsonObject;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_userinfo, null);
        initView(view);
        mAlertView = new AlertView("提示", "确认删除?", "取消", new String[]{"确定"}, null, getContext(), AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener((OnDismissListener) this);

        final ContactActivity activity = (ContactActivity) getActivity();

        activity.contactFragment.setListener(new MoreFragmentListener() {
            @Override
            public void setValueExtra(int position, String[] str) {
                GetServerData(str);

                //延时2毫秒刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.header_title.setText("好友资料");
                        activity.header_right_text.setVisibility(View.GONE);
                        activity.header_left_image.setVisibility(View.GONE);
                        activity.header_left_image.setVisibility(View.VISIBLE);
                    }
                }, 200);
                activity.MyViewPager.setCurrentItem(position, true);
                send_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("receive", username);
                        intent.putExtra("nickName", name);
                        startActivity(intent);
                    }
                });

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView(view);
        return view;
    }


    /**
     * 查询服务器数据
     */
    public void GetServerData(final String str[]) {
        helper = new HttpHelper(getContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.getUserInfoByAccount(str[0]);
                account.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Log.e("tag", "yonghu" + s);
                            if (jsonObject.getString("result").equals("success")) {
                                account.setText(jsonObject.getString("email"));
                                username = jsonObject.getString("userName");
                                if (jsonObject.getString("userName").equals(str[1])) {// 未设置备注
                                    UserName.setText(jsonObject.getString("userName"));
                                    NickName.setText("备注：无");
                                    name = jsonObject.getString("userName");
                                } else {
                                    UserName.setText(str[1]);
                                    NickName.setText("用户名：" + jsonObject.getString("userName"));
                                    name = str[1];
                                }
                                department.setText(jsonObject.getString("facultyName"));
                                college.setText(jsonObject.getString("campusName"));

                                int res = R.drawable.woman;
                                if (jsonObject.has("gender")) {
                                    if (jsonObject.getString("gender").equals("male")) {
                                        sex.setText("男");
                                        res = R.drawable.man;
                                    } else {
                                        sex.setText("女");
                                    }
                                }
                                Glide.with(getContext())
                                        .load(HttpRequest.URL + "/res/img/" + jsonObject.getString("userName"))
                                        .asBitmap()
                                        .error(res)
                                        .into(Icon);
                            } else {
                                Toast.makeText(getContext(), "查询失败", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    private void initView(View view) {
        Icon = (CircleImageView) view.findViewById(R.id.Icon);

        NickName = (FontTextView) view.findViewById(R.id.NickName);
        UserName = (FontTextView) view.findViewById(R.id.UserName);
        account = (FontTextView) view.findViewById(R.id.account);
        sex = (FontTextView) view.findViewById(R.id.sex);
        college = (FontTextView) view.findViewById(R.id.college);
        department = (FontTextView) view.findViewById(R.id.department);
        send_btn = (Button) view.findViewById(R.id.send_btn);
        delete_btn = (Button) view.findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_btn:
                mAlertView.show();
                break;
        }
    }

    @Override
    public void onDismiss(Object o) {
    }

    @Override
    public void onItemClick(Object o, int position) {
    if(position==0){
        sharedPreferencesUtil=new SharedPreferencesUtil();
        email=sharedPreferencesUtil.getAccount(getContext());
        if (email==null){
            return;
        }
                        AsyncTask.execute(new Runnable() {
                @Override
                public void run() {


                    String s=helper.deleteFriend(email,account.getText().toString());
                    if (s!=null){


                        try {
                            jsonObject = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        delete_btn.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    if (jsonObject.getString("result").equals("success")){
                                      Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();


                                    }else  if (jsonObject.getString("result").equals("error")){
                                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();

                                    }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    }
                }
            });
        }

    }
}
