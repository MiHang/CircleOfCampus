package coc.team.login.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.login.LoginActivity;
import coc.team.login.provider.LoginProvider;

/**
 * 组件路由 - 登陆组件接口实现
 */
@Route(path = "/service/login")
public class LoginService implements LoginProvider {

    @Override
    public void init(Context context) {}

    /**
     * 跳转到登录页面
     * @param activity
     */
    @Override
    public void toLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        //activity.finish();
    }

    @Override
    public void toRegister(Activity activity) {

    }

    @Override
    public void toChangerPassword(Activity activity) {

    }

}
