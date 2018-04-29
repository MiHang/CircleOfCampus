package coc.team.campus.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.campus.provider.CampusProvider;

/**
 * 组件路由 - 校园组件接口实现
 */
@Route(path = "/service/campus")
public class CampusService implements CampusProvider {
    @Override
    public void init(Context context) {}
}
