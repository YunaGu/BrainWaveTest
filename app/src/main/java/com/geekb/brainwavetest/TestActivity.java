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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.gyf.immersionbar.ImmersionBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";
    private String[] wName = {"Attention/Meditation", "Delta", "Theta", "LowAlpha", "HighAlpha", "LowBeta", "HighBeta", "LowGamma", "MidGamma"};
    private WaveAdapter mWaveAdapter;
    private BarChart bcD;
    private BarChart bcT;
    private BarChart bcHighA;
    private BarChart bcHighB;
    private BarChart bcLowA;
    private BarChart bcLowB;
    private BarChart bcLowG;
    private BarChart bcMidG;
    private LineChart lcAtt;
    private Button mBegin;
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

    private int attention;
    private int count;
    private int meditation;
    private int sinal;
    private double del;
    private double highA;
    private double highB;
    private double lowA;
    private double lowB;
    private double lowG;
    private double midG;
    private double the;

    private String info;
    private boolean isPlaying = true;

    private MyReceiver receiver;

    private Handler startHandler = new Handler();
    private Runnable add = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                count++;
                if (sinal == 0) {
                    addDataEntry();
                } else {
                    meditation = 0;
                    attention = 0;
                    lowA = 0;
                    lowB = 0;
                    lowG = 0;
                    del = 0;
                    the = 0;
                    midG = 0;
                    highA = 0;
                    highB = 0;
                    addDataEntry();
                }
                att.add(attention);
                med.add(meditation);
                lowAlpha.add((int) lowA);
                lowBeta.add((int) lowB);
                lowGamma.add((int) lowG);
                highAlpha.add((int) highA);
                highBeta.add((int) highB);
                midGamma.add((int) midG);
                delta.add((int) del);
                theta.add((int) the);
            }
            startHandler.postDelayed(this, 1000);
        }
    };

    void addDataEntry() {
        addEntry(lcAtt, "Attention", Color.parseColor("#f7885c"),
                "Meditation", Color.parseColor("#33c0f6"),
                attention, meditation);
        addBarEntry(bcLowA, "LowAlpha", Color.parseColor("#c6d180"),
                lowA);
        addBarEntry(bcLowB, "LowBeta", Color.parseColor("#69d6d6"),
                lowB);
        addBarEntry(bcLowG, "LowGamma", Color.parseColor("#d568d8"),
                lowG);
        addBarEntry(bcD, "Delta", Color.parseColor("#ff5c6c"),
                del);
        addBarEntry(bcT, "Theta", Color.parseColor("#fdb74b"),
                the);
        addBarEntry(bcMidG, "MidGamma", Color.parseColor("#6831d3"),
                midG);
        addBarEntry(bcHighA, "HighAlpha", Color.parseColor("#72c469"),
                highA);
        addBarEntry(bcHighB, "HighBeta", Color.parseColor("#576ece"),
                highB);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor("#010101").init();
        setContentView(R.layout.activity_test);
        info = getIntent().getStringExtra("info");
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.geekb.brainwavetest.BlueService");
        registerReceiver(receiver, filter);
        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        startHandler.removeCallbacks(add);
    }

    void initView() {
        mBegin = findViewById(R.id.btn_begin);
        mFinish = findViewById(R.id.btn_finish);
        mSetting = findViewById(R.id.btn_setting);
        lcAtt = findViewById(R.id.lc_att);
        bcLowA = findViewById(R.id.bc_low_alpha);
        bcHighA = findViewById(R.id.bc_high_alpha);
        bcLowB = findViewById(R.id.bc_low_beta);
        bcHighB = findViewById(R.id.bc_high_beta);
        bcLowG = findViewById(R.id.bc_low_gamma);
        bcMidG = findViewById(R.id.bc_high_gamma);
        lvWave = findViewById(R.id.lv_wave);
        bcD = findViewById(R.id.bc_delta);
        bcT = findViewById(R.id.bc_theata);
        viewList = new View[]{
                lcAtt, bcD, bcT, bcLowA, bcHighA, bcLowB, bcHighB, bcLowG, bcMidG
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

        setLineChart(lcAtt);
        setBarChart(bcLowA);
        setBarChart(bcHighA);
        setBarChart(bcLowB);
        setBarChart(bcHighB);
        setBarChart(bcLowG);
        setBarChart(bcMidG);
        setBarChart(bcD);
        setBarChart(bcT);
        startHandler.postDelayed(add, 1000);
        mBegin.setOnClickListener(this);
        mFinish.setOnClickListener(this);
        mSetting.setOnClickListener(this);
    }

    private void addEntry(LineChart lineChart, String type1, int color1, String type2, int color2, int sdata1, int sdata2) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            LineDataSet set2 = (LineDataSet) data.getDataSetByIndex(1);
            if (set == null) {
                set = createSet(type1, color1);
                set2 = createSet(type2, color2);
                data.addDataSet(set);
                data.addDataSet(set2);
            }
            int min = count / 60;
            int s = count % 60;
            if (min < 10) {
                if (s < 10) {
                    data.addXValue("0" + min + ":" + "0" + s);
                } else {
                    data.addXValue("0" + min + ":" + s);
                }
            } else if (s < 10) {
                data.addXValue(min + ":" + "0" + s);
            } else {
                data.addXValue(min + ":" + s);
            }
            set.addEntry(new Entry(sdata1, set.getEntryCount()));
            set2.addEntry(new Entry(sdata2, set2.getEntryCount()));

            lineChart.setVisibleXRangeMaximum(25.0f);
            lineChart.moveViewToX(data.getXValCount() - 3);
            lineChart.notifyDataSetChanged();
        }
    }

    private void addBarEntry(BarChart barChart, String type, int color, double sdata) {
        BarData data = barChart.getData();
        if (data != null) {
            BarDataSet set = (BarDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = createBarSet(type, color);
                data.addDataSet(set);
            }
            int min = count / 60;
            int s = count % 60;
            if (min < 10) {
                if (s < 10) {
                    data.addXValue("0" + min + ":" + "0" + s);
                } else {
                    data.addXValue("0" + min + ":" + s);
                }
            } else if (s < 10) {
                data.addXValue(min + ":" + "0" + s);
            } else {
                data.addXValue(min + ":" + s);
            }
            set.addEntry(new BarEntry((float) sdata, set.getEntryCount()));

            barChart.setVisibleXRangeMaximum(25.0f);
            barChart.moveViewToX(data.getXValCount() - 3);
            barChart.notifyDataSetChanged();
        }
    }

    private LineDataSet createSet(String type, int color) {
        LineDataSet set = new LineDataSet(null, type);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(1.0f);
        set.setDrawCircles(true);
        set.setCircleRadius(2.0f);
        set.setHighlightEnabled(false);
        set.setDrawCubic(false);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setValueTextColor(-1);
        set.setValueTextSize(9.0f);
        set.setDrawValues(true);
        set.setValueFormatter(new MyValueFormatter());
        return set;
    }

    private BarDataSet createBarSet(String type, int color) {
        BarDataSet set = new BarDataSet(new ArrayList<>(), type);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setHighlightEnabled(false);
        set.setValueTextColor(-1);
        set.setValueTextSize(9.0f);
        set.setDrawValues(true);
        set.setValueFormatter(new MyValueFormatter());
        return set;
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

    void setLineChart(LineChart lineChart) {
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("");
        lineChart.setPinchZoom(true);
        LineData data = new LineData();
        data.setValueTextColor(-1);
        lineChart.setData(data);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(-1);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(-1);
        xl.setDrawGridLines(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelsToSkip(9);
        xl.setEnabled(true);
        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(-1);
        yl.setAxisMaxValue(100.0f);
        yl.setAxisMinValue(0.0f);
        yl.setStartAtZero(false);
        yl.setDrawGridLines(false);
        yl.setValueFormatter(new MyYValueFormatter());
        lineChart.getAxisRight().setEnabled(false);
        lineChart.notifyDataSetChanged();
    }

    void setBarChart(BarChart barChart) {
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setScaleYEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.setNoDataTextDescription("");
        barChart.setPinchZoom(true);
        BarData data = new BarData();
        data.setValueTextColor(-1);
        barChart.setData(data);
        Legend l = barChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(-1);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        XAxis xl = barChart.getXAxis();
        xl.setTextColor(-1);
        xl.setDrawGridLines(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelsToSkip(9);
        xl.setEnabled(true);
        YAxis yl = barChart.getAxisLeft();
        yl.setTextColor(-1);
        yl.setAxisMaxValue(20.0f);
        yl.setAxisMinValue(0.0f);
        yl.setStartAtZero(false);
        yl.setDrawGridLines(false);
        yl.setValueFormatter(new MyYValueFormatter());
        barChart.getAxisRight().setEnabled(false);
        barChart.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_begin:
                if (isPlaying) {
                    isPlaying = false;
                    mBegin.setBackgroundResource(R.drawable.test_begin);
                } else {
                    isPlaying = true;
                    mBegin.setBackgroundResource(R.drawable.test_stop);
                }
                break;
            case R.id.btn_finish:
                Intent intent = new Intent(this, ScoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", info);
                bundle.putIntegerArrayList("lowAlpha", lowAlpha);
                bundle.putIntegerArrayList("highAlpha", highAlpha);
                bundle.putIntegerArrayList("lowBeta", lowBeta);
                bundle.putIntegerArrayList("highBeta", highBeta);
                bundle.putIntegerArrayList("lowGamma", lowGamma);
                bundle.putIntegerArrayList("midGamma", midGamma);
                bundle.putIntegerArrayList("delta", delta);
                bundle.putIntegerArrayList("theta", theta);
                bundle.putIntegerArrayList("att", att);
                bundle.putIntegerArrayList("med", med);
                intent.putExtras(bundle);
                startActivity(intent);
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

   public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            attention = bundle.getInt("attention");
            meditation = bundle.getInt("meditation");
            lowA = bundle.getInt("lowAlpha");
            highA = bundle.getInt("highAlpha");
            lowB = bundle.getInt("lowBeta");
            highB = bundle.getInt("highBeta");
            lowG = bundle.getInt("lowGamma");
            midG = bundle.getInt("midGamma");
            del = bundle.getInt("delta");
            the = bundle.getInt("theta");
            sinal = bundle.getInt("sinal");
        }
    }
}
