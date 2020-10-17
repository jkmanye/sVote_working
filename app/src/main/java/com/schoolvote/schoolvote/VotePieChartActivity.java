package com.schoolvote.schoolvote;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class VotePieChartActivity extends AppCompatActivity {

    HashMap<String, Long> total;
    HashMap<String, String> lists;
    ArrayList<PieEntry> list = new ArrayList<>();
    PieChart pc_vp;
    TextView tv_vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_pie_chart);

        total = (HashMap<String, Long>) getIntent().getSerializableExtra("total");
        lists = (HashMap<String, String>) getIntent().getSerializableExtra("lists");

        tv_vp = findViewById(R.id.tv_vp);
        tv_vp.setText(getIntent().getStringExtra("title") + "의 집계 결과");

        pc_vp = findViewById(R.id.pc_vp);
        pc_vp.setUsePercentValues(true);
        pc_vp.getDescription().setEnabled(false);
        pc_vp.setExtraOffsets(5, 10, 5, 5);
        pc_vp.setDragDecelerationFrictionCoef(0.95f);

        pc_vp.setDrawHoleEnabled(false);
        pc_vp.setHoleColor(Color.WHITE);
        pc_vp.setTransparentCircleRadius(61f);

        for (long i = 0; i < total.size(); i++) {
            float val = (float) total.get(i);
            int a = (int) val;
            String title = "(" + Integer.toString(a) + "표) " + lists.get(Long.toString(i));
            list.add(new PieEntry(val, title));
        }

        pc_vp.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(list, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pc_vp.setData(data);
    }
}