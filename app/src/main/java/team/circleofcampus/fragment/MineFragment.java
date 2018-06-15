package team.circleofcampus.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.activity.LoginActivity;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.view.CircleImageView;

/**
 * Created by 惠普 on 2018-05-11.
 */
public class MineFragment extends Fragment {

    private View view;
    private CircleImageView Icon;
    private Button Log_out;
    private ImageView icon_bg;
    HttpHelper helper;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private EditText userName;
    private TextView QR;

    String Account;
    int res;
    LoadingDialog dialog;

    FragmentSwitchListener switchListener;
    SharedPreferencesUtil sharedPreferencesUtil;


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

        helper=new HttpHelper(getContext());
        sharedPreferencesUtil=new SharedPreferencesUtil();
        Account=sharedPreferencesUtil.getAccount(getContext());
        if (Account!=null){
            dialog = new LoadingDialog(getContext());
            dialog.setLoadingText("加载中")
                    .setSuccessText("加载成功")//显示加载成功时的文字
                    .setFailedText("加载失败")
                    .closeSuccessAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final String s = helper.getUserInfoByAccount(Account);
                    account.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(s);

                                if (jsonObject.getString("result").equals("success")) {


                                        if (jsonObject.getString("gender").equals("male")) {
                                            sex.setText("男");
                                            res=R.drawable.man;
                                        } else {
                                            sex.setText("女");
                                            res=R.drawable.woman;
                                        }
                                    account.setText(jsonObject.getString("email"));
                                    userName.setText(jsonObject.getString("userName"));
                                    department.setText(jsonObject.getString("facultyName"));
                                    college.setText(jsonObject.getString("campusName"));
                                    dialog.loadSuccess();
                                    Glide.with(getContext())
                                            .load(helper.getPath()+"/res/img/"+Account)
                                            .placeholder(res)
                                            .error(res)
                                            .into(Icon);
                                } else {
                                    dialog.loadFailed();
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
        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 清空保存的登陆相关信息
                SharedPreferencesUtil.setUID(getContext(), 0);
                SharedPreferencesUtil.setAccount(getContext(), "");
                SharedPreferencesUtil.setLoginTime(getContext(), 0);

                // 跳转到登陆页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
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
        college = (TextView) view.findViewById(R.id.college);
        department = (TextView) view.findViewById(R.id.department);
        userName = (EditText) view.findViewById(R.id.userName);
        userName.setEnabled(false);
        QR = (TextView) view.findViewById(R.id.QR);
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Account!=null){
                    if (switchListener!=null){
                        switchListener.displayThisFragment(true);
                    }
                }else{
                    Toast.makeText(getContext(),"您暂未登录",Toast.LENGTH_LONG).show();
                }

            }

        });
    }



}
