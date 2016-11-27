package com.fanavard.alisherafat.fanavardpersiancalendar.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.UpdateUtils;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;

/**
 * Startup broadcast receiver to handle Screen on and Time Change events that are emitted by system
 *
 */
public class BroadcastReceivers extends BroadcastReceiver {
    private Context context;
    private UpdateUtils updateUtils;
    private Utils utils;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utils = Utils.getInstance(context);
        updateUtils = UpdateUtils.getInstance(context);

        if (intent != null && intent.getAction() != null && !TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                    intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) ||
                    intent.getAction().equals(Constants.BROADCAST_RESTART_APP)) {

                if (!Utils.getInstance(context).isServiceRunning(ApplicationService.class)) {
                    context.startService(new Intent(context, ApplicationService.class));
                }

            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK) ||
                    intent.getAction().equals(Intent.ACTION_TIME_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                updateUtils.update(false);

            } else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {

                updateUtils.update(true);
                utils.loadApp();
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(new Intent(Constants.LOCAL_INTENT_DAY_PASSED));

            }
        }
    }

}
