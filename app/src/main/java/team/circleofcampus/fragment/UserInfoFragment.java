package team.circleofcampus.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.R;
import team.circleofcampus.http.HttpHelper;


/**
 * Created by 惠普 on 2018-05-11.
 */
public class UserInfoFragment extends Fragment {

    HttpHelper helper;
    private ImageView Icon;
    private TextView NickName;
    private TextView UserName;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private Button send_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userinfo, null);
        initView(view);

        Intent intent = getActivity().getIntent();
        String nick = intent.getStringExtra("NickName");
        if (nick != null) {
            NickName.setText(nick);
        } else {
            NickName.setText("暂无备注");
        }
        GetServerData();

        return view;
    }

    /**
     * 查询服务器数据
     */
    public void GetServerData() {
        helper = new HttpHelper(getContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String s = helper.getUserInfoByAccount("jaye@163.com");
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
        Icon = (ImageView) view.findViewById(R.id.Icon);

        NickName = (TextView) view.findViewById(R.id.NickName);
        UserName = (TextView) view.findViewById(R.id.UserName);
        account = (TextView) view.findViewById(R.id.account);
        sex = (TextView) view.findViewById(R.id.sex);
        college = (TextView) view.findViewById(R.id.college);
        department = (TextView) view.findViewById(R.id.department);
        send_btn = (Button) view.findViewById(R.id.send_btn);
    }


}
