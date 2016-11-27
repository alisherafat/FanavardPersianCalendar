package com.fanavard.alisherafat.fanavardpersiancalendar.app;

/**
 * Static constant values for using in app (actually this is a configuration file)
 */
public class Constants {

    public static final String LOCAL_INTENT_DAY_PASSED = "day-passed";

    public static final String LOCATION_PERMISSION_RESULT = "location-permission-result";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 23;

    public static final String PREF_ISLAMIC_OFFSET = "islamicOffset";
    public static final String PREF_WIDGET_IN_24 = "WidgetIn24";
    public static final String PREF_IRAN_TIME = "IranTime";
    public static final String PREF_PERSIAN_DIGITS = "PersianDigits";
    public static final String PREF_WIDGET_CLOCK = "WidgetClock";
    public static final String PREF_APP_LANGUAGE = "AppLanguage";
    public static final String PREF_SELECTED_WIDGET_TEXT_COLOR = "SelectedWidgetTextColor";

    public static final String DEFAULT_ISLAMIC_OFFSET = "0";
    public static final String DEFAULT_APP_LANGUAGE = "fa";
    public static final String DEFAULT_SELECTED_WIDGET_TEXT_COLOR = "#ffffffff";
    public static final boolean DEFAULT_WIDGET_IN_24 = true;
    public static final boolean DEFAULT_IRAN_TIME = false;
    public static final boolean DEFAULT_PERSIAN_DIGITS = true;
    public static final boolean DEFAULT_WIDGET_CLOCK = true;


    public static final int MONTHS_LIMIT = 5000; // this should be an even number
    public static final String OFFSET_ARGUMENT = "OFFSET_ARGUMENT";
    public static final String BROADCAST_INTENT_TO_MONTH_FRAGMENT = "BROADCAST_INTENT_TO_MONTH_FRAGMENT";
    public static final String BROADCAST_FIELD_TO_MONTH_FRAGMENT = "BROADCAST_FIELD_TO_MONTH_FRAGMENT";
    public static final String BROADCAST_FIELD_SELECT_DAY = "BROADCAST_FIELD_SELECT_DAY";
    public static final String BROADCAST_RESTART_APP = "BROADCAST_RESTART_APP";
    public static final int BROADCAST_TO_MONTH_FRAGMENT_RESET_DAY = Integer.MAX_VALUE;

    public static final char PERSIAN_COMMA = '،';

    public static final String[] FIRST_CHAR_OF_DAYS_OF_WEEK_NAME = {"ش", "ی", "د", "س",
            "چ", "پ", "ج"};
    public static final char[] ARABIC_DIGITS = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9'};
    public static final char[] PERSIAN_DIGITS = {'۰', '۱', '۲', '۳', '۴', '۵', '۶',
            '۷', '۸', '۹'};
    public static final String AM_IN_PERSIAN = "ق.ظ";
    public static final String PM_IN_PERSIAN = "ب.ظ";

}
