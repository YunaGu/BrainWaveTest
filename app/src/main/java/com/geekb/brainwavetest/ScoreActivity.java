package com.geekb.brainwavetest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.gyf.immersionbar.ImmersionBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";
    private LineData dLineData;
    private LineData tLineData;
    private LineData attLineData;
    private LineData highALineData;
    private LineData highBLineData;
    private LineData lowALineData;
    private LineData lowBLineData;
    private LineData lowgLineData;
    private LineData midGLineData;
    private String[] wName = {"Attention/Meditation", "Delta", "Theta", "LowAlpha", "HighAlpha", "LowBeta", "HighBeta", "LowGamma", "MidGamma"};
    private WaveAdapter mWaveAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private LineChart lcD;
    private LineChart lcT;
    private LineChart lcHighA;
    private LineChart lcHighB;
    private LineChart lcLowA;
    private LineChart lcLowB;
    private LineChart lcLowG;
    private LineChart lcMidG;
    private LineChart lcAtt;
    private Button mFinish;
    private Button mSetting;
    private ListView lvWave;

    private ArrayList<Integer> att = new ArrayList<>();
    private ArrayList<Integer> delta = new ArrayList<>();
    private ArrayList<Integer> highAlpha = new ArrayList<>();
    private ArrayList<Integer> highBeta = new ArrayList<>();
    private ArrayList<Integer> lowAlpha = new ArrayList<>();
    private ArrayList<Integer> lowBeta = new ArrayList<>();
    private ArrayList<Integer> lowGamma = new ArrayList<>();
    private ArrayList<Integer> med = new ArrayList<>();
    private ArrayList<Integer> midGamma = new ArrayList<>();
    private ArrayList<Integer> theta = new ArrayList<>();

    private List<Map<String, Object>> mData;
    private View[] viewList = new View[9];

    private String info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor("#010101").init();
        setContentView(R.layout.activity_score);
        initView();

        initData();

    }

    void initData() {
        Bundle bundle = getIntent().getExtras();
        lowAlpha = bundle.getIntegerArrayList("lowAlpha");
        highAlpha = bundle.getIntegerArrayList("highAlpha");
        lowBeta = bundle.getIntegerArrayList("lowBeta");
        highBeta = bundle.getIntegerArrayList("highBeta");
        lowGamma = bundle.getIntegerArrayList("lowGamma");
        midGamma = bundle.getIntegerArrayList("midGamma");
        delta = bundle.getIntegerArrayList("delta");
        theta = bundle.getIntegerArrayList("theta");
        att = bundle.getIntegerArrayList("att");
        med = bundle.getIntegerArrayList("med");
        info = bundle.getString("info");

        attLineData = getLineData(att,med,info,Color.parseColor("#f7885c"),Color.parseColor("#33c0f6"));
        highALineData = getLineData(highAlpha,info + " HighAlpha",Color.parseColor("#72c469"));
        lowALineData = getLineData(lowAlpha,info + " LowAlpha",Color.parseColor("#c6d180"));
        highBLineData = getLineData(highBeta,info + " HighBeta",Color.parseColor("#69d6d6"));
        lowBLineData = getLineData(lowBeta,info + " LowBeta",Color.parseColor("#576ece"));
        lowgLineData = getLineData(lowGamma,info + " LowGamma",Color.parseColor("#d568d8"));
        midGLineData = getLineData(midGamma,info + " MidGamma",Color.parseColor("#6831d3"));
        dLineData = getLineData(delta,info + " Delta",Color.parseColor("#ff5c6c"));
        tLineData = getLineData(theta,info + " Theta",Color.parseColor("#fdb74b"));
        showChart(lcAtt,attLineData,100);
        showChart(lcLowA,lowALineData,20);
        showChart(lcHighA,highALineData,20);
        showChart(lcLowB,lowBLineData,20);
        showChart(lcHighB,highBLineData,20);
        showChart(lcLowG,lowgLineData,20);
        showChart(lcMidG,midGLineData,20);
        showChart(lcD,dLineData,20);
        showChart(lcT,tLineData,20);

    }

    void initView() {
        mFinish = findViewById(R.id.btn_finish);
        mSetting = findViewById(R.id.btn_setting);
        lcAtt = findViewById(R.id.lc_att);
        lcLowA = findViewById(R.id.lc_low_alpha);
        lcHighA = findViewById(R.id.lc_high_alpha);
        lcLowB = findViewById(R.id.lc_low_beta);
        lcHighB = findViewById(R.id.lc_high_beta);
        lcLowG = findViewById(R.id.lc_low_gamma);
        lcMidG = findViewById(R.id.lc_high_gamma);
        lvWave = findViewById(R.id.lv_wave);
        lcD = findViewById(R.id.lc_delta);
        lcT = findViewById(R.id.lc_theata);
        viewList = new View[]{
                lcAtt, lcD, lcT, lcLowA, lcHighA, lcLowG, lcHighB, lcLowG, lcMidG
        };
        mData = getData();
        mWaveAdapter = new WaveAdapter(this, mData);
        lvWave.setAdapter(mWaveAdapter);
        lvWave.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (viewList[position].getVisibility() == View.VISIBLE) {
                    viewList[position].setVisibility(View.GONE);
                } else {
                    viewList[position].setVisibility(View.VISIBLE);
                }
                mWaveAdapter.changeSelected(position);
            }
        });
        mFinish.setOnClickListener(this);
        mSetting.setOnClickListener(this);
    }

    void showChart(LineChart lineChart, LineData lineData, int count) {
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("");
        lineChart.setPinchZoom(true);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(Color.parseColor("#20caff"));
        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.parseColor("#20caff"));
        xl.setDrawGridLines(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setEnabled(true);
        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.parseColor("#20caff"));
        yl.setAxisMaxValue(count);
        yl.setAxisMinValue(0.0f);
        yl.setStartAtZero(false);
        yl.setDrawGridLines(false);
        yl.setValueFormatter(new TestActivity.MyYValueFormatter());
        lineChart.getAxisRight().setEnabled(false);
        lineChart.zoom(lineData.getXValCount() / 30.0f, 1.0f, 0.0f, 0.0f);
        lineChart.setData(lineData);
        Legend mLegend = lineChart.getLegend();
        mLegend.setForm(Legend.LegendForm.SQUARE);
        mLegend.setFormSize(9.0f);
        mLegend.setTextColor(Color.parseColor("#4c4c4c"));
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        lineChart.animateX(0);
    }

    LineData getLineData(ArrayList arrayList, String type, int color) {
        ArrayList<String> xValues = new ArrayList<>();
        int count = arrayList.size();
        for (int i = 0; i < count; i++) {
            if (i / 60 > 9) {
                if (i % 60 > 9) {
                    xValues.add((i / 60) + ":" + (i % 60));
                } else {
                    xValues.add((i / 60) + ":" + "0" + (i % 60));
                }
            } else if (i % 60 > 9) {
                xValues.add("0" + (i / 60) + ":" + (i % 60));
            } else {
                xValues.add("0" + (i / 60) + ":" + "0" + (i % 60));
            }
        }
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i2 = 0; i2 < count; i2++) {
            if (((Integer) arrayList.get(i2)) != 0) {
                yValues.add(new Entry(((Integer) arrayList.get(i2)), i2));
            } else {
                yValues.add(new Entry(0.0f, i2));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(yValues, type);
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(1.0f);
        lineDataSet.setCircleRadius(2.0f);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawCubic(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setValueTextColor(-1);
        lineDataSet.setValueTextSize(9.0f);
        lineDataSet.setDrawValues(true);
        LineData lineData = new LineData(xValues, lineDataSet);
        lineData.setValueFormatter(new MyValueFormatter());
        return lineData;
    }

    LineData getLineData(ArrayList atten, ArrayList medi, String type, int color, int color2) {
        ArrayList<String> xValues = new ArrayList<>();
        int count = atten.size();
        for (int i = 0; i < count; i++) {
            if (i / 60 > 9) {
                if (i % 60 > 9) {
                    xValues.add((i / 60) + ":" + (i % 60));
                } else {
                    xValues.add((i / 60) + ":" + "0" + (i % 60));
                }
            } else if (i % 60 > 9) {
                xValues.add("0" + (i / 60) + ":" + (i % 60));
            } else {
                xValues.add("0" + (i / 60) + ":" + "0" + (i % 60));
            }
        }
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i2 = 0; i2 < count; i2++) {
            yValues.add(new Entry(((Integer) atten.get(i2)), i2));
        }
        LineDataSet lineDataSet = new LineDataSet(yValues, type + " Attention");
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(1.0f);
        lineDataSet.setCircleRadius(2.0f);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawCubic(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setValueTextColor(-1);
        lineDataSet.setValueTextSize(9.0f);
        lineDataSet.setDrawValues(true);
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet);

        ArrayList<Entry> yValues2 = new ArrayList<>();
        for (int i3 = 0; i3 < count; i3++) {
            yValues2.add(new Entry(((Integer) medi.get(i3)), i3));
        }
        LineDataSet lineDataSet2 = new LineDataSet(yValues, type + " Meditation");
        lineDataSet.setColor(color2);
        lineDataSet.setLineWidth(1.0f);
        lineDataSet.setCircleRadius(2.0f);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawCubic(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setValueTextColor(-1);
        lineDataSet.setValueTextSize(9.0f);
        lineDataSet.setDrawValues(true);
        lineDataSets.add(lineDataSet2);

        LineData lineData = new LineData(xValues, lineDataSets);
        lineData.setValueFormatter(new MyValueFormatter());
        return lineData;
    }

    List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String put : wName) {
            Map<String, Object> map = new HashMap<>();
            map.put("textView", put);
            list.add(map);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                finish();
                break;
            case R.id.btn_setting:
                lvSetAnim();
                break;
        }
    }

    void lvSetAnim() {
        if (lvWave.getVisibility() == View.VISIBLE) {
            ObjectAnimator up = ObjectAnimator.ofPropertyValuesHolder(lvWave, new PropertyValuesHolder[]{
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{0.0f, lvWave.getHeight()})
            });
            up.setDuration(200);
            up.start();
            up.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvWave.setVisibility(View.GONE);
                }
            });
        } else {
            lvWave.setVisibility(View.VISIBLE);
            ObjectAnimator down = ObjectAnimator.ofPropertyValuesHolder(lvWave, new PropertyValuesHolder[]{
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{lvWave.getHeight(), 0.0f})
            });
            down.setDuration(200);
            down.setInterpolator(new AccelerateDecelerateInterpolator());
            down.start();

        }
    }


    static class MyValueFormatter implements ValueFormatter {
        private DecimalFormat format = new DecimalFormat("###,###,##0");

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return format.format(value);
        }
    }

    static class MyYValueFormatter implements YAxisValueFormatter {
        private DecimalFormat format = new DecimalFormat("###,###,##0.#");


        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return format.format(value);
        }
    }
}
