package com.expenses.volodymyr.notecase.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;


/**
 * Created by volodymyr on 15.11.15.
 */
public class OnDragDropListener implements View.OnDragListener {
    private static final String TAG = "OnDragDropListener";

    private EditText name;
    private EditText price;
    private int categoryId;
    private Context context;
    private static Snackbar snack;
    private static boolean snackDismissed = true;

    private ProductManager productManager;
    private UserManager userManager;

    public OnDragDropListener(EditText name, EditText price, int category, Context context) {
        this.name = name;
        this.price = price;
        this.categoryId = category;
        this.context = context;
        this.productManager = new ProductManagerImpl(context);
        this.userManager = new UserManagerImpl(context);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();

        if (v instanceof ImageView && action == DragEvent.ACTION_DRAG_LOCATION) {
            if (v.getAnimation() == null) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale));

                //TODO refactor
                if (snack == null){
                    snack = Snackbar.make(v, v.getTag(R.string.category_name_tag).toString(), Snackbar.LENGTH_SHORT);
                    snack.setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            snackDismissed = true;
                            super.onDismissed(snackbar, event);
                        }
                    });
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
                    params.gravity = Gravity.TOP;
                    snack.getView().setLayoutParams(params);
                }

                snack.setText(v.getTag(R.string.category_name_tag).toString());

                if (snackDismissed){
                    snack.show();
                    snackDismissed = false;
                }
            }
        }
        switch (action) {
            case DragEvent.ACTION_DROP:
                String productName = null;
                double productPrice = 0;
                if (price.getText().toString().length() > 0) {
                    try {
                        productPrice = Double.parseDouble(price.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                if (name.getText().toString().trim().length() > 2) {
                    productName = name.getText().toString().trim();
                }
                if (productName == null || productPrice <= 0) {
                    Toast.makeText(context, "Wrong input", Toast.LENGTH_LONG).show();
                    return false;
                }

                User owner = userManager.getUserOwner();
                int userId = 0;
                if (owner != null) {
                    userId = owner.getId();
                }
                final Product product = new Product(categoryId, userId, productName, productPrice);

                new SafeAsyncTask<Product, Void, Boolean>(context) {
                    @Override
                    public Boolean doInBackgroundSafe() throws AuthenticationException {
                        return productManager.addProduct(product);
                    }
                }.execute();
                //clean input fields after save
                name.setText(R.string.empty_string);
                price.setText(R.string.empty_string);
        }
        return true;
    }

    public EditText getName() {
        return name;
    }

    public void setName(EditText name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "OnDragDropListener{" +
                "name=" + name +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", context=" + context +
                '}';
    }
}
