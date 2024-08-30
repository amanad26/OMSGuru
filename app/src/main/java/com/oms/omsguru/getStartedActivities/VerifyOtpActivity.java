package com.oms.omsguru.getStartedActivities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.ActivityVerifyOtpBinding;
import com.oms.omsguru.models.VerifyOtpModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    ActivityVerifyOtpBinding binding;
    Activity activity;
    String email, password;
    ProgressDialog pd;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);

        pd = new ProgressDialog(activity);

        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
        }

        binding.email.setText(getString(R.string.we_have_sent_an_otp_to_your_email) + email + getString(R.string.please_enter_and_verify_otp));

        binding.pinview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.pinview.setLineColor(getResources().getColor(R.color.app_common_color));

                if (s.length() == 7) {
                    hideKeyboardFrom(activity, binding.pinview);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSignIn.setOnClickListener(v -> {
            if (!binding.pinview.getText().toString().equalsIgnoreCase("")) {
                verifyOtp(binding.pinview.getText().toString());
            } else {
                binding.pinview.setLineColor(getResources().getColor(R.color.red));
                binding.pinview.requestFocus();
                Toast.makeText(activity, "Enter OTP!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void verifyOtp(String otp) {
        pd.show();
        RetrofitClient.getClient(activity).verifyOtp(
                "login_user",
                email,
                password,
                otp
        ).enqueue(new Callback<VerifyOtpModel>() {
            @Override
            public void onResponse(@NonNull Call<VerifyOtpModel> call, @NonNull Response<VerifyOtpModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null) {
                        if (response.body().isSuccess()) {
                            if (response.body().getCode() != -1) {
                                Toast.makeText(activity, "Verified..!", Toast.LENGTH_SHORT).show();
                                session.saveVerifyModel(response.body().getResults());
                                session.setLogin(true);
                                session.setUserId(response.body().getResults().getId());
                                session.setEmail(response.body().getResults().getEmail());
                                session.setAuth_code(response.body().getResults().getAuthCode());
                                session.setPermission_code(String.valueOf(response.body().getResults().getLastPermissionUpdate()));

                                try {
                                    if (response.body().getResults().getName() != null)
                                        session.setName(response.body().getResults().getName());

                                    if (response.body().getResults().getMobile() != null)
                                        session.setMobile(response.body().getResults().getMobile());
                                } catch (Exception e) {
                                    Log.e("TAG", "onResponse: " + e.getMessage());
                                }


                                successAlert();
                            } else {
                                Toast.makeText(activity, "Verification Failed..", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(activity, "Verification Failed..", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    } else {
                        failAlert();
                    }
            }

            @Override
            public void onFailure(@NonNull Call<VerifyOtpModel> call, @NonNull Throwable t) {
                pd.dismiss();
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void successAlert() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.SUCCESS);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Login Success").setContentText("Now , you can select warehouse").setCancellable(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    startActivity(new Intent(activity, SelectWherehouseTypeActivity.class));
                    finish();
                })
                .show();
    }


    private void failAlert() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Login Failed").setContentText("Please login again").setCancellable(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    startActivity(new Intent(activity, LoginActivity.class));
                    finish();
                })
                .show();
    }
}