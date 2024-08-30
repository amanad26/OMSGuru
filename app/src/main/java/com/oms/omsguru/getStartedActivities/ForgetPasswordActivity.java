package com.oms.omsguru.getStartedActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.ActivityForgetPasswordBinding;
import com.oms.omsguru.models.ForgetPasswordModel;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    Activity activity;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        UtilsClass.textBackgroundChange(binding.emailEdit, binding.emailLinear);

        binding.btnSignIn.setOnClickListener(v -> {
            if (binding.emailEdit.getText().toString().equalsIgnoreCase("")) {
                binding.emailEdit.setError("Enter Email!");
                binding.emailEdit.requestFocus();
                binding.emailLinear.setBackgroundResource(R.drawable.edit_error_background);
            } else {
                forgetPassword();
            }
        });

        binding.backBtn.setOnClickListener(v -> finish());

    }

    private void forgetPassword() {
        pd.show();
        RetrofitClient.getClient(activity).forgetPassword("forgot_password", binding.emailEdit.getText().toString()).enqueue(new Callback<ForgetPasswordModel>() {
            @Override
            public void onResponse(@NonNull Call<ForgetPasswordModel> call, @NonNull Response<ForgetPasswordModel> response) {
                pd.dismiss();
                if (response.code() == 200) if (response.body() != null) {
                    Toast.makeText(activity, Html.fromHtml(response.body().getMessage()), Toast.LENGTH_SHORT).show();
                    finish();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgetPasswordModel> call, @NonNull Throwable t) {
                pd.dismiss();
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
            }
        });


    }


}