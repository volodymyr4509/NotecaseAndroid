package com.expenses.volodymyr.notecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.MyFragmentPagerAdapter;
import com.expenses.volodymyr.notecase.fragment.Navigation;
import com.expenses.volodymyr.notecase.fragment.TabAddExpenses;
import com.expenses.volodymyr.notecase.fragment.TabStatisticExpenses;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, Navigation.OnControlsClickListener {
    private static final String TAG = "MainActivity";

    private static final String ADD_TAB = "Add";
    private static final String VIEW_TAB = "View";
    private static final String STATS_TAB = "Stats";
    private static final String SETTING_TAB = "Settings";

    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private UserManager userManager;

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

        userManager = new UserManagerImpl(this);

        //save all pages in memory
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Sync User list");
        new SafeAsyncTask<Void, Void, Boolean>(this) {
            @Override
            public Boolean doInBackgroundSafe() throws AuthenticationException {
                return userManager.syncUsers();
            }
        }.execute();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userManager.getUserOwner() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying MainActivity");
        super.onDestroy();
    }

    @Override
    public void onControlsClickListener() {
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());

        if (fragment instanceof TabViewExpenses) {
            TabViewExpenses tabViewExpenses = (TabViewExpenses) fragment;
            tabViewExpenses.updateListView();
        }
        if (fragment instanceof TabStatisticExpenses) {
            TabStatisticExpenses tabStatisticExpenses = (TabStatisticExpenses) fragment;
            tabStatisticExpenses.updateStatistics();
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //workaround to update tabs. getItemPosition called in adapter
        PagerAdapter adapter = viewPager.getAdapter();
        int position = tab.getPosition();

        if (adapter != null && adapter instanceof MyFragmentPagerAdapter) {
            MyFragmentPagerAdapter fragmentAdapter = (MyFragmentPagerAdapter) adapter;
            Fragment fragment = fragmentAdapter.getItem(position);

            switch (position) {
                case 0:
                    TabAddExpenses tabAdd = (TabAddExpenses) fragment;
                    tabAdd.addCategoriesOnScreen();
                    break;
                case 1:
                    TabViewExpenses tabView = (TabViewExpenses) fragment;
                    Navigation navView = (Navigation) fragment.getChildFragmentManager().findFragmentByTag(getString(R.string.view_navigation_key));
                    if (navView != null) {
                        navView.updateCheckedRadio();
                    }
                    tabView.updateListView();
                    break;
                case 2:
                    TabStatisticExpenses tabStats = (TabStatisticExpenses) fragment;
                    Navigation navStats = (Navigation) fragment.getChildFragmentManager().findFragmentByTag(getString(R.string.stats_navigation_key));
                    if (navStats != null) {
                        navStats.updateCheckedRadio();
                    }
                    tabStats.updateStatistics();
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