package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.ViewCategoryActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkret on 04.12.15.
 */
public class TabSettings extends Fragment {
    private static final String TAG = "TabSettings";
    private List<String> settings;

    public TabSettings() {
        super();
        settings = new ArrayList<>();
        settings.add("Manage category");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Settings fragment");

        View view = inflater.inflate(R.layout.tab_settings, container, false);
        ListView listView = (ListView) view.findViewById(R.id.settings_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settings);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //add logic for different setting options
                Intent intent = new Intent(getActivity(), ViewCategoryActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    public void onDestroy() {
        Log.d(TAG, "Destroying Settings fragment");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming Settigns fragment");
        super.onResume();
    }

}
