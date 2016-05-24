package com.expenses.volodymyr.notecase.fragment;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
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

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.util.DBHandler;
import com.domain.volodymyr.notecase.manager.CategoryManager;
import com.domain.volodymyr.notecase.manager.CategoryManagerImpl;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.util.MyDragShadowBuilder;
import com.expenses.volodymyr.notecase.util.OnDragDropListener;

import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class TabAddExpenses extends Fragment {
    private static final String TAG = "TabAddExpenses";
    private final String DOT = ".";

    private LinearLayout leftBlock, rightBlock;
    private EditText priceInput;
    private AutoCompleteTextView nameInput;

    private CategoryManager categoryManager;
    private ProductManager productManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Add fragment");
        View view = inflater.inflate(R.layout.tab_add_expenses, container, false);
        nameInput = (AutoCompleteTextView) view.findViewById(R.id.commodityName);
        priceInput = (EditText) view.findViewById(R.id.commodityPrice);

        leftBlock = (LinearLayout) view.findViewById(R.id.left_category_block);
        rightBlock = (LinearLayout) view.findViewById(R.id.right_category_block);

        categoryManager = new CategoryManagerImpl(getContext());
        productManager = new ProductManagerImpl(getContext());

        SimpleCursorAdapter productNameAdapter = getProductNameQueryAdapter();
        if (productNameAdapter != null) {
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
                return true;
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
        List<Category> categoryList = categoryManager.getAllCategories();

        if (categoryList.size() == leftBlock.getChildCount() + rightBlock.getChildCount()) {
            return;
        }
        leftBlock.removeAllViews();
        rightBlock.removeAllViews();

        for (int i = 0; i < categoryList.size(); i++) {
            LinearLayout categoryLayout;
            if (i % 2 == 0) {
                categoryLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_category_image_left, null);
            } else {
                categoryLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_category_image_right, null);
            }
            ImageView categoryView = (ImageView) categoryLayout.findViewById(R.id.category_drawable_image);

            Category category = categoryList.get(i);
            categoryLayout.setTag(R.string.category_name_tag, category.getName());
            categoryView.setImageResource(category.getImage());
            Drawable drawable = getResources().getDrawable(R.drawable.category_shape_small);
            drawable.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);
            categoryView.setBackground(drawable);
            if (i % 2 == 0) {
                leftBlock.addView(categoryLayout);
            } else {
                rightBlock.addView(categoryLayout);
            }
            Log.i(TAG, "Add category to AddExpense fragment: " + leftBlock.getChildCount() + ":" + rightBlock.getChildCount());
            categoryLayout.setOnDragListener(new OnDragDropListener(nameInput, priceInput, category.getId(), getActivity()));
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
        Cursor cursor = productManager.getProductNameCursor();
        SimpleCursorAdapter productNameAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, cursor, from, to, 0);
        productNameAdapter.setStringConversionColumn(cursor.getColumnIndex(DBHandler.PRODUCT_NAME));
        productNameAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint != null) {
                    return productManager.suggestProductName(constraint.toString());
                }
                return null;
            }
        });
        return productNameAdapter;
    }

}
