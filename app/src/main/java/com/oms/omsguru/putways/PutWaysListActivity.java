package com.oms.omsguru.putways;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ActivityPutWaysListBinding;

public class PutWaysListActivity extends AppCompatActivity {

    ActivityPutWaysListBinding binding;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPutWaysListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;


        binding.icBack.setOnClickListener(v -> finish());


    }
}