package com.expenses.volodymyr.notecase.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.expenses.volodymyr.notecase.fragment.TabAddExpenses;
import com.expenses.volodymyr.notecase.fragment.TabSettings;
import com.expenses.volodymyr.notecase.fragment.TabStatisticExpenses;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkret on 02.12.15.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "MyFragmentPagerAdapter";
    public Map<Integer, Fragment> tabs = new HashMap<>();
    {
        tabs.put(0, new TabViewExpenses());
        tabs.put(1, new TabAddExpenses());
        tabs.put(2, new TabStatisticExpenses());
        tabs.put(3, new TabSettings());
    }

    public MyFragmentPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        if (tabCount != tabs.size()){
            Log.wtf(TAG, "TabLayout and PagerAdapter has different tab count");
            throw new IllegalArgumentException("MyFragmentPagerAdapter has " + tabs.size() + " tabs and TabLayout has " + tabCount);
        }
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "Return tab at position: " + position);
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "Instantiating tab item at position: " + position);
        return super.instantiateItem(container, position);
    }

    //TODO Change instantiateItem/destroyItem implementation for better performance;
    //http://startandroid.ru/ru/uroki/vse-uroki-spiskom/228-urok-125-viewpager.html
    //http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
    //My approach is to use the setTag() method for any instantiated view in the instantiateItem() method.
    // So when you want to change the data or invalidate the view that you need, you can call the findViewWithTag()
    // method on the ViewPager to retrieve the previously instantiated view and modify/use it
    // as you want without having to delete/create a new view each time you want to update some value.
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
}
