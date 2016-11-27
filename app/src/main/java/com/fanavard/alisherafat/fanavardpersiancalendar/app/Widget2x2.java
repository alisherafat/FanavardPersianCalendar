package com.fanavard.alisherafat.fanavardpersiancalendar.app;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.fanavard.alisherafat.fanavardpersiancalendar.service.ApplicationService;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.UpdateUtils;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;


/**
 * this class is for handling app widget programmatically
 */
public class Widget2x2 extends AppWidgetProvider {

    /**
     * gets called when an event is triggered from widget, we'll handle these events in UpdateUtils class
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Utils.getInstance(context).isServiceRunning(ApplicationService.class)) {
            context.startService(new Intent(context, ApplicationService.class));
        }
        UpdateUtils.getInstance(context).update(true);
    }

}
