package com.expenses.volodymyr.notecase.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
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
    private static final String TAG = "TabAddExpenses";
    private final String DOT = ".";

    LinearLayout left_block, right_block;
    EditText priceInput;
    AutoCompleteTextView nameInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Add fragment");
        View view = inflater.inflate(R.layout.tab_add_expenses, container, false);
        nameInput = (AutoCompleteTextView) view.findViewById(R.id.commodityName);
        priceInput = (EditText) view.findViewById(R.id.commodityPrice);

        left_block = (LinearLayout) view.findViewById(R.id.left_category_block);
        right_block = (LinearLayout) view.findViewById(R.id.right_category_block);


        SimpleCursorAdapter productNameAdapter = getProductNameQueryAdapter();
        if (productNameAdapter!=null){
            nameInput.setAdapter(productNameAdapter);
        }

        addCategoriesOnScreen();

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        Button moveButton = (Button) view.findViewById(R.id.move_button);

        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                ClipData clipData = ClipData.newPlainText("mylabel", "mytext");
                View.DragShadowBuilder myShadow = new MyDragShadowBuilder(linearLayout);
                v.startDrag(clipData, myShadow, null, 0);
                return false;
            }
        });

        InputFilter filter = new PriceInputIntentFilter();
        priceInput.setFilters(new InputFilter[]{filter});
        return view;
    }


    public void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming Add fragment");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying Add fragment");
        super.onDestroy();
    }

    public void addCategoriesOnScreen() {
        DBHandler dbHandler = DBHandler.getDbHandler(getActivity());
        List<Category> categoryList = dbHandler.getAllCategories();

        if (categoryList.size() == left_block.getChildCount() + right_block.getChildCount()) {
            return;
        }
        left_block.removeAllViews();
        right_block.removeAllViews();

        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f);
        leftParams.setMargins(0, 30, 0, 30);
        leftParams.gravity = Gravity.LEFT;
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f);
        rightParams.gravity = Gravity.RIGHT;
        rightParams.setMargins(0, 30, 0, 30);

        for (int i = 0; i < categoryList.size(); i++) {
            ImageView categoryView = new ImageView(getActivity());
            Category category = categoryList.get(i);
            categoryView.setBackgroundColor(category.getColor());
            categoryView.setImageResource(category.getImage());

            if (i % 2 == 0) {
                left_block.addView(categoryView);
                categoryView.setLayoutParams(leftParams);
            } else {
                right_block.addView(categoryView);
                categoryView.setLayoutParams(rightParams);
            }
            Log.i(TAG, "Add category to AddExpense fragment: " + left_block.getChildCount() + ":" + right_block.getChildCount());
            categoryView.setOnDragListener(new MyOnDragListener(nameInput, priceInput, category.getId(), getActivity()));
        }
    }


    private class PriceInputIntentFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String currInput = priceInput.getText().toString() + source;
            if (currInput.equals(".")) {
                return "0.";
            }
            //close keyboard within .12 precision
            int dotIndex = currInput.indexOf(DOT);
            if (dotIndex != -1 && currInput.length() - dotIndex > 2) {
                closeKeyboard();
            }
            return null;
        }
    }

    public SimpleCursorAdapter getProductNameQueryAdapter() {
        final int[] to = new int[]{android.R.id.text1};
        final String[] from = new String[]{DBHandler.PRODUCT_NAME};
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
        return productNameAdapter;
    }
}
