package com.oms.omsguru.getStartedActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ActivityProfileBinding;
import com.oms.omsguru.session.Session;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    Activity activity;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);

        try {

            if (!session.getName().equalsIgnoreCase("")) binding.name.setText(session.getName());
            else binding.name.setText("No Name Found");

            if (!session.getEmail().equalsIgnoreCase("")) binding.email.setText(session.getEmail());
            else binding.email.setText("No Email Found");


            if (!session.getMobile().equalsIgnoreCase(""))
                binding.mobile.setText(session.getMobile());
            else binding.mobile.setText("No Mobile Found");


        } catch (Exception e) {
            Log.e("TAG", "onResponse: " + e.getMessage());
        }


        binding.icBack.setOnClickListener(v -> finish());

    }
}