package com.expenses.volodymyr.notecase.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.fragment.TabStatisticExpenses;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.MyFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private ViewPager viewPager;
    MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("**************** MainActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("View"));
        tabLayout.addTab(tabLayout.newTab().setText("Add"));
        tabLayout.addTab(tabLayout.newTab().setText("Statistic"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //save all pages in memory
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onDestroy() {
        System.out.println("**************** MainActivity.onDestroy");

        super.onDestroy();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //workaround to update tabs. getItemPosition called in adapter
        PagerAdapter adapter = viewPager.getAdapter();
        int position = tab.getPosition();
        if (adapter instanceof MyFragmentPagerAdapter) {
            MyFragmentPagerAdapter myAdapter = (MyFragmentPagerAdapter) adapter;
            if (position == 0) {
                Object tabView = myAdapter.fragments.get(MyFragmentPagerAdapter.VIEW_TAB);
                if (tabView instanceof TabViewExpenses) {
                    TabViewExpenses fragment = (TabViewExpenses) tabView;
                    if (fragment.isVisible()) {
                        fragment.updateListView();
                    }
                }
            }
            if (position == 2) {
                Object tabStatistic = myAdapter.fragments.get(MyFragmentPagerAdapter.STATISTIC_TAB);
                if (tabStatistic instanceof TabStatisticExpenses) {
                    TabStatisticExpenses fragment = (TabStatisticExpenses) tabStatistic;

                    if (fragment.isVisible()) {
                        fragment.setData();
                    }
                }
            }

        }

//        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}