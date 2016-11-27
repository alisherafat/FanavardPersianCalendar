package com.fanavard.alisherafat.fanavardpersiancalendar.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.fanavard.alisherafat.fanavardpersiancalendar.R;
import com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants;
import com.fanavard.alisherafat.fanavardpersiancalendar.service.ApplicationService;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.UpdateUtils;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.fragment.CalendarFragment;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.fragment.ConverterFragment;

public class MainActivity extends AppCompatActivity {
    private Utils utils;
    private UpdateUtils updateUtils;

    private Class<?>[] fragments = {
            null,
            CalendarFragment.class,
            ConverterFragment.class,
    };

    private static final int CALENDAR = 1;

    // Default selected fragment
    private static final int DEFAULT = CALENDAR;

    private int menuPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        utils = Utils.getInstance(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        utils.changeAppLanguage(this);
        utils.loadLanguageResource();
        updateUtils = UpdateUtils.getInstance(getApplicationContext());

        if (!Utils.getInstance(this).isServiceRunning(ApplicationService.class)) {
            startService(new Intent(getBaseContext(), ApplicationService.class));
        }

        updateUtils.update(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else {
            toolbar.setPadding(0, 0, 0, 0);
        }


        selectItem(DEFAULT);

        LocalBroadcastManager.getInstance(this).registerReceiver(dayPassedReceiver,
                new IntentFilter(Constants.LOCAL_INTENT_DAY_PASSED));
    }


    public boolean dayIsPassed = false;

    private BroadcastReceiver dayPassedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dayIsPassed = true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (dayIsPassed) {
            dayIsPassed = false;
            restartActivity();
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dayPassedReceiver);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
     if (menuPosition != DEFAULT) {
            selectItem(DEFAULT);
        } else {
            finish();
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void selectItem(int item) {
        if (item == 2) {
            getSupportActionBar().setTitle("تبدیل تاریخ");
        } else {
            getSupportActionBar().setTitle("تقویم شمسی فناورد");
        }
        if (menuPosition != item) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_holder,
                                (Fragment) fragments[item].newInstance(),
                                fragments[item].getName()
                        ).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            menuPosition = item;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.converter) {
            selectItem(2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
