package com.expenses.volodymyr.notecase.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by volodymyr on 24.10.15.
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder {
    private static Drawable shadow;

    public MyDragShadowBuilder(View v) {
        super(v);
        shadow = new ColorDrawable(Color.RED);
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, getView().getWidth(), getView().getHeight());

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(getView().getWidth(), getView().getHeight());

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(getView().getWidth()/2, getView().getHeight()-20);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
