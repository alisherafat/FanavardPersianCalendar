package com.fanavard.alisherafat.fanavardpersiancalendar.view.fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanavard.alisherafat.fanavardpersiancalendar.R;
import com.fanavard.alisherafat.fanavardpersiancalendar.adapter.CalendarAdapter;
import com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants;
import com.fanavard.alisherafat.fanavardpersiancalendar.utils.Utils;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.activity.GoogleEventsActivity;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.dialog.SelectDayDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import calendar.DateConverter;
import calendar.GregorianDate;
import calendar.PersianDate;

public class CalendarFragment extends Fragment
        implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.txtToday) AppCompatTextView txtToday;
    @BindView(R.id.imgTodayIcon) AppCompatImageView imgTodayIcon;
    @BindView(R.id.txtWeekDayName) TextView txtWeekDayName;
    @BindView(R.id.txtSolarDate) AppCompatTextView txtSolarDate;
    @BindView(R.id.txtGregorianDate) AppCompatTextView txtGregorianDate;
    @BindView(R.id.txtIslamicDate) AppCompatTextView txtIslamicDate;
    @BindView(R.id.txtEventCardTitle) AppCompatTextView txtEventCardTitle;
    @BindView(R.id.txtHolidayTitle) AppCompatTextView txtHolidayTitle;
    @BindView(R.id.txtEventTitle) AppCompatTextView txtEventTitle;
    @BindView(R.id.cardEvent) CardView cardEvent;
    @BindView(R.id.mainView) LinearLayoutCompat mainView;


    private Utils utils;
    private int viewPagerPosition;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);

        utils = Utils.getInstance(getContext());
        viewPagerPosition = 0;


        viewPager.setAdapter(new CalendarAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(Constants.MONTHS_LIMIT / 2);

        viewPager.addOnPageChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setupLayoutTransition();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bringTodayYearMonth();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setStartDelay(LayoutTransition.APPEARING, 500);
        transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 500);
        mainView.setLayoutTransition(transition);
    }

    public void changeMonth(int position) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + position, true);
    }

    public void selectDay(PersianDate persianDate) {
        txtWeekDayName.setText(utils.shape(utils.getWeekDayName(persianDate)));
        txtSolarDate.setText(utils.shape(utils.dateToString(persianDate)));
        GregorianDate gregorianDate = DateConverter.persianToGregorian(persianDate);
        txtGregorianDate.setText(utils.shape(utils.dateToString(gregorianDate)));
        txtIslamicDate.setText(utils.shape(utils.dateToString(
                DateConverter.gregorianToIslamic(gregorianDate, utils.getIslamicOffset()))));

        if (utils.getToday().equals(persianDate)) {
            txtToday.setVisibility(View.GONE);
            imgTodayIcon.setVisibility(View.GONE);
            if (utils.iranTime) {
                txtWeekDayName.setText(txtWeekDayName.getText() +
                        utils.shape(" (" + getString(R.string.iran_time) + ")"));
            }
        } else {
            txtToday.setVisibility(View.VISIBLE);
            imgTodayIcon.setVisibility(View.VISIBLE);
        }

        showEvent(persianDate);
    }

    public void onLongClickDay(PersianDate persianDate) {
        GregorianDate gd = DateConverter.persianToGregorian(persianDate);
        String clickedDayInString = gd.getYear() + "-" + gd.getMonth() + "-" + gd.getDayOfMonth();
        Intent intent = new Intent(getContext(), GoogleEventsActivity.class);
        intent.putExtra(GoogleEventsActivity.DATE, clickedDayInString);
        startActivity(intent);
    }

    private void showEvent(PersianDate persianDate) {
        String holidays = utils.getEventsTitle(persianDate, true);
        String events = utils.getEventsTitle(persianDate, false);

        cardEvent.setVisibility(View.GONE);
        txtHolidayTitle.setVisibility(View.GONE);
        txtEventTitle.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(holidays)) {
            txtHolidayTitle.setText(utils.shape(holidays));
            txtHolidayTitle.setVisibility(View.VISIBLE);
            cardEvent.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(events)) {
            txtEventTitle.setText(utils.shape(events));
            txtEventTitle.setVisibility(View.VISIBLE);
            cardEvent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.today:
                bringTodayYearMonth();
                break;

        }
    }

    private void bringTodayYearMonth() {
        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT,
                Constants.BROADCAST_TO_MONTH_FRAGMENT_RESET_DAY);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, -1);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        if (viewPager.getCurrentItem() != Constants.MONTHS_LIMIT / 2) {
            viewPager.setCurrentItem(Constants.MONTHS_LIMIT / 2);
        }

        selectDay(utils.getToday());
    }

    public void bringDate(PersianDate date) {
        PersianDate today = utils.getToday();
        viewPagerPosition =
                (today.getYear() - date.getYear()) * 12 + today.getMonth() - date.getMonth();

        viewPager.setCurrentItem(viewPagerPosition + Constants.MONTHS_LIMIT / 2);

        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT, viewPagerPosition);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, date.getDayOfMonth());

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        selectDay(date);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        viewPagerPosition = position - Constants.MONTHS_LIMIT / 2;

        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT, viewPagerPosition);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, -1);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        txtToday.setVisibility(View.VISIBLE);
        imgTodayIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.action_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_to:
                SelectDayDialog dialog = new SelectDayDialog();
                dialog.show(getChildFragmentManager(), SelectDayDialog.class.getName());
                break;
            case R.id.go_to_today:
                bringTodayYearMonth();
                break;
            default:
                break;
        }
        return true;
    }

    public int getViewPagerPosition() {
        return viewPagerPosition;
    }
}
