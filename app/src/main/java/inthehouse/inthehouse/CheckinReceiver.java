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

public class CheckinReceiver extends BroadcastReceiver {

    private static final String SERVER_URL = "http://ec2-54-191-243-15.us-west-2.compute.amazonaws.com";
    private static final String SERVER_PORT = "5000";
    private static final int SUCCESS = 200;

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

                if (!user.isIncognito() && wifiInfo.getMacAddress().equals(user.getHomeMac())) {
                    Log.d("CheckinReceiver", "checking in");
                    new CheckinTask(Person.getCurrentUser()).execute();
                }
            }
            // TODO: Maybe explicitly set user as not home if we change the user data we're storing
            //       on the server
        }
    }

    private void serverCheckin(Person user) {
        HttpGet request = new HttpGet(SERVER_URL + ":" + SERVER_PORT + "/checkin/" + user.getAuthToken());

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != SUCCESS) {
                Log.d("CheckinReceiver", "Login failed: " + response.getStatusLine().getStatusCode());
                // Do we want to have the user re-login if this happens?
            }
            response.close();
            httpClient.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CheckinTask extends AsyncTask<Void, Void, Void> {
        Person user;

        CheckinTask(Person user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            user.checkin();
            serverCheckin(user);
            return null;
        }
    }
}
