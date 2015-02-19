package inthehouse.inthehouse;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import inthehouse.inthehouse.Persistence.PreferenceStorage;


public class FriendStatusActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Person mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_status);
        //Log.d("User Name", mCurrentUser.getName());

        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (netInfo != null && netInfo.isConnected()) {
            WifiInfo wifiInfo = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            String homeMac = PreferenceStorage.getWifiMac(this);

            // if home mac address preference is not set
            if (homeMac == null) {
                // ask user if he is home
                showHomePopup(wifiInfo.getSSID(), wifiInfo.getMacAddress(), this);
            }
        }

        if (!isServiceRunning(CheckinService.class)) {
            Intent service = new Intent(this, CheckinService.class);
            startService(service);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mCurrentUser.getLastCheckin() != null) {
            // Temporarily displaying this here for testing purposes until we add friends next sprint
            ((TextView) findViewById(R.id.tempCheckinDisplay)).setText("Last checkin: " +
                    mCurrentUser.getLastCheckin().toString());
        }*/
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_incognito:
                //mCurrentUser.toggleIsIncognito();
                //Toast.makeText(this, "Incognito mode is now " + (mCurrentUser.isIncognito() ?
                //        "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // From http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showHomePopup(String networkName, final String macAddress, final Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you home?");
        builder.setMessage("Is " + networkName + " your home network?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setHomeMacPref(macAddress, c);

                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setHomeMacPref(String macAddress, Context c) {
        PreferenceStorage.setWifiMac(c, macAddress);
    }
}
