package coc.team.home.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.home.provider.HomeProvider;

/**
 * 组件路由 - 主页组件接口实现
 */
@Route(path = "/service/home")
public class HomeService implements HomeProvider {
    @Override
    public void init(Context context) {}
}
