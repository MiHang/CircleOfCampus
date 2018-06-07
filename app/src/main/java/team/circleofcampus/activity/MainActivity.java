package team.circleofcampus.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import team.circleofcampus.R;
import team.circleofcampus.util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    private boolean isLogin = false; // 是否登陆

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x0001) {
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main);

        int uId = SharedPreferencesUtil.getUID(MainActivity.this);
        long prevLoginTime = SharedPreferencesUtil.getLoginTime(MainActivity.this);

        // id为0或者距上传使用时间大于一周，则重新登陆
        if (uId == 0 || System.currentTimeMillis() - prevLoginTime > 604800000) {
            isLogin = true;
        }

        // 线程
        new Thread() {
            @Override
            public void run() {
                // 模拟数据加载
                try {
                    sleep(2000);
                    handler.sendEmptyMessage(0x0001);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
