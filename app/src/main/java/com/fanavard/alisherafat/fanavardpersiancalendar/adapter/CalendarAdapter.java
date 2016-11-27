package com.fanavard.alisherafat.fanavardpersiancalendar.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fanavard.alisherafat.fanavardpersiancalendar.app.Constants;
import com.fanavard.alisherafat.fanavardpersiancalendar.view.fragment.MonthFragment;


/**
 * this adapter represents a specific month based on user scroll
 */
public class CalendarAdapter extends FragmentStatePagerAdapter {

    public CalendarAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        MonthFragment fragment = new MonthFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.OFFSET_ARGUMENT, position - Constants.MONTHS_LIMIT / 2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return Constants.MONTHS_LIMIT;
    }
}
