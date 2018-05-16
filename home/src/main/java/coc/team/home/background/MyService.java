package coc.team.home.background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.common.model.DataMsg;
import com.common.utils.ByteUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;


/**
 * Created by 惠普 on 2018-05-15.
 */

public class MyService extends Service {
    String TAG="service";
    Binder binder=new Binder();
    WebSocketClient myClient;
    String send;
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
            URI uri = new URI("ws://192.168.157.2:8891");
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
                    Intent intent=new Intent();
                    intent.putExtra("Msg", bytes.array());
                    intent.setAction("coc.team.home.activity");
                    sendBroadcast(intent);
                    ByteUtils utils=new ByteUtils();
                    DataMsg msg =utils.toT(bytes.array());

                    Log.d(TAG,"消息"+ msg.getSend()+":"+msg.getSendMsg().getTextMsg());

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


}
