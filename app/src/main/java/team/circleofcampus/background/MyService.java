package team.circleofcampus.background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.common.model.Msg;
import com.common.utils.ByteUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.util.HttpUtils;


/**
 * Created by 惠普 on 2018-05-15.
 */

public class
MyService extends Service {
    String TAG="service";
    MsgBinder binder=new MsgBinder();
    WebSocketClient myClient;
    String send;
    MessageListener listener;
    List<byte[]> data=new ArrayList<>();

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
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"---onStartCommand---");
        send=intent.getStringExtra("send");
        try {
            URI uri = new URI("ws://"+ HttpUtils.Ip+":8891");
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
                    Intent intent=new Intent();
                    intent.putExtra("Msg", bytes.array());
                    intent.setAction("coc.team.home.activity");
                    sendBroadcast(intent);

                    Log.d(TAG, "接收到"+bytes.toString());

                    ByteUtils utils=new ByteUtils();
                    Msg msg =utils.toT(bytes.array());

                    Log.d(TAG,"消息"+ msg.getSend()+":"+msg.getText());
                    if (listener!=null){

                            listener.sendMessage(bytes.array());


                    }else{
                        data.add(bytes.array());
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
