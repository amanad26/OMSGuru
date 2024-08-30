package com.oms.omsguru.getStartedActivities;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.oms.omsguru.MainActivity;
import com.oms.omsguru.MainActivity2;
import com.oms.omsguru.databinding.ActivitySplashBinding;
import com.oms.omsguru.return_processing.UploadFileActivity;
import com.oms.omsguru.return_processing.VideoRecordActivity;
import com.oms.omsguru.session.Session;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    Activity activity;
    ActivitySplashBinding binding;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);

        startApp();

    }

    private void startApp() {
        new Thread(() -> {
            try {
                sleep(2000);

                if (session.isLoggedIn()) {
                    Intent i = new Intent(activity, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(activity, LoginActivity.class);
                    startActivity(i);
                    finish();
                }


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}