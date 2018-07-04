package team.circleofcampus.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.common.model.Message;
import com.common.model.UserMsg;
import com.common.utils.AudioUtils;
import com.common.utils.ByteUtils;
import com.common.utils.Symbol;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.List;
import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.R;
import team.circleofcampus.dao.Data_Dao;
import team.circleofcampus.dao.UserMsg_Dao;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.util.Uuidutil;


/**
 * Created by 惠普 on 2018-05-15.
 */

public class MyService extends Service {
    String TAG="service";
    MsgBinder binder=new MsgBinder();
    WebSocketClient myClient;
    String send;
    Data_Dao dao;
    UserMsg_Dao userMsg_dao;
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
        try {
            dao = new Data_Dao(this);
            userMsg_dao = new UserMsg_Dao(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        String name=preferences.getString("send", null);
        if (name!=null){
            getCon(name);
        }
    }

    public void getCon(final String send){
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

                    ByteUtils utils = new ByteUtils();
                    Message msg = utils.toT(bytes.array());

                    msg.setMsg_Receive(Symbol.Msg_Receive);//消息接收
                    Log.d(TAG,"来下哦消息"+ msg.getSend()+":"+msg.getText()+msg.getDate());

                    //储存语音信息
                    if (msg.getAudio()!=null&&msg.getAudioPath()!=null){
                        Log.e("tag","语音消息储存");
                        Uuidutil.FileToByte(msg.getAudio(),getApplicationContext(),msg.getAudioPath());
    //                  AudioUtils audioUtils=new AudioUtils(getApplicationContext());
    //                  File file = new File(getFilesDir(), msg.getAudioPath());
    //                  audioUtils.PlayAudio(Integer.parseInt(msg.getDuration()),file);
                    }

                    try {
                        dao.save(msg);
                        Log.e("tag", "sqlite save method execute。。。。");
                    } catch (SQLException e) {
                        Log.e("tag", "SQLException method execute。。。。");
                        e.printStackTrace();
                    }




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
                    List<UserMsg> m=userMsg_dao.queryMsgBySearch(userMsg.getAccount());//判断是否已有记录,有则更新
                    if (m!=null&&m.size()>0){
                        UserMsg user=m.get(0);
                        user.setMsg(userMsg.getMsg());
                        user.setAmount(user.getAmount()+1);
                        user.setDate(msg.getDate());
                        userMsg_dao.update(user);
                        if(msg.getText()!=null){
                            user.setMsg(msg.getText());
                        }
                        if(msg.getAudio()!=null){
                            user.setMsg("语音消息");
                        }
                        if(msg.getImg()!=null){
                            user.setMsg("图片消息");
                        }
                        Log.e("tag","----更新表___");
                    }else{
                        userMsg_dao.add(userMsg);
                    }
                    if (listener!=null){
                        listener.update(userMsg.getAccount(),true);
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
