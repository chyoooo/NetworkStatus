package lyu.network.connect.observer;

import java.util.Observer;

import lyu.network.connect.annotation.NetSubscribe;

public abstract class NetworkConnectObserver implements Observer {

    private NetSubscribe netSubscribe;

    public NetworkConnectObserver(NetSubscribe netSubscribe) {
        this.netSubscribe = netSubscribe;
    }

    public NetSubscribe getNetworkType() {
        return netSubscribe;
    }

}