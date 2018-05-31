package coc.team.home.provider;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 组件路由 - 主页组件接口声明
 */
public interface HomeProvider extends IProvider {
    void toHome(Activity activity);
}
