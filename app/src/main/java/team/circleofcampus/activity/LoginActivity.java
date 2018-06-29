package team.circleofcampus.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;
import team.circleofcampus.Interface.OnItemListPopupClickListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.HistoryAccountListPopupAdapter;
import team.circleofcampus.dao.MyPublishSocietyCircleDao;
import team.circleofcampus.dao.UserDao;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.util.SharedPreferencesUtil;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_account_edit_text)
    protected EditText account;
    @BindView(R.id.login_pwd_edit_text)
    protected EditText password;

    private LoadingDialog loadingDialog;

    private ListPopupWindow lpw; // 列表弹出框
    private HistoryAccountListPopupAdapter historyAccountListPopupAdapter;
    // 弹出框数据 - 历史登陆账号
    private ArrayList<String> historyAccounts = new ArrayList<String>();

    // handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x0001) {
                if (loadingDialog != null) {
                    loadingDialog.close();
                }
                Toast.makeText(LoginActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x0002) { // 登陆成功
                loadingDialog.loadSuccess();
                String accountStr = account.getText().toString().trim();
                SharedPreferencesUtil.setAccount(LoginActivity.this, accountStr);
                SharedPreferencesUtil.setUID(LoginActivity.this, msg.arg1);

                // 将登陆记录保存到shara.xml中
                try {
                    String record = SharedPreferencesUtil.getLandingRecord(LoginActivity.this);
                    JSONArray jsonTempArr = new JSONArray(record);
                    JSONArray jsonArr = new JSONArray();
                    jsonArr.put(accountStr);
                    for (int i = 0; i < jsonTempArr.length(); i ++) {
                        if (!accountStr.equals(jsonTempArr.getString(i))) {
                            jsonArr.put(jsonTempArr.getString(i));
                        }
                    }
                    SharedPreferencesUtil.setLandingRecord(LoginActivity.this, jsonArr.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 清除本地相关缓存数据
                try {
                    MyPublishSocietyCircleDao myPublishSocietyCircleDao = new MyPublishSocietyCircleDao(LoginActivity.this);
                    if(myPublishSocietyCircleDao.deleteForAllData() == 1) { // 清空本地数据库数据
                        Log.e("tag", "清空本地数据库我发布的社团公告表数据");
                    }
                    UserDao userDao = new UserDao(LoginActivity.this);
                    if(userDao.deleteForAllData() == 1) { // 清空本地数据库数据
                        Log.e("tag", "清空本地数据库用户表数据");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                toHome();
            } else if (msg.what == 0x0003) { // 登陆失败
                loadingDialog.setFailedText("用户名或密码错误");
                loadingDialog.loadFailed();
            } else if (msg.what == 0x0004) { // 未知用户
                loadingDialog.setFailedText("此用户未注册");
                loadingDialog.loadFailed();
            } else if (msg.what == 0x0005) { // 页面跳转
                loadingDialog.close();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 设置账号输入框右侧下拉箭头大小
        Drawable rightDrawable = account.getCompoundDrawables()[2];
        if(rightDrawable != null){
            rightDrawable.setBounds(0, 0, 46, 23);
            account.setCompoundDrawables(account.getCompoundDrawables()[0], account.getCompoundDrawables()[1],
                    rightDrawable, account.getCompoundDrawables()[3]);
        }

        // 历史登陆账号
        try {
            String record = SharedPreferencesUtil.getLandingRecord(LoginActivity.this);
            JSONArray jsonArr = new JSONArray(record);
            for (int i = 0; i < jsonArr.length(); i ++) {
                historyAccounts.add(jsonArr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 列表弹出框
        historyAccountListPopupAdapter = new HistoryAccountListPopupAdapter(
                LoginActivity.this, historyAccounts);
        lpw = new ListPopupWindow(this);
        lpw.setAdapter(historyAccountListPopupAdapter);
        lpw.setAnchorView(account);
        lpw.setModal(true);
        lpw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_login_white_translucent));
        historyAccountListPopupAdapter.setOnItemListPopupClickListener(new OnItemListPopupClickListener() {
            @Override
            public void onItemClick(int position, int itemId) {
                if (itemId == R.id.item_history_account) {
                    account.setText(historyAccounts.get(position));
                    // 将光标移动到末尾
                    account.setSelection(account.getText().length());
                } else if (itemId == R.id.item_history_account_remove) {
                    try {
                        String accountStr = historyAccounts.get(position);
                        String record = SharedPreferencesUtil.getLandingRecord(LoginActivity.this);
                        JSONArray jsonTempArr = new JSONArray(record);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < jsonTempArr.length(); i ++) {
                            if (!accountStr.equals(jsonTempArr.getString(i))) {
                                jsonArray.put(jsonTempArr.getString(1));
                            }
                        }
                        SharedPreferencesUtil.setLandingRecord(LoginActivity.this, jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    historyAccounts.remove(position);
                    historyAccountListPopupAdapter.notifyDataSetChanged();
                }
                lpw.dismiss();
            }
        });

        // 设置账号输入框右侧箭头的触摸事件
        account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                // 点击输入框右侧图标
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getX() >= (account.getWidth() - account.getCompoundDrawables()[DRAWABLE_RIGHT]
                            .getBounds().width())) {
                        lpw.show(); // 显示下拉列表
                        return true;
                    }
                }
                return false;
            }
        });

        // android 6.0 存储权限申请
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage));
        HiPermission.create(LoginActivity.this)
                .permissions(permissionItems)
                .msg("必要权限，拒绝此权限会导致app异常")
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))//图标的颜色
                .style(R.style.PermissionStyle)
                .checkMutiPermission(new PermissionCallback(){
                    @Override
                    public void onClose() {
                        // 此权限为必要的权限，拒绝会影响程序运行
                    }
                    @Override
                    public void onFinish() {
                        Log.e("tag", "所有权限申请完成");
                    }
                    @Override
                    public void onDeny(String permission, int position) {}
                    @Override
                    public void onGuarantee(String permission, int position) {}
                });

    }

    /**
     * 跳转到主页
     */
    private void toHome() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    handler.sendEmptyMessage(0x0005);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 登陆按钮点击事件
     * @param view
     */
    @OnClick(R.id.login_btn)
    public void onClickLoginBtn(View view) {

        if ("".equals(account.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(password.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {

            // 加载对话框
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setLoadingText("正在登录")
                    .setSuccessText("登录成功")//显示加载成功时的文字
                    .setFailedText("登录失败")
                    .closeSuccessAnim()
                    .closeFailedAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

            // 联网线程
            new Thread() {
                @Override
                public void run() {
                    String result = LoginRequest.login(account.getText().toString().trim(),
                            password.getText().toString().trim());
                        if (null != result) {
                            try {
                                JSONObject json = new JSONObject(result);
                                result = json.getString("result");

                                if (result.equals("success")) {
                                    Message msg = new Message();
                                    msg.what = 0x0002;
                                    msg.arg1 = json.getInt("id");
                                    handler.sendMessage(msg);
                                } else if (result.equals("error")) {
                                    handler.sendEmptyMessage(0x0003);
                                } else if (result.equals("unknown")) {
                                    handler.sendEmptyMessage(0x0004);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(0x0001);
                        }
                }
            }.start();

        }
    }

    /**
     * 注册文字点击事件
     * @param view
     */
    @OnClick(R.id.login_register_text)
    public void onClickRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 忘记密码文字点击事件
     * @param view
     */
    @OnClick(R.id.login_forget_pwd_text)
    public void onClickForgetPwd(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }
}
