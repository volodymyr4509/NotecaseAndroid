package com.expenses.volodymyr.notecase.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.expenses.volodymyr.notecase.R;

/**
 * Created by vkret on 02.12.15.
 */
public class TabStatisticExpenses extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_statistic_expenses, container, false);
    }
}
