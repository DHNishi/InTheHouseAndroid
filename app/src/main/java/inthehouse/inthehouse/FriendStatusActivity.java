package inthehouse.inthehouse;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class FriendStatusActivity extends ActionBarActivity {

    private StatusReceiver mWifiMonitor;

    private Person mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_status);
        mCurrentUser = Person.getCurrentUser();

        if (!isServiceRunning(CheckinService.class)) {
            Intent service = new Intent(this, CheckinService.class);
            startService(service);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Temporarily displaying this here for testing purposes until we add friends next sprint
        ((TextView) findViewById(R.id.tempCheckinDisplay)).setText("Last checkin: " +
                mCurrentUser.getLastCheckin().toString());
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
                mCurrentUser.toggleIsIncognito();
                Toast.makeText(this, "Incognito mode is now " + (mCurrentUser.isIncognito() ?
                        "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
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
}
