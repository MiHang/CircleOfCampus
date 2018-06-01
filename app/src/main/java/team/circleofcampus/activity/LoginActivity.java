package team.circleofcampus.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.Interface.OnItemListPopupClickListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.HistoryAccountListPopupAdapter;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_account_edit_text)
    protected EditText account;
    @BindView(R.id.login_pwd_edit_text)
    protected EditText password;
    @BindView(R.id.login_btn)
    protected Button login;

    private ListPopupWindow lpw; // 列表弹出框
    private HistoryAccountListPopupAdapter historyAccountListPopupAdapter;
    // 弹出框数据 - 历史登陆账号
    private ArrayList<String> historyAccounts = new ArrayList<String>();

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
        historyAccounts.add("jayevip@163.com");
        historyAccounts.add("jaye");

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
    }

    /**
     * 登陆按钮点击事件
     * @param view
     */
    @OnClick(R.id.login_btn)
    public void onClickLoginBtn(View view) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        finish();
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
