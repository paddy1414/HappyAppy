package happyappy.com.pdesigns.happyappy.ApplicationResources;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Paddy on 29/05/2015.
 */
public class AppStatus {

    public static AppStatus instance = new AppStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo;
    NetworkInfo mobileInfo;
    boolean connected = false;

    public static AppStatus getInstance(Context contexx) {
        context = contexx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("checkConntection exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }

        return connected;
    }
}
