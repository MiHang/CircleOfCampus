package coc.team.home.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.home.activity.HomeActivity;
import coc.team.home.provider.HomeProvider;

/**
 * 组件路由 - 主页组件接口实现
 */
@Route(path = "/service/home")
public class HomeService implements HomeProvider {
    @Override
    public void init(Context context) {}

    @Override
    public void toHome(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
