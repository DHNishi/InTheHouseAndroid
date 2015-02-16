package inthehouse.inthehouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class CheckinReceiver extends BroadcastReceiver {

    private static final String SERVER_URL = "ec2-54-191-243-15.us-west-2.compute.amazonaws.com";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Person user = Person.getCurrentUser();

        if (netInfo != null && user != null) {
            if (netInfo.isConnected()) {
                WifiInfo wifiInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo();

                // TODO: Uncomment and combine if statements when home-setting is added
                //if (wifiInfo.getMacAddress().equals(user.getHomeMac())) {
                if (!user.isIncognito()) {
                    Log.d("CheckinReceiver", "checking in");
                    user.checkin();
                    // TODO: send the new time to the server
                }
            }
            // TODO: Maybe explicitly set user as not home if we change the user data we're storing
            //       on the server
        }
    }
}
