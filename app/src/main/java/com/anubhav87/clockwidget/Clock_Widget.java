package com.anubhav87.clockwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class Clock_Widget extends AppWidgetProvider {

    public static final String CLOCK_WIDGET_UPDATE = "com.anubhav87.clockwidget.Clock_Widget.CLOCK_WIDGET_UPDATE";

    BroadcastReceiver tickReceiver ;

    private PendingIntent createUpdateIntent(Context context){
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void buildUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.clock__widget);
        // clock data
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        remoteViews.setTextViewText(R.id.hours, String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
        remoteViews.setTextViewText(R.id.min, String.valueOf(c.get(Calendar.MINUTE)));
        ComponentName widget = new ComponentName(context,Clock_Widget.class);
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    private void startTicking(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MINUTE, 1);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), createUpdateIntent(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        Log.e("onEnabled","hello");
     //   context.startService(new Intent(context,UpdateService.class));
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
       /* AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.e("onEnabled","hello");
        //update clock widget in 1 sec
        alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),
                60000,createUpdateIntent(context));*/
        buildUpdate(context);
       context.startService(new Intent(context,UpdateService.class));
        //Register the broadcast receiver to receive TIME_TICK
        //context.registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));


        //context.startService(new Intent(context,UpdateService.class));
        Log.e("onEnabled","hello");
    }

    public void onReceive(Context context, Intent intent) {
        try {
            // This method receives broadcast event from clock service which
            // had sendBroadcast in the updatewidget method.
            Log.d("onReceiveWidget", "onReceive " + intent.getAction());
            super.onReceive(context, intent);
                 // startTicking(context);
                buildUpdate(context);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
       // AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
       // alarmManager.cancel(createUpdateIntent(context));
      //  context.stopService(new Intent(context,UpdateService.class));
    }
}



