package com.anubhav87.clockwidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

// Update service for clock widget

public class UpdateService extends Service {

    BroadcastReceiver timeReceiver = null;

    public UpdateService() {
    }

    public class TimeReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive",intent.getAction());
            UpdateService.this.updateWidget();
        }
    }
    private IntentFilter getTimeFilter() {
        // These specify type of actions that can be broadcasted by the system to this app.
        IntentFilter timeFilter = new IntentFilter("android.intent.action.TIME_TICK");
        timeFilter.addAction("android.intent.action.SCREEN_OFF");
        timeFilter.addAction("android.intent.action.SCREEN_ON");
        timeFilter.addAction("android.intent.action.TIME_SET");
        timeFilter.addAction("android.intent.action.DATE_CHANGED");
        timeFilter.addAction("android.intent.action.TIME_CHANGED");
        timeFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        timeFilter.addAction("android.intent.action.SCREEN_OFF");
        timeFilter.addAction("android.intent.action.SCREEN_ON");
        timeFilter.addAction("android.intent.action.USER_PRESENT");
        return timeFilter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.timeReceiver == null) {
            this.timeReceiver = new TimeReceiver();
            registerReceiver(this.timeReceiver, getTimeFilter());
        }

        updateWidget();
        return Service.START_STICKY;
    }

    private void updateWidget() {
        // Intent to ClockWeatherWidget to update the clock
        sendBroadcast(new Intent(this, Clock_Widget.TimeReceiver1.class).setAction(Constants.CLOCK_WIDGET_UPDATE));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onDestroy() {
        // Here receiver is unregistered.
        super.onDestroy();
        if (this.timeReceiver != null) {
            unregisterReceiver(this.timeReceiver);
        }
    }
}
