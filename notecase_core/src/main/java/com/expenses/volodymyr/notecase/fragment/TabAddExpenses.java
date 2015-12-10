package com.expenses.volodymyr.notecase.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;
import com.expenses.volodymyr.notecase.util.MyDragShadowBuilder;
import com.expenses.volodymyr.notecase.util.MyOnDragListener;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class TabAddExpenses extends Fragment {
    private final String DOT = ".";

    LinearLayout left_block, right_block;
    EditText priceInput;
    AutoCompleteTextView nameInput;
    LinearLayout.LayoutParams params;

    @Override
    public void onDestroy(){
        System.out.println("**************** TabAddExpenses.onDestroy");
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("**************** TabAddExpenses.onCreateView");
        View view = inflater.inflate(R.layout.tab_add_expenses, container, false);
        nameInput = (AutoCompleteTextView) view.findViewById(R.id.commodityName);
        priceInput = (EditText) view.findViewById(R.id.commodityPrice);

        left_block = (LinearLayout) view.findViewById(R.id.left_category_block);
        right_block = (LinearLayout) view.findViewById(R.id.right_category_block);
        left_block.setPadding(0, 20, 50, 20);
        right_block.setPadding(50, 20, 0, 20);
        right_block.setGravity(Gravity.RIGHT);
        left_block.setGravity(Gravity.LEFT);


        final int[] to = new int[]{android.R.id.text1};
        final String[] from = new String[] {DBHandler.PRODUCT_NAME};
        final DBHandler dbHandler = DBHandler.getDbHandler(getActivity());
        Cursor cursor = dbHandler.getProductNameCursor();
        SimpleCursorAdapter productNameAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, cursor, from, to, 0);
        productNameAdapter.setStringConversionColumn(cursor.getColumnIndexOrThrow(DBHandler.PRODUCT_NAME));
        productNameAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String partialItemName = null;
                if (constraint != null) {
                    partialItemName = constraint.toString();
                }
                return dbHandler.suggestProductName(partialItemName);
            }
        });
        nameInput.setAdapter(productNameAdapter);



        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        params.width = 150;
        params.height = 150;

        addCategoriesOnScreen();

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        Button moveButton = (Button) view.findViewById(R.id.move_button);
        final Activity activity = getActivity();

        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(activity);
                ClipData clipData = ClipData.newPlainText("mylabel", "mytext");
                View.DragShadowBuilder myShadow = new MyDragShadowBuilder(linearLayout);
                v.startDrag(clipData, myShadow, null, 0);
                return false;
            }
        });

        InputFilter filter = new PriceInputIntentFilter(getActivity());
        priceInput.setFilters(new InputFilter[]{filter});
        return view;
    }


    public void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addCategoriesOnScreen() {
        DBHandler dbHandler = DBHandler.getDbHandler(getActivity());
        List<Category> categoryList = dbHandler.getAllCategories();

        for (int i = 0; i < categoryList.size(); i++) {
            Button categoryButton = new Button(getActivity());
            Category category = categoryList.get(i);
            categoryButton.setText(category.getName());
            categoryButton.setBackgroundColor(category.getColor());
            categoryButton.setPadding(20, 20, 20, 20);
            categoryButton.setLayoutParams(params);
            categoryButton.setId(category.getId());
            if (i % 2 == 0) {
                left_block.addView(categoryButton);
            } else {
                right_block.addView(categoryButton);
            }
            categoryButton.setOnDragListener(new MyOnDragListener(nameInput, priceInput, category.getId(), getActivity()));
        }
    }


    private class PriceInputIntentFilter implements InputFilter {
        Activity activity;

        public PriceInputIntentFilter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String currInput = priceInput.getText().toString() + source;
            if (currInput.equals(".")) {
                return "0.";
            }
            //close keyboard with .12 precision
            int dotIndex = currInput.indexOf(DOT);
            if (dotIndex != -1 && currInput.length() - dotIndex > 2) {
                closeKeyboard(activity);
            }
            return null;
        }
    }
}
