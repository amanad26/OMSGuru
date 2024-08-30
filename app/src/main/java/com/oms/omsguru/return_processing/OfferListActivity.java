package com.oms.omsguru.return_processing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ActivityOfferListBinding;

public class OfferListActivity extends AppCompatActivity {

    ActivityOfferListBinding binding;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfferListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;


        binding.icBack.setOnClickListener(v -> finish());
    }
}