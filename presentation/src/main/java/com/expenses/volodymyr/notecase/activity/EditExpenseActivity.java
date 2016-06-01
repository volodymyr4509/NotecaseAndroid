package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.CategoryManager;
import com.domain.volodymyr.notecase.manager.CategoryManagerImpl;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.CategoryAdapter;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

import java.util.Calendar;
import java.util.List;

/**
 * Created by vkret on 02.12.15.
 */
public class EditExpenseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditExpenseActivity";

    private EditText name, price;
    private TextView date, time, categoryName, userName;
    private ImageView save, delete, navigationArrow, logo, categoryImage;

    private Product product;
    private Category category;
    private User user;

    private ProductManager productManager;
    private CategoryManager categoryManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        name = (EditText) findViewById(R.id.edit_expense_name);
        price = (EditText) findViewById(R.id.edit_expense_price);

        categoryName = (TextView) findViewById(R.id.edit_expense_category_name);
        userName = (TextView) findViewById(R.id.product_user);
        date = (TextView) findViewById(R.id.edit_expense_date);
        time = (TextView) findViewById(R.id.edit_expense_time);
        save = (ImageView) findViewById(R.id.action_item_right);
        delete = (ImageView) findViewById(R.id.action_item_left);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        categoryImage = (ImageView) findViewById(R.id.edit_expense_category_image);

        productManager = new ProductManagerImpl(getApplicationContext());
        categoryManager = new CategoryManagerImpl(getApplicationContext());
        userManager = new UserManagerImpl(getApplicationContext());

        save.setOnClickListener(this);
        categoryImage.setOnClickListener(this);
        delete.setVisibility(View.GONE);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        String productUuid = getIntent().getStringExtra(TabViewExpenses.PRODUCT_UUID_KEY);

        product = productManager.getProductByUuid(productUuid);
        category = categoryManager.getCategoryById(product.getCategoryId());
        user = userManager.getUserById(product.getUserId());

        super.onStart();
    }

    @Override
    protected void onResume() {
        name.setText(product.getName());
        price.setText(String.valueOf(product.getPrice()));
        Calendar calendar = Calendar.getInstance();
        if (product.getCreated() != null) {
            calendar.setTimeInMillis(product.getCreated().getTime());
            date.setText("" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
            time.setText("" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        }

        if (user != null) {
            userName.setText(user.getName());
        }

        setupCategoryBlock();

        name.setText(product.getName());
        price.setText(String.format("%.2f", product.getPrice()));
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item_right:
                try {
                    String productName = null;
                    double productPrice = 0;
                    if (price.getText().toString().length() > 0) {
                        try {
                            productPrice = Double.parseDouble(price.getText().toString().trim());
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Wrong price format", e);
                        }
                    }
                    if (name.getText().toString().trim().length() > 2) {
                        productName = name.getText().toString().trim();
                    }
                    if (productName == null || productPrice <= 0) {
                        Toast.makeText(this, "Wrong input", Toast.LENGTH_LONG).show();
                        break;
                    }

                    product.setName(productName);
                    product.setPrice(productPrice);
                    product.setCategoryId(category.getId());
                } catch (RuntimeException e) {
                    Log.e(TAG, "Wrong input", e);
                    Toast.makeText(getApplicationContext(), "Incorrect input", Toast.LENGTH_LONG).show();
                }
                new SafeAsyncTask<Void, Void, Void>(this) {
                    @Override
                    public Void doInBackgroundSafe() throws AuthenticationException {
                        productManager.updateProduct(product);
                        finish();
                        return null;
                    }
                }.execute();
                break;
            case R.id.edit_expense_category_image:
                showCategoryPopup();
                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                finish();
                break;

        }
    }

    private void showCategoryPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.category_grid, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        final GridView categoryGrid = (GridView) popupView.findViewById(R.id.category_grid);
        final List<Category> categoryList = categoryManager.getAllCategories();
        categoryGrid.setAdapter(new CategoryAdapter(getApplicationContext(), categoryList, true));

        categoryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (categoryList.size() > position && categoryList.get(position) != null) {
                    category = categoryList.get(position);
                    setupCategoryBlock();
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 300);
    }

    private void setupCategoryBlock(){
        categoryName.setText(category.getName());

        Drawable drawable = getResources().getDrawable(R.drawable.category_shape_medium);
        drawable.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);
        categoryImage.setBackground(drawable);
        categoryImage.setImageResource(category.getImage());
    }

}
