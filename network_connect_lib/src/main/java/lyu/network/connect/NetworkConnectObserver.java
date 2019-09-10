package lyu.network.connect;

import lyu.network.connect.annotation.NetSubscribe;

public abstract class NetworkConnectObserver {

    private NetSubscribe netSubscribe;

    public NetworkConnectObserver(NetSubscribe netSubscribe) {
        this.netSubscribe = netSubscribe;
    }


    public NetSubscribe getNetworkType() {
        return netSubscribe;
    }

    public abstract void connect(NetStatus netStatus);
}