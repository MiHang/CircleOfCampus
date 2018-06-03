package team.circleofcampus.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.common.model.Msg;
import com.common.utils.ByteUtils;

import team.circleofcampus.Interface.MsgBroadcastReceiverListener;
import team.circleofcampus.model.UserMsg;


/**
 * Created by 惠普 on 2018-05-15.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    MsgBroadcastReceiverListener listener;


    public void setMessageListener(MsgBroadcastReceiverListener listener) {
        this.listener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        byte[] msg=bundle.getByteArray("Msg");
        ByteUtils utils=new ByteUtils();
        Msg dataMsg =utils.toT(msg);
        if (listener!=null){
            UserMsg userMsg=new UserMsg();
            userMsg.setAccount(dataMsg.getSend());
            userMsg.setUserName(dataMsg.getUserName());
            userMsg.setSex(dataMsg.getSex());
            if(dataMsg.getText()!=null){
                userMsg.setMsg(dataMsg.getText());
            }
            if(dataMsg.getAudio()!=null){
                userMsg.setMsg("语音消息");
            }
            if(dataMsg.getImg()!=null){
                userMsg.setMsg("图片消息");
            }
            userMsg.setDate(dataMsg.getDate());

            listener.sendMessageListener(userMsg);
        }

        Log.e("tag","广播:接收到消息"+ dataMsg.getSend()+":"+dataMsg.getText());



    }
}
