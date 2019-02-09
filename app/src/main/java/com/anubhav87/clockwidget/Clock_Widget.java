package com.anubhav87.clockwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */

public class Clock_Widget extends AppWidgetProvider {

    public static String FORMAT_12_HOURS = "h:mm";
    public static String FORMAT_24_HOURS = "kk:mm";
    protected static final String PREFS_NAME = "preferences";
    private Calendar calendar;
    TimeReceiver1 timeReceiver1 = null;
    public Calendar getCalendar() {
        // This method creates an instance of calender.
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance();
        }
        return this.calendar;
    }
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // This function is called when widget is resized
        int[] appWidgetIds = {appWidgetId};
        updateClockWithDynamicTextSizes(context,appWidgetIds);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        Log.e("onEnabled","hello");
        updateClockWithDynamicTextSizes(context,appWidgetIds);
    }
    private IntentFilter getTimeFilter1() {
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
        timeFilter.addAction(Constants.CLOCK_WIDGET_UPDATE);
        return timeFilter;
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
         ComponentName componentName = new ComponentName(context,Clock_Widget.class);
        int appWidgetIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        if (appWidgetIds.length == 0) {
            Log.e("onEnabled","hello");
            appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, getClass()));
        }
        Log.e("onEnabled","hello");
        updateClockWithDynamicTextSizes(context, appWidgetIds);
        if (this.timeReceiver1 == null) {
            this.timeReceiver1 = new TimeReceiver1();
            context.getApplicationContext().registerReceiver(this.timeReceiver1,getTimeFilter1());
        }
        context.startService(new Intent(context,UpdateService.class));

    }

    public class TimeReceiver1 extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ComponentName componentName = new ComponentName(context,Clock_Widget.class);
            int appWidgetIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
           updateClockWithDynamicTextSizes(context,appWidgetIds);
        }
    }
    protected void updateClock(Context context, int[] appWidgetIds) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        boolean is24hourFormat = DateFormat.is24HourFormat(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), Constants.layout.digit_clock_widget);
        getCalendar().setTimeInMillis(System.currentTimeMillis());
        String time = DateFormat.format(is24hourFormat ? FORMAT_24_HOURS : FORMAT_12_HOURS, getCalendar()).toString();
        String eeee = DateFormat.format("EEEE", getCalendar()).toString();
        String dd = DateFormat.format("dd", getCalendar()).toString();
        String mmmm = DateFormat.format("MMMM", getCalendar()).toString();
        String yyyy = DateFormat.format("yyyy", getCalendar()).toString();
        remoteViews.setTextViewText(Constants.id.time, time);
        remoteViews.setTextViewText(Constants.id.dayOfMonth, dd);
        remoteViews.setTextViewText(Constants.id.dayOfWeek, eeee);
        remoteViews.setTextViewText(Constants.id.month, mmmm);
        remoteViews.setTextViewText(Constants.id.year, yyyy);
        if (is24hourFormat) {
            remoteViews.setTextViewText(Constants.id.ampm, "");
        } else {
            String dayTime;
            String[] ampm = new DateFormatSymbols().getAmPmStrings();
            String amText = ampm[0].toUpperCase();
            String pmText = ampm[1].toUpperCase();
            if (getCalendar().get(Calendar.AM_PM) == 0) {
                dayTime = amText;
            } else {
                dayTime = pmText;
            }
            remoteViews.setTextViewText(Constants.id.ampm, dayTime);
        }
       PendingIntent clockIntent = Utils.getClockIntent(context);
        if (clockIntent != null) {
            remoteViews.setOnClickPendingIntent(Constants.id.time, clockIntent);
        }
        manager.updateAppWidget(appWidgetIds, remoteViews);
    }

    protected void updateWidgetSize(Context context, int id) {
        try {
            Log.d("DigitClockWidget", "updateWidgetSize " + id);
            Resources res = context.getResources();
            Bundle widgetOptions = AppWidgetManager.getInstance(context).getAppWidgetOptions(id);
            widgetOptions.getInt("appWidgetMinHeight");
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;
            int width = widgetOptions.getInt("appWidgetMinWidth") - 10;
            int maxWidth = widgetOptions.getInt("appWidgetMaxWidth") - 10;
            int height = widgetOptions.getInt("appWidgetMinHeight");
            int maxHeight = widgetOptions.getInt("appWidgetMaxHeight");
            if (width > 0) {
                if (heightPixels > widthPixels) {
                    height = maxHeight;
                }
                context.getSharedPreferences(PREFS_NAME, 0).edit().putInt(id + "width", width).apply();
                context.getSharedPreferences(PREFS_NAME, 0).edit().putInt(id + "height", height).apply();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    protected int calculateTextSize(String text, int maxWidth, int maxHeight, int baseTextSize) {
        Paint paint = getDefaultPaint((float) baseTextSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        while (true) {
            if ((paint.measureText(text) > ((float) maxWidth) || ((float) rect.height()) + paint.getFontMetrics().descent > ((float) maxHeight)) && baseTextSize > 0) {
                baseTextSize--;
                paint.setTextSize((float) baseTextSize);
                paint.getTextBounds(text, 0, text.length(), rect);
            }
            else {
                break;
            }
        }
        return baseTextSize;
    }

    protected Paint getDefaultPaint(float textSize) {
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.SANS_SERIF);
        return paint;
    }


    protected void updateClockWithDynamicTextSizes(Context context, int[] appWidgetIds) {
        try {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), Constants.layout.digit_clock_widget_dynamic);
            getCalendar().setTimeInMillis(System.currentTimeMillis());
            boolean is24hourFormat = DateFormat.is24HourFormat(context);
            String time = DateFormat.format(is24hourFormat ? FORMAT_24_HOURS : FORMAT_12_HOURS, getCalendar()).toString();
            String eeee = DateFormat.format("EEEE", getCalendar()).toString();
            String dd = DateFormat.format("dd", getCalendar()).toString();
            String mmmm = DateFormat.format("MMMM", getCalendar()).toString();
            String yyyy = DateFormat.format("yyyy", getCalendar()).toString();
            String dayTime = "";
            if (!is24hourFormat) {
                String[] ampm = new DateFormatSymbols().getAmPmStrings();
                String amText = ampm[0].toUpperCase();
                String pmText = ampm[1].toUpperCase();
                if (getCalendar().get(Calendar.AM_PM) == 0) {
                    dayTime = amText;
                } else {
                    dayTime = pmText;
                }
            }
            for (int id : appWidgetIds) {
                updateWidgetSize(context, id);
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                float density = displayMetrics.scaledDensity;
                float densityStatic = displayMetrics.density;
                float densityCof = densityStatic / density;
                int width = context.getSharedPreferences(PREFS_NAME, 0).getInt(id + "width", -1);
                int height = context.getSharedPreferences(PREFS_NAME, 0).getInt(id + "height", -1);
                if (width < 0) {
                    updateClock(context, appWidgetIds);
                    return;
                }
                int baseTextSize = width > height ? width / 2 : height / 2;
                int textSize = calculateTextSize(mmmm + dd + " " + dd + eeee, width, height / 4, baseTextSize / 3);
                int textSizeTime = calculateTextSize(time, (int) (((double) width) - (((double) ((int) getDefaultPaint((float) textSize).measureText(dayTime))) * 1.1d)), height / 2, baseTextSize);
                int paddingTopTime = (int) (-getDefaultPaint((float) textSizeTime).getFontMetrics().descent);
                views.setTextViewText(Constants.id.time, time);
                views.setViewPadding(Constants.id.time, 0, paddingTopTime, 0, 0);
                float timeTextSize = ((float) textSizeTime) * densityCof;
                float dayOfMonthTextSize = ((float) (((double) textSize) * 2.2d)) * densityCof;
                if (timeTextSize > 2.0f * dayOfMonthTextSize) {
                    timeTextSize = dayOfMonthTextSize * 2.0f;
                }
                views.setFloat(Constants.id.time, "setTextSize", timeTextSize);
                Rect rect = new Rect();
                getDefaultPaint((float) textSizeTime).getTextBounds(time, 0, time.length(), rect);
                int paddingTopBase = (int) (((double) (((float) rect.height()) * densityStatic)) * 1.2d);
                int paddingTopText = (int) (((double) paddingTopBase) + ((((double) textSize) * 0.2d) * ((double) densityStatic)));
                views.setTextViewText(Constants.id.ampm, dayTime);
                views.setViewPadding(Constants.id.ampm, 0, (int) (((float) paddingTopText) - (((float) textSize) * densityStatic)), 0, 0);
                views.setFloat(Constants.id.ampm, "setTextSize", ((float) textSize) * densityCof);
                views.setViewPadding(Constants.id.dayOfWeek, 0, paddingTopText, 0, 0);
                views.setFloat(Constants.id.dayOfWeek, "setTextSize", ((float) textSize) * densityCof);
                views.setTextViewText(Constants.id.dayOfWeek, eeee);
                views.setViewPadding(Constants.id.month, 0, paddingTopText, 0, 0);
                views.setFloat(Constants.id.month, "setTextSize", ((float) textSize) * densityCof);
                views.setTextViewText(Constants.id.month, mmmm);
                getDefaultPaint((float) textSize).getTextBounds(eeee.toUpperCase(), 0, eeee.length(), rect);
                views.setViewPadding(Constants.id.year, 0, (int) (-getDefaultPaint((float) textSize).getFontMetrics().descent), 0, 0);
                views.setFloat(Constants.id.year, "setTextSize", ((float) (((double) textSize) * 0.8d)) * densityCof);
                views.setTextViewText(Constants.id.year, yyyy);
                views.setViewPadding(Constants.id.dayOfMonth, 0, (int) (((double) paddingTopBase) - (((double) textSize) * 0.3d)), 0, 0);
                views.setFloat(Constants.id.dayOfMonth, "setTextSize", dayOfMonthTextSize);
                views.setTextViewText(Constants.id.dayOfMonth, dd);
                // Pending Intent to start clock
                PendingIntent clockIntent = Utils.getClockIntent(context);
                if (clockIntent != null) {
                    views.setOnClickPendingIntent(Constants.id.time, clockIntent);
                }
                setListenerOnDate(context, views);
                manager.updateAppWidget(id, views);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void setListenerOnDate(Context context, RemoteViews views) {
        Intent calendarIntent = new Intent();
        calendarIntent.setComponent(new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity"));
        views.setOnClickPendingIntent(Constants.id.dateGroup, PendingIntent.getActivity(context, 0, calendarIntent, 0));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }
}



