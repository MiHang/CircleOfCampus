package team.circleofcampus.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.view.CircleImageView;

/**
 * Created by 惠普 on 2018-05-11.
 */
public class MineFragment extends Fragment {

    private View view;
    private CircleImageView Icon;
    private Button Log_out;
    int icon = R.drawable.icon;
    private ImageView icon_bg;
    HttpHelper helper;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private EditText userName;
    private TextView QR;

    private RadioGroup genderRadioGroup;
    private RadioButton maleRb;
    private RadioButton femaleRb;

    FragmentSwitchListener switchListener;

    public FragmentSwitchListener getSwitchListener() {
        return switchListener;
    }

    public void setSwitchListener(FragmentSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view!=null){
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mine, null);
        initView(view);

        helper = new HttpHelper(getContext());
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNotifi = new Intent(getActivity(), ChatActivity.class);

                getActivity().startActivity(intentNotifi);
                Toast.makeText(getContext(), "退出", Toast.LENGTH_SHORT).show();
            }
        });
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
                                userName.setText(jsonObject.getString("userName"));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

    private void initView(View view) {

        Icon = (CircleImageView) view.findViewById(R.id.Icon);
        Log_out = (Button) view.findViewById(R.id.Log_out);
        icon_bg = (ImageView) view.findViewById(R.id.icon_bg);
        account = (TextView) view.findViewById(R.id.account);
        sex = (TextView) view.findViewById(R.id.sex);
        genderRadioGroup = (RadioGroup) view.findViewById(R.id.mine_gender_radio_group);
        maleRb = (RadioButton) view.findViewById(R.id.mine_gender_male_rb);
        femaleRb = (RadioButton) view.findViewById(R.id.mine_gender_female_rb);
        college = (TextView) view.findViewById(R.id.college);
        department = (TextView) view.findViewById(R.id.department);
        userName = (EditText) view.findViewById(R.id.userName);
        userName.setEnabled(false);
        QR = (TextView) view.findViewById(R.id.QR);
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchListener!=null){
                    switchListener.displayThisFragment(true);
                }
            }

        });
    }



}
