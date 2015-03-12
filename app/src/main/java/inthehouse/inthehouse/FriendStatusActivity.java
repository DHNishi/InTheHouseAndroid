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
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class FriendStatusActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<Person> mFriends;

    private PersonListAdapter mFriendsAdapter;

    private ListView mFriendStatusVw;
    private TextView mNoFriendsVw;
    private Button mRefreshVw;


    private static final String TAG = "Friend Status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_status);
        mFriendStatusVw = (ListView) findViewById(R.id.friendStatusViewGroup);
        mNoFriendsVw = (TextView) findViewById(R.id.noFriendsText);
        mRefreshVw = (Button) findViewById(R.id.refreshStatusBtn);

        mRefreshVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshVw.setEnabled(false);
                mFriends = new ArrayList<Person>();
                loadFriendStatuses();
            }
        });
        mNoFriendsVw.setVisibility(View.GONE);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (netInfo != null && netInfo.isConnected()) {
            WifiInfo wifiInfo = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            String homeWifi = PreferenceStorage.getWifiSSID(this);

            // if home mac address preference is not set
            if (homeWifi == null) {
                // ask user if he is home
                showHomePopup(wifiInfo.getSSID(), this);
            }
        }

        if (!isServiceRunning(CheckinService.class)) {
            Intent service = new Intent(this, CheckinService.class);
            startService(service);
        }

        mFriends = new ArrayList<Person>();
        loadFriendStatuses();
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
                PreferenceStorage.toggleIncognito(this);
                Toast.makeText(this, "Incognito mode is now " + (PreferenceStorage.isIncognito(this) ?
                        "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_view_friend_requests:
                startActivity(new Intent(this, FriendRequestsActivity.class));
                return true;
            case R.id.action_add_friend:
                startActivity(new Intent(this, AddFriendActivity.class));
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

    private void loadFriendStatuses() {
        Server.getFriendStatuses(this, new Server.ResponseCallback() {
            @Override
            public void execute(InputStream response, int status) {
                try {
                    ArrayList<Map<String, String>> responseData = new ObjectMapper()
                            .readValue(response, ArrayList.class);

                    if (responseData != null) {
                        for (Map<String, String> friend : responseData) {
                            Log.d("FriendStatus", "url: " + friend.get("picture"));
                            mFriends.add(new Person(
                                    friend.get("name"),
                                    friend.get("id"),
                                    friend.get("picture"),
                                    new Timestamp(System.currentTimeMillis()
                                            - Integer.parseInt(friend.get("checkin"))
                                            * TimeUnit.SECONDS.toMillis(1)),
                                    null
                            ));
                        }

                        mFriendsAdapter = new PersonListAdapter(FriendStatusActivity.this, mFriends, PersonView.class);
                        mFriendStatusVw.setAdapter(mFriendsAdapter);
                    }
                    else {
                        mNoFriendsVw.setVisibility(View.VISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mRefreshVw.setEnabled(true);
                }
            }
        }, null);
    }

    private void showHomePopup(final String networkName, final Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you home?");
        builder.setMessage("Is " + networkName + " your home network?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceStorage.setWifiSSID(c, networkName);

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

    public void deleteFriend(final Person friend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm Friend Deletion");
        builder.setMessage("Are you sure you want to delete " + friend.getName() + "?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Server.deleteFriend(FriendStatusActivity.this, friend.getGoogleId(), new Server.ResponseCallback() {
                    @Override
                    public void execute(InputStream response, int status) {
                        mFriends.remove(friend);
                        mFriendsAdapter.notifyDataSetChanged();
                        Toast.makeText(FriendStatusActivity.this, "Your friend was successfully deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, null);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
