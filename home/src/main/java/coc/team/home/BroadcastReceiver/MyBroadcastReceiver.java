package coc.team.home.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.common.model.DataMsg;
import com.common.utils.ByteUtils;

import coc.team.home.Interface.MessageListener;
import coc.team.home.model.UserMsg;

/**
 * Created by 惠普 on 2018-05-15.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    MessageListener listener;


    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        byte[] msg=bundle.getByteArray("Msg");
        ByteUtils utils=new ByteUtils();
        DataMsg dataMsg =utils.toT(msg);
        if (listener!=null){
            UserMsg userMsg=new UserMsg();
            userMsg.setAccount(dataMsg.getSend());
            if(dataMsg.getSendMsg().getTextMsg()!=null){
                userMsg.setMsg(dataMsg.getSendMsg().getTextMsg());
            }
            if(dataMsg.getSendMsg().getAudioMsg()!=null){
                userMsg.setMsg("语音消息");
            }
            if(dataMsg.getSendMsg().getImgMsg()!=null){
                userMsg.setMsg("图片消息");
            }
            userMsg.setDate(dataMsg.getSendMsg().getDate());

            listener.sendMessageListener(userMsg);
        }

        Log.e("tag","广播:接收到消息"+ dataMsg.getSend()+":"+dataMsg.getSendMsg().getTextMsg());



    }
}
