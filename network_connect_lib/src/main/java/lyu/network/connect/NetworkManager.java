package lyu.network.connect;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lyu.network.connect.core.NetWorkCallbackImpl;
import lyu.network.connect.core.NetworkBroadcastReceiver;
import lyu.network.connect.observer.NetworkConnectObserver;
import lyu.network.connect.observer.NetworkObservable;
import lyu.network.connect.observer.ObserversManager;

public class NetworkManager {

    private static final NetworkManager globalInstance = new NetworkManager();

    private Application mApplication;

    private ObserversManager mObserversManager;

    private NetStatus netStatus = NetStatus.NONE;

    private NetworkManager(){
        networkConnectBindMap = new HashMap<>();
        mObserversManager = new ObserversManager();
    }

    public static NetworkManager getInstance() {
        return globalInstance;
    }

    public void init(Application application) {
        if (mApplication != null) {
            return;
        }
        this.mApplication = application;
        NetWorkCallbackImpl mCallback = new NetWorkCallbackImpl();
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkRequest request = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(request, mCallback);
            } else {
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                application.registerReceiver(new NetworkBroadcastReceiver(), intentFilter);
            }
        }
    }

    public Application getApplication() {
        return mApplication;
    }

    public void addObservers(Object object, List<NetworkConnectObserver> observers) {
        mObserversManager.addObservers(object, observers);
    }

    public void removeObservers(Object object) {
        mObserversManager.removeObservers(object);
    }

    public void subscribe(NetStatus netStatus) {
        this.netStatus = netStatus;
        mObserversManager.subscribe(netStatus);
    }

    public NetStatus getNetStatus() {
        return netStatus;
    }

    private Map<Object, NetworkConnectBinder> networkConnectBindMap;
    public void register(Object target) {
        if (networkConnectBindMap.containsKey(target)) {
            return;
        }

        Class<?> targetClass = target.getClass();

        String className = target.getClass().getName() + "$NetworkSubscribe";

        try {
            Class<?> clazz = Class.forName(className);
            NetworkConnectBinder bind = (NetworkConnectBinder) clazz.newInstance();
            networkConnectBindMap.put(target, bind);
            bind.bind(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void unregister(Object target) {
        if (networkConnectBindMap.containsKey(target)) {
            NetworkConnectBinder bind = networkConnectBindMap.get(target);
            bind.unBind(target);
            networkConnectBindMap.remove(target);
        }

    }

}
