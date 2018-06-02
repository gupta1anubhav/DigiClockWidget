package com.anubhav87.clockwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

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
        timeFilter.addAction("android.intent.action.DATE_CHANGED");
        return timeFilter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.timeReceiver == null) {
//            Log.e("onStartCommandtimesnull",intent.getAction());
            this.timeReceiver = new TimeReceiver();
            registerReceiver(this.timeReceiver, getTimeFilter());
        }
//        Log.e("onStartCommand",intent.getAction());
        updateWidget();
        return Service.START_STICKY;
    }
    private RemoteViews buildUpdate(Context context){
        // we build view for clock widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.clock__widget);
        // clock data
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        remoteViews.setTextViewText(R.id.hours,String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
        remoteViews.setTextViewText(R.id.min,String.valueOf(c.get(Calendar.MINUTE)));

        //we must think that min < 10 miss 0 to start so we must manage that case.
        int min = c.get(Calendar.MINUTE);
        remoteViews.setTextViewText(R.id.min, (min < 10 ?  "0":"")+String.valueOf(min));

        // now it stays to configure Android Manifest

        return remoteViews;
    }
    private void updateWidget() {
        Log.e("updateWidgetinService","hello");

        // Intent to ClockWeatherWidget to update the clock
        sendBroadcast(new Intent().setAction(Clock_Widget.CLOCK_WIDGET_UPDATE));
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
