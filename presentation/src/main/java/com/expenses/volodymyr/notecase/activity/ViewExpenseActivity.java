package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

/**
 * Created by volodymyr on 01.01.16.
 */
public class ViewExpenseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ViewExpenseActivity";
    private TextView name, price, dateTime, categoryName, userName;
    private ImageView categoryImage, navigationArrow, logo, editButton, deleteButton;
    private Product product;
    private ProductManager productManager;
    private CategoryManager categoryManager;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        name = (TextView) findViewById(R.id.view_expense_name);
        price = (TextView) findViewById(R.id.view_expense_price);
        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        dateTime = (TextView) findViewById(R.id.view_expense_datetime);
        categoryImage = (ImageView) findViewById(R.id.view_expense_category_image);
        categoryName = (TextView) findViewById(R.id.view_expense_category_name);
        userName = (TextView) findViewById(R.id.product_user);
        editButton = (ImageView) findViewById(R.id.action_item_right);
        deleteButton = (ImageView) findViewById(R.id.action_item_left);

        productManager = new ProductManagerImpl(getApplicationContext());
        categoryManager = new CategoryManagerImpl(getApplicationContext());
        userManager = new UserManagerImpl(getApplicationContext());

        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        String productId = getIntent().getStringExtra(TabViewExpenses.PRODUCT_UUID_KEY);
        product = productManager.getProductByUuid(productId);

        User owner = userManager.getUserOwner();
        if (owner != null && owner.getId() != product.getUserId()) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        User user = userManager.getUserById(product.getUserId());
        Category category = categoryManager.getCategoryById(product.getCategoryId());
        if (category == null) {
            return;
        }
        name.setText(product.getName());
        price.setText(String.valueOf(product.getPrice()));
        dateTime.setText(product.getCreated().toString());
        categoryName.setText(category.getName());
        if (user != null) {
            userName.setText(user.getName());
        }
        Drawable drawable = getResources().getDrawable(R.drawable.category_shape_medium);
        drawable.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);
        categoryImage.setBackground(drawable);
        categoryImage.setImageResource(category.getImage());
        editButton.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_item_right:
                Intent editExpense = new Intent(this, EditExpenseActivity.class);
                editExpense.putExtra(TabViewExpenses.PRODUCT_UUID_KEY, product.getUuid());
                Log.d(TAG, "Starting EditExpenseActivity, productUuid: " + product.getUuid());
                startActivity(editExpense);
                break;
            case R.id.action_item_left:
                new AlertDialog.Builder(ViewExpenseActivity.this)
                        .setTitle("Delete product")
                        .setMessage("Are you sure you want to deleteButton this product?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SafeAsyncTask<Product, Void, Boolean>(getApplicationContext()) {
                                    @Override
                                    public Boolean doInBackgroundSafe() throws AuthenticationException {
                                        return productManager.deleteProductByUuid(product.getUuid());
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean success) {
                                        finish();
                                    }
                                }.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.alert)
                        .show();
                break;
            case R.id.logo:
            case R.id.navigation_arrow:
                finish();
                break;
        }
    }
}
