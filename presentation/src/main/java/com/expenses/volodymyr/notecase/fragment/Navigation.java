package com.expenses.volodymyr.notecase.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.expenses.volodymyr.notecase.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by volodymyr on 13.05.16.
 */
public class Navigation extends Fragment implements View.OnClickListener {
    private static final String TAG = "Navigation";

    public static long begin, end;
    public static SelectedTimeRange timeRange;

    private ImageButton navigateLeft, navigateRight;
    private Calendar calendar = GregorianCalendar.getInstance();
    private Snackbar snack;
    private RadioButton day, week, month;
    private OnControlsClickListener callBack;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.navigation_fragment, container, false);

        day = (RadioButton) view.findViewById(R.id.last_24_hours);
        week = (RadioButton) view.findViewById(R.id.last_week);
        month = (RadioButton) view.findViewById(R.id.last_month);

        //set onClickListener instead of onCheckedChangedListener because the last one calls onCheckedChanged twice
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        timeRange = SelectedTimeRange.DAY;

        navigateLeft = (ImageButton) view.findViewById(R.id.navigate_left);
        navigateLeft.setOnClickListener(this);
        navigateRight = (ImageButton) view.findViewById(R.id.navigate_right);
        navigateRight.setOnClickListener(this);

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        begin = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, +1);
        end = calendar.getTimeInMillis();
        return view;
    }

    @Override
    public void onResume() {
        snack = Snackbar.make(navigateLeft, "[" + new Date(begin) + ":" + new Date(end) + "]", Snackbar.LENGTH_SHORT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snack.getView().setLayoutParams(params);

        super.onResume();
    }

    public void updateCheckedRadio(){
        switch (timeRange){
            case DAY: day.setChecked(true);
                break;
            case WEEK: week.setChecked(true);
                break;
            case MONTH: month.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);

        switch (v.getId()) {
            case R.id.navigate_left:
                moveLeft();
                break;
            case R.id.navigate_right:
                moveRight();
                break;
            case R.id.last_24_hours:
                begin = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                end = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, -1);

                timeRange = SelectedTimeRange.DAY;
                break;
            case R.id.last_week:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                end = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                begin = calendar.getTimeInMillis();

                timeRange = SelectedTimeRange.WEEK;
                break;
            case R.id.last_month:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                end = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                begin = calendar.getTimeInMillis();

                timeRange = SelectedTimeRange.MONTH;
                break;
        }
        snack.setText(df.format(begin) + " : " + df.format(end));
        snack.show();

        Log.v(TAG, "Pick time range: " + new Date(begin) + " : " + new Date(end));
        callBack.onControlsClickListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callBack = (OnControlsClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnControlsClickListener");
        }
    }

    private void moveLeft() {
        end = begin;

        switch (timeRange) {
            case DAY:
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, -1);
                break;
        }

        //change moving direction left-right
        if (calendar.getTimeInMillis() == end) {
            moveLeft();
        }

        begin = calendar.getTimeInMillis();
    }

    private void moveRight() {
        begin = end;

        switch (timeRange) {
            case DAY:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, 1);
                break;
        }

        //change moving direction left-right
        if (calendar.getTimeInMillis() == begin) {
            moveRight();
        }

        end = calendar.getTimeInMillis();
    }

    public interface OnControlsClickListener {
        void onControlsClickListener();
    }

    public enum SelectedTimeRange{
        DAY,
        WEEK,
        MONTH
    }

}
