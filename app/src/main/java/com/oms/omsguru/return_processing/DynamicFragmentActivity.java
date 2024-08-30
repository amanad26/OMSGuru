package com.oms.omsguru.return_processing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.oms.omsguru.R;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class DynamicFragmentActivity extends AppCompatActivity {

    private DynamicViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    String barcode;
    Session session;

    List<FetchDetailsModel.Result> list = new ArrayList<>();
    ProgressDialog pd;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_fragment);

        session = new Session(this);
        pd = new ProgressDialog(this);
        activity = this;

        viewPager = findViewById(R.id.viewpager);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        list = (List<FetchDetailsModel.Result>) getIntent().getSerializableExtra("product");
        barcode = getIntent().getStringExtra("tracker");
        viewPagerAdapter = new DynamicViewPagerAdapter(getSupportFragmentManager());


        if (list.size() != 0) {

            for (int i = 0; i < list.size(); i++) {
                viewPagerAdapter.add(new DynamicDataFragment(list.get(i), barcode), list.get(i).getSubOrderId(), barcode);
            }
        }

        // setting up the adapter
        viewPager.setAdapter(viewPagerAdapter);


    }
}