package coc.team.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coc.team.home.GlideCircleTransform;
import coc.team.home.R;
import coc.team.home.http.HttpHelper;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by 惠普 on 2018-05-11.
 */

public class DFragment extends Fragment{


    private ImageView Icon;
    private Button Log_out;
    int icon = R.drawable.icon;
    private ImageView icon_bg;
    HttpHelper helper;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private TextView UerName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, null);
        initView(view);
        DisplayMetrics dm = getResources().getDisplayMetrics();


        helper = new HttpHelper(getContext());
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "退出", Toast.LENGTH_SHORT).show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String s = helper.getUserInfoByAccount("87654321@qq.com");
                account.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(s);

                            if (jsonObject.getString("result").equals("success")){
                                account.setText(jsonObject.getString("email"));
                                UerName.setText(jsonObject.getString("userName"));
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
                                Toast.makeText(getContext(),"查询失败",Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                });
            }
        }).start();
        return view;
    }

    private void initView(View view) {

        Icon = (ImageView) view.findViewById(R.id.Icon);
        Log_out = (Button) view.findViewById(R.id.Log_out);
        icon_bg = (ImageView) view.findViewById(R.id.icon_bg);
        account = (TextView) view.findViewById(R.id.account);
        sex = (TextView) view.findViewById(R.id.sex);
        college = (TextView) view.findViewById(R.id.college);
        department = (TextView) view.findViewById(R.id.department);
        UerName = (TextView) view.findViewById(R.id.userName);

    }


}
