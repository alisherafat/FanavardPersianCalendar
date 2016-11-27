package com.fanavard.alisherafat.fanavardpersiancalendar.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.fanavard.alisherafat.fanavardpersiancalendar.R;
import com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants;
import com.fanavard.alisherafat.fanavardpersiancalendar.app.Widget2x2;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.activity.MainActivity;

import java.util.Calendar;
import java.util.Date;

import calendar.GregorianDate;
import calendar.PersianDate;

/**
 * this class helps to update app widget
 */
public class UpdateUtils {
    private static UpdateUtils myInstance;
    private Context context;
    private PersianDate pastDate;


    private UpdateUtils(Context context) {
        this.context = context;
    }

    public static UpdateUtils getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new UpdateUtils(context);
        }
        return myInstance;
    }

    boolean firstTime = true;

    public void update(boolean updateDate) {
        Utils utils = Utils.getInstance(context);
        utils.changeAppLanguage(context);
        if (firstTime) {
            utils.loadLanguageResource();
            firstTime = false;
        }
        Calendar calendar = utils.makeCalendarFromDate(new Date());
        GregorianDate civil = new GregorianDate(calendar);
        PersianDate persian = utils.getToday();

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //
        // Widget
        //
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.widget2x2);
        String colorInt = utils.getSelectedWidgetTextColor();
        int color = Color.parseColor(colorInt);

        String text1;
        String text2;
        String text3 = "";
        String weekDayName = utils.getWeekDayName(civil);
        String persianDate = utils.dateToString(persian);
        String civilDate = utils.dateToString(civil);
        String date = persianDate + Constants.PERSIAN_COMMA + " " + civilDate;

        String time = utils.getPersianFormattedClock(calendar);
        boolean enableClock = utils.isWidgetClock();

        if (enableClock) {
            text2 = weekDayName + " " + date;
            text1 = time;
            if (utils.iranTime) {
                text3 = "(" + context.getString(R.string.iran_time) + ")";
            }
        } else {
            text1 = weekDayName;
            text2 = date;
        }

        // Widget 2x2
        remoteViews2.setTextColor(R.id.time_2x2, color);
        remoteViews2.setTextColor(R.id.date_2x2, color);
        remoteViews2.setTextColor(R.id.event_2x2, color);
        remoteViews2.setTextColor(R.id.owghat_2x2, color);

        if (enableClock) {
            text2 = weekDayName + " " + persianDate;
            text1 = time;
        } else {
            text1 = weekDayName;
            text2 = persianDate;
        }


        if (pastDate == null || !pastDate.equals(persian) || updateDate) {
            Log.d("UpdateUtils", "change date");
            pastDate = persian;

            String holidays = utils.getEventsTitle(persian, true);

            if (!TextUtils.isEmpty(holidays)) {
                remoteViews2.setTextViewText(R.id.holiday_2x2, utils.shape(holidays));
                remoteViews2.setViewVisibility(R.id.holiday_2x2, View.VISIBLE);
            } else {
                remoteViews2.setViewVisibility(R.id.holiday_2x2, View.GONE);
            }

            String events = utils.getEventsTitle(persian, false);

            if (!TextUtils.isEmpty(events)) {
                remoteViews2.setTextViewText(R.id.event_2x2, utils.shape(events));
                remoteViews2.setViewVisibility(R.id.event_2x2, View.VISIBLE);
            } else {
                remoteViews2.setViewVisibility(R.id.event_2x2, View.GONE);
            }
        }


        remoteViews2.setTextViewText(R.id.time_2x2, utils.shape(text1));
        remoteViews2.setTextViewText(R.id.date_2x2, utils.shape(text2));

        remoteViews2.setOnClickPendingIntent(R.id.widget_layout2x2, launchAppPendingIntent);
        manager.updateAppWidget(new ComponentName(context, Widget2x2.class), remoteViews2);


    }


}
