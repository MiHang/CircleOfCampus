package team.circleofcampus.fragment;

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

import com.common.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.Interface.MoreFragmentListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.activity.ContactActivity;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.view.FontTextView;


/**
 * "我的"界面
 */
public class UserInfoFragment extends Fragment {

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

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
            Toast.makeText(getContext(), "结束", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_userinfo, null);
        initView(view);

        final ContactActivity activity= (ContactActivity) getActivity();

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
                activity.MyViewPager.setCurrentItem(position,true);
                send_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("receive",account.getText());
                        startActivity(intent);
                    }
                });

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                            Log.e("tag" ,"yonghu"+s);

                            if (jsonObject.getString("result").equals("success")) {
                                account.setText(jsonObject.getString("email"));
                                UserName.setText("昵称:" + jsonObject.getString("userName"));
                                if (jsonObject.getString("userName").equals(str[1])){//未设置备注
                                    NickName.setText("备注:无");
                                }else{
                                    NickName.setText(str[1]);
                                }
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
    }

}
