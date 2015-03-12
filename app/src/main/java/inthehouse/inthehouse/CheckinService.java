package inthehouse.inthehouse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CheckinService extends Service {

    public static String ACTION_CHECKIN = "inthehouse.CHECKIN";

    @Override
    public void onCreate() {
        super.onCreate();

        Context ctx = getApplicationContext();
        AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_CHECKIN),
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                60000, 60000, pending);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Causes the service to restart automatically if Android kills it
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CheckinBinder();
    }

    private class CheckinBinder extends Binder {
        public CheckinService getService() {
            return CheckinService.this;
        }
    }
}
