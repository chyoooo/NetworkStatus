package lyu.network.connect;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkManager {

    private static final NetworkManager globalInstance = new NetworkManager();

    private NetWorkCallbackImpl mCallback;

    private NetworkManager(){
        networkConnectBindMap = new HashMap<>();
    }

    public static NetworkManager getInstance() {
        return globalInstance;
    }

    public void init(Application application) {
        if (mCallback != null) {
            return;
        }
        mCallback = NetWorkCallbackImpl.getInstance();

        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(mCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(request, mCallback);
            }
        }
    }

    public void addObservers(Object object, List<NetworkConnectObserver> observers) {
        mCallback.addObservers(object, observers);
    }

    public void removeObservers(Object object) {
        mCallback.removeObservers(object);
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
