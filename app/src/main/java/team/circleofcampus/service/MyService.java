package team.circleofcampus.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.common.dao.Data_Dao;
import com.common.model.Message;
import com.common.model.Msg;
import com.common.model.UserMsg;
import com.common.utils.ByteUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.http.HttpRequest;


/**
 * Created by 惠普 on 2018-05-15.
 */

public class MyService extends Service {
    String TAG="service";
    MsgBinder binder=new MsgBinder();
    WebSocketClient myClient;
    String send;
    Data_Dao dao = new Data_Dao(this, "12.db");
    MessageListener listener;


    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public WebSocketClient getMyClient() {
        return myClient;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"---onCreate---");
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        String name=preferences.getString("send", null);
        if (name!=null){
            getCon(name);
        }
    }
private void getCon(final String send){
    try {
        URI uri = new URI("ws://"+ HttpRequest.IP+":8888");
        myClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                JSONObject js = new JSONObject();
                try {
                    js.put("Account", send);
                    js.put("Request", "Login");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                myClient.send(js.toString());
            }

            /**
             * 接收二进制
             */
            @Override
            public void onMessage(ByteBuffer bytes) {


                Log.d(TAG, "接收到"+bytes.toString());

                ByteUtils utils=new ByteUtils();
                Msg msg =utils.toT(bytes.array());

                Log.d(TAG,"消息"+ msg.getSend()+":"+msg.getText());
                Message message = new Message();
                message.setMsg(msg);
                Log.e("tag","用户"+message.getMsg().getSend()+"性别"+message.getMsg().getSex());
                dao.setData(message);//储存聊天信息

                if (listener!=null){
                    UserMsg userMsg=new UserMsg();
                    userMsg.setAccount(msg.getSend());
                    userMsg.setUserName(msg.getUserName());
                    userMsg.setSex(msg.getSex());
                    if(msg.getText()!=null){
                        userMsg.setMsg(msg.getText());
                    }
                    if(msg.getAudio()!=null){
                        userMsg.setMsg("语音消息");
                    }
                    if(msg.getImg()!=null){
                        userMsg.setMsg("图片消息");
                    }
                    userMsg.setDate(msg.getDate());
                    listener.update(userMsg,true);
                    Log.e("tag","监听");
                }

            }

            @Override
            public void onMessage(final String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };
        myClient.connect();
    } catch (URISyntaxException e) {
        e.printStackTrace();
    }
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"---onStartCommand---");
        send=intent.getStringExtra("send");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"---onDestroy---");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG,"---onUnbind---");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG,"---onRebind---");
        super.onRebind(intent);
    }
    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public MyService getService() {
            return MyService.this;
        }
    }
    }
