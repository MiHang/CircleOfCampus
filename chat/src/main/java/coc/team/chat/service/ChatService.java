package coc.team.chat.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;

import coc.team.chat.provider.ChatProvider;

/**
 * 组件路由 - 聊天组件接口实现
 */
@Route(path = "/service/chat")
public class ChatService implements ChatProvider {
    @Override
    public void init(Context context) {}
}
