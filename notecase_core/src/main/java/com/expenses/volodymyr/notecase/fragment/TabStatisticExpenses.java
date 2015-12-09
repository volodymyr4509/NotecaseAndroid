package com.expenses.volodymyr.notecase.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.util.DBHandler;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vkret on 02.12.15.
 */
public class TabStatisticExpenses extends Fragment implements OnChartValueSelectedListener {
    private boolean showCategoryName = false;
    private PieChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_statistic_expenses, container, false);

        mChart = (PieChart) view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("Expense statistic pie chart");
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawSliceText(showCategoryName);
        mChart.setDrawHoleEnabled(false);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData();
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        return view;
    }

    private void setData() {
        DBHandler dbHandler = DBHandler.getDbHandler(getActivity());
        Map<Category, Double> groupedByCategories = dbHandler.getExpensesGroupedByCategories();

        ArrayList<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        double sum = 0;
        Iterator<Double> it = groupedByCategories.values().iterator();
        while (it.hasNext()) {
            sum += it.next();
        }

        Set<Category> categorySet = groupedByCategories.keySet();
        Iterator<Category> catIt = categorySet.iterator();
        int i = 0;
        while (catIt.hasNext()) {
            Category category = catIt.next();
            xVals.add(category.getName());
            double percentage = groupedByCategories.get(category) / sum;
            yVals.add(new Entry((float) percentage, i));
            colors.add(category.getColor());
            i++;
        }

        PieDataSet dataSet = new PieDataSet(yVals, "Categories");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        showCategoryName = !showCategoryName;
        mChart.setDrawSliceText(showCategoryName);
    }

    @Override
    public void onNothingSelected() {

    }
}
