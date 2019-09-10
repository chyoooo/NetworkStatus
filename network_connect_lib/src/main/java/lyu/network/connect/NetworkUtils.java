package lyu.network.connect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean isNetworkConnected() {
        return NetworkManager.getInstance().getNetStatus() != NetStatus.NONE;
    }

    public static boolean isWifiConnected() {
        return NetworkManager.getInstance().getNetStatus() == NetStatus.WIFI;
    }

    public static boolean isMobileConnected() {
        return NetworkManager.getInstance().getNetStatus() == NetStatus.MOBILE;
    }

    public static NetStatus getNetworkStatus() {
        return NetworkManager.getInstance().getNetStatus();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return netInfo != null && netInfo.isConnected();
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return netInfo != null && netInfo.isConnected();
    }

}
