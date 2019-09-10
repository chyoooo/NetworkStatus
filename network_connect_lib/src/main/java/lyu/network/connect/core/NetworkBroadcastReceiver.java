package lyu.network.connect.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import lyu.network.connect.NetStatus;
import lyu.network.connect.NetworkManager;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            NetStatus netStatus = getNetStatus(netInfo);
            NetworkManager.getInstance().subscribe(netStatus);
        }
    }

    private NetStatus getNetStatus(NetworkInfo netInfo) {
        if (netInfo != null && netInfo.isConnected()) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return NetStatus.MOBILE;
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_ETHERNET:
                    return NetStatus.WIFI;
                default:
                    return NetStatus.OTHER;
            }
        } else {
            return NetStatus.NONE;
        }
    }
}
