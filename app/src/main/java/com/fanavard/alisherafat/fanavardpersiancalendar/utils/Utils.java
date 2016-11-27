package com.fanavard.alisherafat.fanavardpersiancalendar.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fanavard.alisherafat.fanavardpersiancalendar.R;
import com.fanavard.alisherafat.fanavardpersiancalendar.adapter.ShapedArrayAdapter;
import com.fanavard.alisherafat.fanavardpersiancalendar.enums.CalendarTypeEnum;
import com.fanavard.alisherafat.fanavardpersiancalendar.models.Day;
import com.fanavard.alisherafat.fanavardpersiancalendar.models.DayEvent;
import com.fanavard.alisherafat.fanavardpersiancalendar.service.BroadcastReceivers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import calendar.AbstractDate;
import calendar.DateConverter;
import calendar.DayOutOfRangeException;
import calendar.GregorianDate;
import calendar.IslamicDate;
import calendar.PersianDate;

import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.AM_IN_PERSIAN;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.ARABIC_DIGITS;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.BROADCAST_RESTART_APP;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_APP_LANGUAGE;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_IRAN_TIME;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_ISLAMIC_OFFSET;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_PERSIAN_DIGITS;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_SELECTED_WIDGET_TEXT_COLOR;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_WIDGET_CLOCK;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.DEFAULT_WIDGET_IN_24;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PERSIAN_COMMA;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PERSIAN_DIGITS;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PM_IN_PERSIAN;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_APP_LANGUAGE;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_IRAN_TIME;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_ISLAMIC_OFFSET;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_PERSIAN_DIGITS;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_SELECTED_WIDGET_TEXT_COLOR;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_WIDGET_CLOCK;
import static com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants.PREF_WIDGET_IN_24;

/**
 * Common utilities that needed for this calendar
 */

public class Utils {

    private final String TAG = Utils.class.getName();
    private Context context;
    private Typeface typeface;
    private SharedPreferences prefs;

    private List<DayEvent> dayEvents;

    private String[] persianMonths;
    private String[] islamicMonths;
    private String[] gregorianMonths;
    private String[] weekDays;

    private String cachedCityKey = "";

    private Utils(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateStoredPreference();
    }

    private static WeakReference<Utils> myWeakInstance;

    public static Utils getInstance(Context context) {
        if (myWeakInstance == null || myWeakInstance.get() == null) {
            myWeakInstance = new WeakReference<>(new Utils(context.getApplicationContext()));
        }
        return myWeakInstance.get();
    }

    /**
     * Text shaping is a essential thing on supporting Arabic script text on older Android versions.
     * It converts normal Arabic character to their presentation forms according to their position
     * on the text.
     *
     * @param text Arabic string
     * @return Shaped text
     */
    public String shape(String text) {
        return (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
                ? ArabicShaping.shape(text)
                : text;
    }


    public void setFontShapeAndGravity(TextView textView) {
        textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }

    public int getIslamicOffset() {
        return Integer.parseInt(prefs.getString(
                PREF_ISLAMIC_OFFSET,
                DEFAULT_ISLAMIC_OFFSET).replace("+", ""));
    }

    private char[] preferredDigits;
    private boolean clockIn24;
    public boolean iranTime;

    public void updateStoredPreference() {
        preferredDigits = isPersianDigitSelected()
                ? PERSIAN_DIGITS
                : ARABIC_DIGITS;

        clockIn24 = prefs.getBoolean(PREF_WIDGET_IN_24, DEFAULT_WIDGET_IN_24);
        iranTime = prefs.getBoolean(PREF_IRAN_TIME, DEFAULT_IRAN_TIME);
    }

    public boolean isPersianDigitSelected() {
        return prefs.getBoolean(PREF_PERSIAN_DIGITS, DEFAULT_PERSIAN_DIGITS);
    }


    public boolean isWidgetClock() {
        return prefs.getBoolean(PREF_WIDGET_CLOCK, DEFAULT_WIDGET_CLOCK);
    }


    public String getAppLanguage() {
        String language = prefs.getString(PREF_APP_LANGUAGE, DEFAULT_APP_LANGUAGE);
        // If is empty for whatever reason (pref dialog bug, etc), return Persian at least
        return TextUtils.isEmpty(language) ? DEFAULT_APP_LANGUAGE : language;
    }


    public String getSelectedWidgetTextColor() {
        return prefs.getString(PREF_SELECTED_WIDGET_TEXT_COLOR, DEFAULT_SELECTED_WIDGET_TEXT_COLOR);
    }

    public PersianDate getToday() {
        return DateConverter.civilToPersian(new GregorianDate(makeCalendarFromDate(new Date())));
    }

    public Calendar makeCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (iranTime) {
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        }
        calendar.setTime(date);
        return calendar;
    }

    public String clockToString(int hour, int minute) {
        return formatNumber(String.format(Locale.ENGLISH, "%d:%02d", hour, minute));
    }


