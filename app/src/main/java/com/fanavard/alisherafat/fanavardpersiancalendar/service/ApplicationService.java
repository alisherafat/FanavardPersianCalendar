package com.fanavard.alisherafat.fanavardpersiancalendar.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fanavard.alisherafat.fanavardpersiancalendar.utils.UpdateUtils;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;

import java.lang.ref.WeakReference;


/**
 * The Calendar Service that updates widget time and clock and build/update
 * calendar notification.
 * (Long running service )
 */
public class ApplicationService extends Service {

    private static WeakReference<ApplicationService> instance;

    @Nullable
    public static ApplicationService getInstance() {
        return instance == null ? null : instance.get();
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = new WeakReference<>(this);
        UpdateUtils updateUtils = UpdateUtils.getInstance(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(new BroadcastReceivers(), intentFilter);

        Utils utils = Utils.getInstance(getBaseContext());
        utils.loadApp();
        updateUtils.update(true);

        return START_STICKY;
    }
}
