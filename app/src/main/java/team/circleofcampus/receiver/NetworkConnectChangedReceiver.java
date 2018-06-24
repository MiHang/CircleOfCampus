package team.circleofcampus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import team.circleofcampus.Interface.NetworkStateChangeListener;

/**
 * 网络连接状态广播
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    // 网络连接状态改变监听
    private NetworkStateChangeListener networkStateChangeListener;
    public void setNetworkStateChangeListener(NetworkStateChangeListener networkStateChangeListener) {
        this.networkStateChangeListener = networkStateChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // 获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                // 如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (networkStateChangeListener != null) {
                            networkStateChangeListener.networkAvailable(info.getType());
                        }
                        Log.e("TAG", getConnectionType(info.getType()) + "连上");
                    }
                } else {
                    if (networkStateChangeListener != null) {
                        networkStateChangeListener.networkUnavailable();
                    }
                    Log.e("TAG", getConnectionType(info.getType()) + "断开");
                }
            }
        }
    }

    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "移动数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

}
