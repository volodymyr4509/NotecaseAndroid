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
    public static String VIEW_TAB = "view";
    public static String ADD_TAB = "add";
    public static String STATISTIC_TAB = "stat";
    public static String SETTING_TAB = "setting";
    int mNumOfTabs;
    public Map<String, Object> fragments = new HashMap<>();

    public MyFragmentPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabViewExpenses tab1 = new TabViewExpenses();
                return tab1;
            case 1:
                TabAddExpenses tab2 = new TabAddExpenses();
                return tab2;
            case 2:
                TabStatisticExpenses tab3 = new TabStatisticExpenses();
                return tab3;
            case 3:
                TabSettings tab4 = new TabSettings();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        String key = "";
        switch (position){
            case 0: key = VIEW_TAB;
                break;
            case 1: key = ADD_TAB;
                break;
            case 2: key = STATISTIC_TAB;
                break;
            case 3: key = SETTING_TAB;
                break;
        }
        fragments.put(key, item);
        return item;
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
