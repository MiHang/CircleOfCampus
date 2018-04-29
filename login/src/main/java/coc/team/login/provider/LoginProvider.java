package coc.team.login.provider;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 组件路由 - 登陆组件接口声明
 */
public interface LoginProvider extends IProvider {

    void toLogin(Activity activity);

    void toRegister(Activity activity);

    void toChangerPassword(Activity activity);

}
