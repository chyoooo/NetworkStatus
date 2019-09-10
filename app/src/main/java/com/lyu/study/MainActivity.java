package com.lyu.study;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lyu.network.connect.NetStatus;
import lyu.network.connect.NetworkManager;
import lyu.network.connect.NetworkUtils;
import lyu.network.connect.annotation.NetSubscribe;
import lyu.network.connect.annotation.NetworkSubscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager.getInstance().register(this);
    }

    @NetworkSubscribe(NetSubscribe.MOBILE)
    void test1() {
        Log.e("NetworkType----->", "test1" + NetworkUtils.getNetworkStatus().toString());
    }

    @NetworkSubscribe(NetSubscribe.ALL)
    void test2(NetStatus netStatus22) {
        Log.e("NetworkType----->", "test2" + netStatus22.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().unregister(this);
    }
}
