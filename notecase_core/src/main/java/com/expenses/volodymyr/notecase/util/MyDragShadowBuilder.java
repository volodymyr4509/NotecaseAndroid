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

    public MyDragShadowBuilder(View v){
        super(v);
        shadow = new ColorDrawable(Color.RED);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        int width, height;
        width = getView().getWidth()/2;
        height = getView().getHeight()/2;

        shadow.setBounds(0,0,width,height);
        shadowSize.set(width, height);
        shadowTouchPoint.set(width/2, height/2);
        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
