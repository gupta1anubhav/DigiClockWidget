package com.anubhav87.clockwidget;

public final class Constants {
    static final String CLOCK_WIDGET_UPDATE = "com.anubhav87.clockwidget.Clock_Widget.CLOCK_WIDGET_UPDATE";

    public static final class attr {
    }

    public static final class drawable {
        public static final int ic_launcher_main = R.drawable.ic_launcher_foreground;
      //  public static final int icon = R.drawable.icon;
       // public static final int preview_big = R.drawable.preview_big;
      //  public static final int preview_small = R.drawable.preview_small;
    }

    public static final class id {
        static final int ampm = R.id.ampm;
        static final int dateGroup = R.id.dateGroup;
        static final int dayOfMonth = R.id.dayOfMonth;
        static final int dayOfWeek = R.id.dayOfWeek;
        public static final int layout = R.id.layout;
        static final int month = R.id.month;
        static final int time = R.id.time;
        static final int year = R.id.year;
    }

    static final class layout {
        static final int digit_clock_widget = R.layout.clock_weather_widget;
        static final int digit_clock_widget_dynamic = R.layout.clock_weather_widget_dynamic;
    }

}