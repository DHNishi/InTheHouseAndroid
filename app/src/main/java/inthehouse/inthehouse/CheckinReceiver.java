package inthehouse.inthehouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class CheckinReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("CheckinReceiver", "Started running.");
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        if (netInfo == null)
//                || !netInfo.getTypeName().equalsIgnoreCase("WIFI"))
        {
            return;
        }

        if (netInfo != null) {
            Log.d("CheckinReceiver", "has netinfo");
            if (netInfo.isConnected()) {
                WifiInfo wifiInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo();
                Log.d("CheckinReceiver", "netinfo is connected");
                if ( !PreferenceStorage.isIncognito(context)
                        && wifiInfo.getSSID().equals(PreferenceStorage.getWifiSSID(context))
                        ) {
                    Log.d("CheckinReceiver", "checking in");
                    Server.checkin(context, new Server.ResponseCallback() {
                        @Override
                        public void execute(InputStream response, int status) {
                            PreferenceStorage.setLastCheckinTime(context);
                        }
                    }, null);
                }
                else {
                    Log.d("CheckinReceiver", "failed checks.  Not checking in.");
                }
            }
        }
    }
}
