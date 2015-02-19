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

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class CheckinReceiver extends BroadcastReceiver {

    private static final String SERVER_URL = "http://ec2-54-191-243-15.us-west-2.compute.amazonaws.com";
    private static final String SERVER_PORT = "5000";
    private static final int SUCCESS = 200;

    @Override
    public void onReceive(Context context, Intent intent) {
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
                    new CheckinTask(context).execute();
                }
                else {
                    Log.d("CheckinReceiver", "failed checks.  Not checking in.");
                }
            }
            // TODO: Maybe explicitly set user as not home if we change the user data we're storing
            //       on the server
        }
    }

    private void serverCheckin(Context c) {
        HttpGet request = new HttpGet(SERVER_URL + ":" + SERVER_PORT + "/checkin/" + PreferenceStorage.getAuthToken(c));

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != SUCCESS) {
                Log.d("CheckinReceiver", "Login failed: " + response.getStatusLine().getStatusCode());
                // Do we want to have the user re-login if this happens?
            }
            response.close();
            httpClient.close();
            PreferenceStorage.setLastCheckinTime(c);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CheckinTask extends AsyncTask<Void, Void, Void> {
        Context c;

        CheckinTask(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverCheckin(c);
            return null;
        }
    }
}
