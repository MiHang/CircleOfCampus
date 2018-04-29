package team.circleofcampus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import coc.team.login.provider.LoginProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDebug = true;
        if (isDebug) { // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog(); // 打印日志
            ARouter.openDebug(); // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(getApplication()); // 尽可能早，推荐在Application中初始化

        TextView login = (TextView) findViewById(R.id.login_text_view);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginProvider loginService = (LoginProvider) ARouter.getInstance().build("/service/login").navigation();
                if(loginService != null){
                    loginService.toLogin(MainActivity.this);
                }
            }
        });
    }
}
