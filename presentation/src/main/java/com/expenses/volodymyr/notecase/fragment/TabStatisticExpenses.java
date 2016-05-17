package com.expenses.volodymyr.notecase.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.ProductManager;
import com.domain.volodymyr.notecase.manager.ProductManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vkret on 02.12.15.
 */
public class TabStatisticExpenses extends Fragment implements OnChartValueSelectedListener {
    private static String TAG = "TabStatisticExpenses";
    private boolean showCategoryName = false;
    private PieChart mChart;

    private ProductManager productManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Statistic fragment");

        View view = inflater.inflate(R.layout.tab_statistic_expenses, container, false);

        productManager = new ProductManagerImpl(getContext());

        Navigation navigation = new Navigation();
        getChildFragmentManager().beginTransaction().add(R.id.navigation_holder_stats, navigation, getString(R.string.stats_navigation_key)).commit();
        setMPChart(view);

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying Statistic tab");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming Statistic tab");

        updateStatistics();
        super.onResume();
    }

    public void updateStatistics() {
        Log.i(TAG, "Set up data for Statistic tab");

        final Timestamp till = new Timestamp(Navigation.end);
        final Timestamp since = new Timestamp(Navigation.begin);

        new SafeAsyncTask<Timestamp, Void, Map<Category, Double>>(getContext()) {
            @Override
            public Map<Category, Double> doInBackgroundSafe() throws AuthenticationException {
                return productManager.getExpensesGroupedByCategories(since, till);
            }

            @Override
            protected void onPostExecute(Map<Category, Double> categoryDoubleMap) {
                ArrayList<String> xVals = new ArrayList<>();
                List<Entry> yVals = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();

                double sum = 0;
                if (categoryDoubleMap == null || categoryDoubleMap.values() == null) {
                    return;
                }
                Iterator<Double> it = categoryDoubleMap.values().iterator();
                while (it.hasNext()) {
                    sum += it.next();
                }

                Set<Category> categorySet = categoryDoubleMap.keySet();
                Iterator<Category> catIt = categorySet.iterator();
                int i = 0;
                while (catIt.hasNext()) {
                    Category category = catIt.next();
                    xVals.add(category.getName());
                    double percentage = categoryDoubleMap.get(category) / sum;
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
        }.execute();

    }

    private void setMPChart(View view) {
        mChart = (PieChart) view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription(getString(R.string.empty_string));
//        mChart.setDescription("Expense statistic pie chart");
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawSliceText(showCategoryName);
        mChart.setDrawHoleEnabled(false);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(true);
        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
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
