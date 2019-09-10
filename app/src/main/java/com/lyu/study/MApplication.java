package com.lyu.study;

import android.app.Application;

import lyu.network.connect.NetworkManager;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.getInstance().init(this);
    }
}