    public String getPersianFormattedClock(Calendar calendar) {
        String timeText = null;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (!clockIn24) {
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                timeText = PM_IN_PERSIAN;
                hour -= 12;
            } else {
                timeText = AM_IN_PERSIAN;
            }
        }

        String result = clockToString(hour, calendar.get(Calendar.MINUTE));
        if (!clockIn24) {
            result = result + " " + timeText;
        }
        return result;
    }

    public String formatNumber(int number) {
        return formatNumber(Integer.toString(number));
    }

    public String formatNumber(String number) {
        if (preferredDigits == ARABIC_DIGITS)
            return number;

        StringBuilder sb = new StringBuilder();
        for (char i : number.toCharArray()) {
            if (Character.isDigit(i)) {
                sb.append(preferredDigits[Integer.parseInt(i + "")]);
            } else {
                sb.append(i);
            }
        }
        return sb.toString();
    }

    public String dateToString(AbstractDate date) {
        return formatNumber(date.getDayOfMonth()) + ' ' + getMonthName(date) + ' ' +
                formatNumber(date.getYear());
    }

    public String dayTitleSummary(PersianDate persianDate) {
        return getWeekDayName(persianDate) + PERSIAN_COMMA + " " + dateToString(persianDate);
    }

    public String[] monthsNamesOfCalendar(AbstractDate date) {
        // the next step would be using them so lets check if they have initialized already
        if (persianMonths == null || gregorianMonths == null || islamicMonths == null)
            loadLanguageResource();

        if (date instanceof PersianDate)
            return persianMonths.clone();
        else if (date instanceof IslamicDate)
            return islamicMonths.clone();
        else
            return gregorianMonths.clone();
    }

    public String getMonthName(AbstractDate date) {
        return monthsNamesOfCalendar(date)[date.getMonth() - 1];
    }

    public String getWeekDayName(AbstractDate date) {
        if (date instanceof IslamicDate)
            date = DateConverter.islamicToCivil((IslamicDate) date);
        else if (date instanceof PersianDate)
            date = DateConverter.persianToGregorian((PersianDate) date);

        if (weekDays == null)
            loadLanguageResource();

        return weekDays[date.getDayOfWeek() % 7];
    }

    public void quickToast(String message) {
        Toast.makeText(context, shape(message), Toast.LENGTH_SHORT).show();
    }


    private String readStream(InputStream is) {
        // http://stackoverflow.com/a/5445161
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String readRawResource(@RawRes int res) {
        return readStream(context.getResources().openRawResource(res));
    }


    private List<DayEvent> readEventsFromJSON() {
        List<DayEvent> result = new ArrayList<>();
        try {
            JSONArray days = new JSONObject(readRawResource(R.raw.events)).getJSONArray("events");

            int length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject event = days.getJSONObject(i);

                int year = event.getInt("year");
                int month = event.getInt("month");
                int day = event.getInt("day");
                String title = event.getString("title");
                boolean holiday = event.getBoolean("holiday");

                result.add(new DayEvent(new PersianDate(year, month, day), title, holiday));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    public List<DayEvent> getEvents(PersianDate day) {
        if (dayEvents == null) {
            dayEvents = readEventsFromJSON();
        }

        List<DayEvent> result = new ArrayList<>();
        for (DayEvent dayEvent : dayEvents) {
            if (dayEvent.getDate().equals(day)) {
                result.add(dayEvent);
            }
        }
        return result;
    }

    public String getEventsTitle(PersianDate day, boolean holiday) {
        String titles = "";
        boolean first = true;
        List<DayEvent> dayDayEvents = getEvents(day);

        for (DayEvent dayEvent : dayDayEvents) {
            if (dayEvent.isHoliday() == holiday) {
                if (first) {
                    first = false;
                } else {
                    titles = titles + "\n";
                }
                titles = titles + dayEvent.getTitle();
            }
        }
        return titles;
    }

    public String getEventTitles(PersianDate day) {
        String titles = "";
        boolean first = true;
        List<DayEvent> dayDayEvents = getEvents(day);

        for (DayEvent dayEvent : dayDayEvents) {
            if (first) {
                first = false;
            } else {
                titles = titles + "\n";
            }
            titles = titles + dayEvent.getTitle();
        }
        return titles;
    }

    public void loadApp() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 1);
        Intent intent = new Intent(context, BroadcastReceivers.class);
        intent.setAction(BROADCAST_RESTART_APP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC, startTime.getTimeInMillis(), pendingIntent);
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Context preferably should be activity context not application
    public void changeAppLanguage(Context context) {
        String localeCode = getAppLanguage().replaceAll("-(IR|AF)", "");
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void loadLanguageResource() {
        int messagesFile = R.raw.messages_fa;

        persianMonths = new String[12];
        islamicMonths = new String[12];
        gregorianMonths = new String[12];
        weekDays = new String[7];

        try {
            JSONObject messages = new JSONObject(readRawResource(messagesFile));

            JSONArray persianMonthsArray = messages.getJSONArray("PersianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                persianMonths[i] = persianMonthsArray.getString(i);

            JSONArray islamicMonthsArray = messages.getJSONArray("IslamicCalendarMonths");
            for (int i = 0; i < 12; ++i)
                islamicMonths[i] = islamicMonthsArray.getString(i);

            JSONArray gregorianMonthsArray = messages.getJSONArray("GregorianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                gregorianMonths[i] = gregorianMonthsArray.getString(i);

            JSONArray weekDaysArray = messages.getJSONArray("WeekDays");
            for (int i = 0; i < 7; ++i)
                weekDays[i] = weekDaysArray.getString(i);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void copyToClipboard(View view) {
        // if it is older than this, the view is also shaped which is not good for copying, so just
        // nvm about backup solution for older Androids
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            CharSequence text = ((TextView) view).getText();
            CopyToClipboard.copyToClipboard(text, context);
            quickToast("«" + text + "»\n" + context.getString(R.string.date_copied_clipboard));
        }
    }

    private static class CopyToClipboard {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static void copyToClipboard(CharSequence text, Context context) {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText("converted date", text));
        }
    }

    public List<Day> getDays(int offset) {
        List<Day> days = new ArrayList<>();
        PersianDate persianDate = getToday();
        int month = persianDate.getMonth() - offset;
        month -= 1;
        int year = persianDate.getYear();

        year = year + (month / 12);
        month = month % 12;
        if (month < 0) {
            year -= 1;
            month += 12;
        }
        month += 1;
        persianDate.setMonth(month);
        persianDate.setYear(year);
        persianDate.setDayOfMonth(1);

        int dayOfWeek = DateConverter.persianToGregorian(persianDate).getDayOfWeek() % 7;

        try {
            PersianDate today = getToday();
            for (int i = 1; i <= 31; i++) {
                persianDate.setDayOfMonth(i);

                Day day = new Day();
                day.setNum(formatNumber(i));
                day.setDayOfWeek(dayOfWeek);

                if (dayOfWeek == 6 || !TextUtils.isEmpty(getEventsTitle(persianDate, true))) {
                    day.setHoliday(true);
                }

                if (getEvents(persianDate).size() > 0) {
                    day.setEvent(true);
                }

                day.setPersianDate(persianDate.clone());

                if (persianDate.equals(today)) {
                    day.setToday(true);
                }

                days.add(day);
                dayOfWeek++;
                if (dayOfWeek == 7) {
                    dayOfWeek = 0;
                }
            }
        } catch (DayOutOfRangeException e) {
            // okay, it was expected
        }

        return days;
    }

    // based on R.array.calendar_type order
    public CalendarTypeEnum calendarTypeFromPosition(int position) {
        if (position == 0)
            return CalendarTypeEnum.SHAMSI;
        else if (position == 1)
            return CalendarTypeEnum.ISLAMIC;
        else
            return CalendarTypeEnum.GREGORIAN;
    }

    @IdRes
    public final static int DROPDOWN_LAYOUT = R.layout.select_dialog_item;

    public int fillYearMonthDaySpinners(Context context, Spinner calendarTypeSpinner,
                                        Spinner yearSpinner, Spinner monthSpinner,
                                        Spinner daySpinner) {
        AbstractDate date;
        PersianDate newDatePersian = getToday();
        GregorianDate newDateCivil = DateConverter.persianToGregorian(newDatePersian);
        IslamicDate newDateIslamic = DateConverter.persianToIslamic(newDatePersian);

        date = newDateCivil;
        switch (calendarTypeFromPosition(calendarTypeSpinner.getSelectedItemPosition())) {
            case GREGORIAN:
                date = newDateCivil;
                break;

            case ISLAMIC:
                date = newDateIslamic;
                break;

            case SHAMSI:
                date = newDatePersian;
                break;
        }

        // years spinner init.
        String[] years = new String[200];
        int startingYearOnYearSpinner = date.getYear() - years.length / 2;
        for (int i = 0; i < years.length; ++i) {
            years[i] = formatNumber(i + startingYearOnYearSpinner);
        }
        yearSpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, years));
        yearSpinner.setSelection(years.length / 2);
        //

        // month spinner init.
        String[] months = monthsNamesOfCalendar(date);
        for (int i = 0; i < months.length; ++i) {
            months[i] = months[i] + " / " + formatNumber(i + 1);
        }
        monthSpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, months));
        monthSpinner.setSelection(date.getMonth() - 1);
        //

        // days spinner init.
        String[] days = new String[31];
        for (int i = 0; i < days.length; ++i) {
            days[i] = formatNumber(i + 1);
        }
        daySpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, days));
        daySpinner.setSelection(date.getDayOfMonth() - 1);
        //

        return startingYearOnYearSpinner;
    }
}
