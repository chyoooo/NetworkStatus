package lyu.network.connect.core;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import androidx.annotation.NonNull;
import lyu.network.connect.NetStatus;
import lyu.network.connect.NetworkManager;

public class NetWorkCallbackImpl extends ConnectivityManager.NetworkCallback {


    private final static String TAG = "NetWorkCallback";
    private final static String TAG_POST = "NetWorkCallback_POST";
    private final static String TAG_BOOL = "NetWorkCallback_BOOL";

    private boolean isAvailable = true;
    // 有经过
    private boolean hasLosing = false;

    private boolean hasValidated = false;

    private NetStatus netStatus;
    @Override
    public void onAvailable(@NonNull Network network) {
        isAvailable = true;
//        Log.e(TAG_BOOL,"onAvailable isAvailable: " + isAvailable + " hasLosing:" + hasLosing + " hasValidated:" + hasValidated);
    }

    @Override
    public void onLost(@NonNull Network network) {
//        Log.e(TAG_BOOL,"onLost isAvailable: " + isAvailable + " hasLosing:" + hasLosing + " hasValidated:" + hasValidated);
        if (hasLosing && hasValidated) {
            hasLosing = false;
            return;
        }
        hasLosing = false;

//        Log.d(TAG_POST,"onLost");
        post(NetStatus.NONE);
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
//        Log.e(TAG_BOOL,"onLosing isAvailable: " + isAvailable + " hasLosing:" + hasLosing + " hasValidated:" + hasValidated);
        hasLosing = true;
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {

//        Log.e(TAG_BOOL,"onCapabilitiesChanged isAvailable: " + isAvailable + " hasLosing:" + hasLosing + " hasValidated:" + hasValidated);
        if (hasValidated && !isAvailable) {
            return;
        }
        isAvailable = false;
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            hasValidated = true;
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.d(TAG_POST, "onCapabilitiesChanged: 网络类型为wifi");
                post(NetStatus.WIFI);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.d(TAG_POST, "onCapabilitiesChanged: 蜂窝网络");
                post(NetStatus.MOBILE);
            } else {
//                Log.d(TAG_POST, "onCapabilitiesChanged: 其他网络");
                post(NetStatus.OTHER);
            }
        } else {
            hasValidated = false;
            // 初始化 触发事件
            if (netStatus == null) {
//                Log.d(TAG_POST,"onLost");
                post(NetStatus.NONE);
            }
        }
    }


    private void post(NetStatus netStatus) {
        this.netStatus = netStatus;
        NetworkManager.getInstance().subscribe(netStatus);
    }
}
