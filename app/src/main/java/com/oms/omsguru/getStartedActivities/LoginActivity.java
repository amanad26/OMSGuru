package com.oms.omsguru.getStartedActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.ActivityLoginBinding;
import com.oms.omsguru.models.VerifyOtpModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Activity activity;
    ActivityLoginBinding binding;
    ProgressDialog pd;
    boolean isVisible = false;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);
        session = new Session(activity);

        binding.btnSignIn.setOnClickListener(v -> {
            if (isValidate()) Login();

        });

        UtilsClass.textBackgroundChange(binding.emailEdit, binding.emailLinear);
        UtilsClass.textBackgroundChange(binding.passwordEdit, binding.passwordLinear);

        binding.forgetPassword.setOnClickListener(v -> startActivity(new Intent(activity, ForgetPasswordActivity.class)));

        binding.eyeBtn.setOnClickListener(v -> {
            if (isVisible)
                hidePassword(binding.passwordEdit);
            else showPassword(binding.passwordEdit);
        });

    }

    private void showPassword(EditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        binding.eyeBtn.setImageResource(R.drawable.baseline_visibility_eye);
        isVisible = true;
    }


    private void hidePassword(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.eyeBtn.setImageResource(R.drawable.baseline_visibility_off_24);
        isVisible = false;
    }


    private boolean isValidate() {

        if (binding.emailEdit.getText().toString().equalsIgnoreCase("")) {
            binding.emailEdit.setError("Enter Email!");
            binding.emailEdit.requestFocus();
            binding.emailLinear.setBackgroundResource(R.drawable.edit_error_background);
            return false;
        } else if (binding.passwordEdit.getText().toString().equalsIgnoreCase("")) {
            binding.passwordEdit.setError("Enter Password!");
            binding.passwordEdit.requestFocus();
            binding.passwordLinear.setBackgroundResource(R.drawable.edit_error_background);
            return false;
        } else {
            binding.emailLinear.setBackgroundResource(R.drawable.edit_backgound);
            binding.passwordLinear.setBackgroundResource(R.drawable.edit_backgound);
            return true;

        }
    }


    private void Login() {
        pd.show();

        RetrofitClient.getClient(activity).login(
                "login_user",
                binding.emailEdit.getText().toString(),
                binding.passwordEdit.getText().toString()
        ).enqueue(new Callback<VerifyOtpModel>() {
            @Override
            public void onResponse(@NonNull Call<VerifyOtpModel> call, @NonNull Response<VerifyOtpModel> response) {
                pd.dismiss();
                try {
                    if (response.code() == 200)
                        if (response.body() != null)
                            if (response.body().getCode() == 0 && response.body().isSuccess()) {
                                if (response.body().getResults() != null) {
                                    session.saveVerifyModel(response.body().getResults());
                                    session.setLogin(true);
                                    session.setUserId(response.body().getResults().getId());
                                    session.setAuth_code(response.body().getResults().getAuthCode());
                                    session.setPermission_code(String.valueOf(response.body().getResults().getLastPermissionUpdate()));
                                    session.setEmail(response.body().getResults().getEmail());

                                    try {
                                        if (response.body().getResults().getName() != null)
                                            session.setName(response.body().getResults().getName());

                                        if (response.body().getResults().getMobile() != null)
                                            session.setMobile(response.body().getResults().getMobile());
                                    } catch (Exception e) {
                                        Log.e("TAG", "onResponse: "+e.getMessage() );
                                    }


                                    successAlert();
                                }
                            } else if (response.body().getCode() == 1 && response.body().isSuccess()) {
                                Toast.makeText(activity, "OTP is sent to " + binding.emailEdit.getText().toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(activity, VerifyOtpActivity.class)
                                        .putExtra("email", binding.emailEdit.getText().toString())
                                        .putExtra("password", binding.passwordEdit.getText().toString())
                                );
                                finish();
                            } else if (response.body().getCode() == -2 && !response.body().isSuccess() && response.body().getMessage().equalsIgnoreCase("We have already sent you an OTP email, please login using that OTP")) {
                                Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(activity, VerifyOtpActivity.class)
                                        .putExtra("email", binding.emailEdit.getText().toString())
                                        .putExtra("password", binding.passwordEdit.getText().toString())
                                );
                                finish();
                            } else {
                                failAlert(response.body().getMessage());
                            }
                } catch (Exception e) {
                    Toast.makeText(activity, "Server not respond!", Toast.LENGTH_SHORT).show();
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

    private void failAlert(String message) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Login Failed").setContentText(message).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("OK", Dialog::dismiss)
                .show();
    }

}