package lyu.network.connect;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NetWorkCallbackImpl extends ConnectivityManager.NetworkCallback {


    private final static String TAG = "NetWorkCallbackImpl";

    private static final NetWorkCallbackImpl globalInstance = new NetWorkCallbackImpl();

    private Map<Object, List<NetworkConnectObserver>> mNetworkListenerCache;
    private List<NetworkConnectObserver> mNoneObservers;
    private List<NetworkConnectObserver> mAllObservers;
    private List<NetworkConnectObserver> mWifiObservers;
    private List<NetworkConnectObserver> mMobileObservers;
    private List<NetworkConnectObserver> mOtherObservers;

    private NetStatus netStatus = NetStatus.NONE;

    private NetWorkCallbackImpl() {
        mNetworkListenerCache = new HashMap<>();
        mNoneObservers = new ArrayList<>();
        mAllObservers = new ArrayList<>();
        mWifiObservers = new ArrayList<>();
        mMobileObservers = new ArrayList<>();
        mOtherObservers = new ArrayList<>();
    }

    public static NetWorkCallbackImpl getInstance() {
        return globalInstance;
    }

    public void addObservers(Object object, List<NetworkConnectObserver> observers) {
        mNetworkListenerCache.put(object, observers);
        for (NetworkConnectObserver observer : observers) {
            addToObservers(observer);
        }
    }


    private void addToObservers(NetworkConnectObserver observer) {
        switch (observer.getNetworkType()) {
            case NONE:
                mNoneObservers.add(observer);
                break;
            case ALL:
                mAllObservers.add(observer);
                break;
            case MOBILE:
                mMobileObservers.add(observer);
                break;
            case WIFI:
                mWifiObservers.add(observer);
                break;
            case OTHER:
                mOtherObservers.add(observer);
                break;
        }
    }

    public void removeObservers(Object object) {
        if (mNetworkListenerCache.containsKey(object)) {
            List<NetworkConnectObserver> observers = mNetworkListenerCache.get(object);
            for (NetworkConnectObserver observer : observers) {
                removeFromObservers(observer);
            }
            mNetworkListenerCache.remove(object);
        }

    }

    private void removeFromObservers(NetworkConnectObserver observer) {
        switch (observer.getNetworkType()) {
            case NONE:
                mNoneObservers.remove(observer);
                break;
            case ALL:
                mAllObservers.remove(observer);
                break;
            case MOBILE:
                mMobileObservers.remove(observer);
                break;
            case WIFI:
                mWifiObservers.remove(observer);
                break;
            case OTHER:
                mOtherObservers.remove(observer);
                break;
        }
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.e(TAG,"网络连接了");
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        Log.e(TAG,"网络断开了 onUnavailable");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.e(TAG,"网络断开了 onLost");
        post(NetStatus.NONE);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
//        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d(TAG, "onCapabilitiesChanged: 网络类型为wifi");
                post(NetStatus.WIFI);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d(TAG, "onCapabilitiesChanged: 蜂窝网络");
                post(NetStatus.MOBILE);
            } else {
                Log.d(TAG, "onCapabilitiesChanged: 其他网络");
                post(NetStatus.OTHER);
            }
//        }
    }


    private void post(NetStatus netStatus) {
        this.netStatus = netStatus;
        subscribe(mAllObservers, netStatus);
        switch (netStatus) {
            case OTHER:
                subscribe(mOtherObservers, NetStatus.OTHER);
                break;
            case NONE:
                subscribe(mNoneObservers, NetStatus.NONE);
                break;
            case WIFI:
                subscribe(mWifiObservers, NetStatus.NONE);
                break;
            case MOBILE:
                subscribe(mMobileObservers, NetStatus.MOBILE);
                break;
        }
    }

    private void subscribe(List<NetworkConnectObserver> observers, NetStatus netStatus) {
        for (NetworkConnectObserver observer : observers) {
            observer.connect(netStatus);
        }
    }

    public NetStatus getNetStatus() {
        return netStatus;
    }
}
