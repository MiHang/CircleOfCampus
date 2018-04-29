package coc.team.manager.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.manager.provider.ManagerProvider;

/**
 * 组件路由 - 管理组件接口实现
 */
@Route(path = "/service/manager")
public class ManagerService implements ManagerProvider {
    @Override
    public void init(Context context) {}
}
