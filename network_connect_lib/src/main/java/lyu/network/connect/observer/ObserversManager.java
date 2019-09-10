package lyu.network.connect.observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lyu.network.connect.NetStatus;

public class ObserversManager {


    private Map<Object, List<NetworkConnectObserver>> mNetworkListenerCache;
    private NetworkObservable mNoneObservable;
    private NetworkObservable mAllObservable;
    private NetworkObservable mWifiObservable;
    private NetworkObservable mMobileObservable;
    private NetworkObservable mOtherObservable;

    public ObserversManager() {
        mNetworkListenerCache = new HashMap<>();
        mNoneObservable = new NetworkObservable();
        mAllObservable = new NetworkObservable();
        mWifiObservable = new NetworkObservable();
        mMobileObservable = new NetworkObservable();
        mOtherObservable = new NetworkObservable();
    }


    public void addObservers(Object object, List<NetworkConnectObserver> observers) {
        if (mNetworkListenerCache.containsKey(object)) {
            return;
        }
        mNetworkListenerCache.put(object, observers);
        for (NetworkConnectObserver observer : observers) {
            addToObservers(observer);
        }
    }


    private void addToObservers(NetworkConnectObserver observer) {
        switch (observer.getNetworkType()) {
            case NONE:
                mNoneObservable.addObserver(observer);
                break;
            case ALL:
                mAllObservable.addObserver(observer);
                break;
            case MOBILE:
                mMobileObservable.addObserver(observer);
                break;
            case WIFI:
                mWifiObservable.addObserver(observer);
                break;
            case OTHER:
                mOtherObservable.addObserver(observer);
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
                mNoneObservable.deleteObserver(observer);
                break;
            case ALL:
                mAllObservable.deleteObserver(observer);
                break;
            case MOBILE:
                mMobileObservable.deleteObserver(observer);
                break;
            case WIFI:
                mWifiObservable.deleteObserver(observer);
                break;
            case OTHER:
                mOtherObservable.deleteObserver(observer);
                break;
        }
    }

    public void subscribe(NetStatus netStatus) {
        subscribe(mAllObservable, netStatus);
        switch (netStatus) {
            case OTHER:
                subscribe(mOtherObservable, NetStatus.OTHER);
                break;
            case NONE:
                subscribe(mNoneObservable, NetStatus.NONE);
                break;
            case WIFI:
                subscribe(mWifiObservable, NetStatus.NONE);
                break;
            case MOBILE:
                subscribe(mMobileObservable, NetStatus.MOBILE);
                break;
        }
    }

    private void subscribe(NetworkObservable observers, NetStatus netStatus) {
        observers.notifyObservers(netStatus);
    }

}
