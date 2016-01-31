package com.expenses.volodymyr.notecase.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.fragment.TabAddExpenses;
import com.expenses.volodymyr.notecase.fragment.TabStatisticExpenses;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.MyFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private static final String TAG = "MainActivity";

    private static final String ADD_TAB = "Add";
    private static final String VIEW_TAB = "View";
    private static final String STATS_TAB = "Stats";
    private static final String SETTING_TAB = "Settings";

    private ViewPager viewPager;
    MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Creating MainActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(ADD_TAB));
        tabLayout.addTab(tabLayout.newTab().setText(VIEW_TAB));
        tabLayout.addTab(tabLayout.newTab().setText(STATS_TAB));
        tabLayout.addTab(tabLayout.newTab().setText(SETTING_TAB));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //save all pages in memory
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "Resuming MainActivity");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying MainActivity");
        super.onDestroy();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //workaround to update tabs. getItemPosition called in adapter
        PagerAdapter adapter = viewPager.getAdapter();
        int position = tab.getPosition();
        if (adapter instanceof MyFragmentPagerAdapter) {
            MyFragmentPagerAdapter myAdapter = (MyFragmentPagerAdapter) adapter;
            switch (position) {
                case 0:
                Fragment tabAdd = myAdapter.getItem(position);
                if (tabAdd instanceof TabAddExpenses) {
                    TabAddExpenses fragment = (TabAddExpenses) tabAdd;
                    if (fragment.isVisible()) {
                        fragment.addCategoriesOnScreen();
                    }
                }
                break;
                case 1:
                    Fragment tabView = myAdapter.getItem(position);
                    if (tabView instanceof TabViewExpenses) {
                        TabViewExpenses fragment = (TabViewExpenses) tabView;
                        if (fragment.isVisible()) {
                            fragment.updateListView();
                        }
                    }
                    break;
                case 2:
                    Fragment tabStatistic = myAdapter.getItem(position);
                    if (tabStatistic instanceof TabStatisticExpenses) {
                        TabStatisticExpenses fragment = (TabStatisticExpenses) tabStatistic;
                        if (fragment.isVisible()) {
                            fragment.setData();
                        }
                    }
                    break;
            }
        }
        //handling direct onTab click
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}