package lyu.network.connect;

public class NetworkUtils {

    public static boolean isNetworkConnected() {
        return NetWorkCallbackImpl.getInstance().getNetStatus() != NetStatus.NONE;
    }

    public static boolean isWifiConnected() {
        return NetWorkCallbackImpl.getInstance().getNetStatus() == NetStatus.WIFI;
    }

    public static boolean isMobileConnected() {
        return NetWorkCallbackImpl.getInstance().getNetStatus() == NetStatus.MOBILE;
    }

    public static NetStatus getNetworkStatus() {
        return NetWorkCallbackImpl.getInstance().getNetStatus();
    }
}
