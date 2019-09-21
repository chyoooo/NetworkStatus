# Network_Connect
像用EventBus一样使用网络状态切换监听，在某个需要在某种网络状态下执行的方法上添加`@NetworkSubscribe(NetSubscribe.ALL)`这个注解，当网络状态切换到这个状态时这个方法会自动执行，括号中为网络状态。
做了版本兼容性处理，6.0以上使用的是 `NetWorkCallback`，6.0以下使用的是广播

运用了注解处理器，JavaPoet，反射等技术。

## 用法

```Java
NetworkManager.getInstance().init(this);
```

```Java
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
```

