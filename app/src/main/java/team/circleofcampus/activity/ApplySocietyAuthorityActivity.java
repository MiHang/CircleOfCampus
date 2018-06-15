package team.circleofcampus.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MySpinnerAdapter;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.http.SocietyAuthorityRequest;
import team.circleofcampus.model.SpinnerItem;
import team.circleofcampus.util.SharedPreferencesUtil;

public class ApplySocietyAuthorityActivity extends AppCompatActivity {

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.apply_authority_society_spinner)
    protected Spinner allSocietySpinner;
    @BindView(R.id.apply_authority_reason_edit)
    protected EditText applyReasonEdit;

    private MySpinnerAdapter allSocietySpinnerAdapter;
    // spinner item data
    private ArrayList<SpinnerItem> societyItems = new ArrayList<SpinnerItem>();

    private LoadingDialog loadingDialog;

    /**
     * 用户ID
     */
    private int userId = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : { // 网络连接出错
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    Toast.makeText(ApplySocietyAuthorityActivity.this,
                            "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                } break;
                case 0x0002 : { // 加载所有社团成功
                    if (allSocietySpinnerAdapter != null) {
                        allSocietySpinnerAdapter.notifyDataSetChanged();
                    }
                } break;
                case 0x0003 : { // 用户所在学校暂无社团信息
                    Toast.makeText(ApplySocietyAuthorityActivity.this,
                            "您所在的学校暂无社团信息", Toast.LENGTH_SHORT).show();
                } break;
                case 0x0004 : { // 提交失败
                    if (loadingDialog != null) {
                        loadingDialog.setFailedText("提交失败，请稍后再试");
                        loadingDialog.loadFailed();
                    }
                } break;
                case 0x0005 : { // 提交成功
                    if (loadingDialog != null) {
                        loadingDialog.loadSuccess();
                    }
                    backPrev(); // 返回上一个页面
                } break;
                case 0x0006 : { // 返回上一个页面
                   onBackPressed();
                } break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loadingDialog != null) {
            loadingDialog.close();
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_society_authority);
        ButterKnife.bind(this);
        headerTitle.setText("社团权限申请");

        // 获取用户ID
        userId = SharedPreferencesUtil.getUID(ApplySocietyAuthorityActivity.this);

        // 选择社团部分
        getAllSociety(); // 获取所有社团信息
        allSocietySpinnerAdapter = new MySpinnerAdapter(ApplySocietyAuthorityActivity.this, societyItems);
        allSocietySpinner.setAdapter(allSocietySpinnerAdapter);
    }

    public void backPrev() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    handler.sendEmptyMessage(0x0006);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取所有社团信息
     */
    private void getAllSociety() {
        // 联网线程
        new Thread() {
            @Override
            public void run() {
                String result = SocietyAuthorityRequest.getAllSociety(userId);
                if (result != null) {
                    try {
                        JSONArray jsonArr = new JSONArray(result);
                        for (int i = 0; i < jsonArr.length(); i ++) {
                            JSONObject json = new JSONObject(jsonArr.get(i).toString());
                            SpinnerItem spinnerItem = new SpinnerItem(json.getInt("societyId"),
                                    json.getString("societyName"));
                            societyItems.add(spinnerItem);
                        }

                        if (jsonArr.length() > 0) { // 成功
                            handler.sendEmptyMessage(0x0002);
                        } else { // 无社团信息
                            handler.sendEmptyMessage(0x0003);
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

    /**
     * 提交权限申请请求
     */
    private void submitRequest() {
        // 联网线程
        new Thread() {
            @Override
            public void run() {
                String result = SocietyAuthorityRequest.submitSocietyAuthorityRequest(userId,
                        (int)allSocietySpinner.getSelectedItemId(), applyReasonEdit.getText().toString());
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");
                        if ("error".equals(result)) {
                            handler.sendEmptyMessage(0x0004);
                        } else {
                            handler.sendEmptyMessage(0x0005);
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

    /**
     * 点击提交申请按钮事件
     */
    @OnClick(R.id.apply_authority_submit_btn)
    public void onClickSubmitBtn() {

        if (societyItems.size() == 0) { // 所在的学校暂无社团信息
            Toast.makeText(ApplySocietyAuthorityActivity.this,
                    "您所在的学校暂无社团信息", Toast.LENGTH_SHORT).show();
        } else if ("".equals(applyReasonEdit.getText().toString().trim())) {
            Toast.makeText(ApplySocietyAuthorityActivity.this,
                    "请输入申请理由", Toast.LENGTH_SHORT).show();
        } else if (applyReasonEdit.getText().toString().length() > 40) {
            Toast.makeText(ApplySocietyAuthorityActivity.this,
                    "申请理由最多输入40个字符，您输入了" +
                            applyReasonEdit.getText().toString().length() + "个字符", Toast.LENGTH_SHORT).show();
        } else {
            // 加载对话框
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setLoadingText("正在提交")
                    .setSuccessText("提交成功")
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();
            submitRequest(); // 提交权限申请请求
        }
    }

    /**
     * 标题栏返回按钮图标点击事件
     * @param view
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon(View view) {
        onBackPressed();
    }

}
