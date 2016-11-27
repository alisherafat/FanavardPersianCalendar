package com.fanavard.alisherafat.fanavardpersiancalendar.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;

import calendar.DateConverter;
import calendar.GregorianDate;
import calendar.PersianDate;

public class ServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleReceivedIntent();
    }

    /**
     * this method gets called when an app tries to get some information via intent<br/>
     * sending an intent with ACTION_VIEW with a gregorian date as data is needed<br/>
     */
    private void handleReceivedIntent() {
        Utils utils = Utils.getInstance(this);

        Intent intent = getIntent();
        if (intent.getType().equals("text/plain")) {
            String data = intent.getDataString();
            String[] dateParts = data.split("-");
            GregorianDate gregorianDate = new GregorianDate(
                    Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
            PersianDate persianDate = DateConverter.civilToPersian(gregorianDate);

            String strPersianDate = persianDate.getYear() + "-" + persianDate.getMonth() + "-" + persianDate.getDayOfMonth();

            Intent result = new Intent();
            result.setData(Uri.parse(strPersianDate));
            result.putExtra(Intent.EXTRA_TEXT, utils.getEventTitles(persianDate));

            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
