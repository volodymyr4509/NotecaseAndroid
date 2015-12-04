package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.AddEditCategoryActivity;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;

import java.util.List;

/**
 * Created by vkret on 04.12.15.
 */
public class TabViewCategory extends Fragment {
    public static final String CATEGORY_ID_KEY = "categoryId";


    private EditText categoryName;
    private SeekBar colorSeekBar;
    private View resultColor;
    private Button saveButton;
    private int categoryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_view_category, container, false);
        ListView listView = (ListView) view.findViewById(R.id.categoriesList);
        Button addCategoryButton = (Button) view.findViewById(R.id.addNewCategory);

        DBHandler dbHandler = DBHandler.getDbHandler(getActivity());
        List<Category> categoryList = dbHandler.getAllCategories();

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, categoryList);

        listView.setAdapter(adapter);


        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
                Category category = (Category) parent.getAdapter().getItem(position);
                intent.putExtra(CATEGORY_ID_KEY, category.getId());
                startActivity(intent);
            }
        });
        return view;
    }
}
