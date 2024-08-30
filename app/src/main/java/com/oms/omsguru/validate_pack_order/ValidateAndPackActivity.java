package com.oms.omsguru.validate_pack_order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.adapters.SkuListAdapter;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.ActivityValidateAndPackBinding;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.SkuInterface;
import com.oms.omsguru.utils.UtilsClass;
import com.oms.omsguru.utils.ValidateInterface;
import com.oms.omsguru.validate_pack_order.SkuCheck.SkuOrderPageChangeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidateAndPackActivity extends AppCompatActivity implements ProductListInterface, ValidateInterface, SkuInterface {

    ActivityValidateAndPackBinding binding;
    Activity activity;
    String items[] = {"Scan Bar Code", "Manual Enter"};
    GetOrderModel.Results results = null;
    List<GetOrderModel.Results.Sku> skus = new ArrayList<>();
    boolean isDataPartVisible = true;
    boolean isAllComplete = true;
    boolean isFirst = false;
    SkuListAdapter skuListAdapter = null;
    ProgressDialog pd;
    Session session;
    private GetOrderModel.Results results2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityValidateAndPackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);
        pd = new ProgressDialog(activity);

        results = (GetOrderModel.Results) getIntent().getSerializableExtra("product");

        results2 = results;

        if (results != null) {
            skus = results.getSku();

            binding.recyler.setLayoutManager(new LinearLayoutManager(activity));
            skuListAdapter = new SkuListAdapter(activity, skus, this);
            binding.recyler.setAdapter(skuListAdapter);

            SkuOrderPageChangeAdapter adapter = new SkuOrderPageChangeAdapter(ValidateAndPackActivity.this, activity, this, results.getSku(), this);
            binding.viewPager.setAdapter(adapter);

            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(items[position])).attach();
        }

        binding.icBack.setOnClickListener(v -> finish());

        binding.continueBtn.setOnClickListener(v -> {
            if (isFirst) {
                if (isAllComplete) {
                    warningAlert();
                } else {
                    Toast.makeText(activity, "Scan all sku codes first!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Scan all sku codes first!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.resetBtn.setOnClickListener(v -> {
            for (int i = 0; i < skus.size(); i++) {
                skus.get(i).setScanned(false);
            }

            activity.finish();
            activity.startActivity(activity.getIntent().putExtra("product", (Serializable) results));

        });


        binding.scanBtn.setOnClickListener(v -> {
            if (isDataPartVisible) {
                binding.linearData.setVisibility(View.GONE);
                binding.linearFragment.setVisibility(View.VISIBLE);
                isDataPartVisible = false;
            }

        });

        ///end on Create

    }

    private void savePackLog() {
        pd.show();
        RetrofitClient.getClient(activity).savePackLoag("save_pack_log", session.getWarehouseId(), session.getCompanyId(), results.getInvoice().getId(), UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<GetOrderModel>() {
            @Override
            public void onResponse(Call<GetOrderModel> call, Response<GetOrderModel> response) {
                pd.dismiss();
                if (response.code() == 200) if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        successAlert(response.body().getMessage());
                    } else {
                        failAlert(response.body().getMessage(), String.valueOf(response.body().getCode()));
                    }


                }


            }

            @Override
            public void onFailure(Call<GetOrderModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(activity, "Server Not Responding!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onAdded(ProductListModel model, String code, String message) {

    }

    private void failAlert(String mes, String code) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Failed with code : " + code).setContentText("Message: " + mes).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                    ValidatePackBarcodeScanFragment.isFailed = true;
                    ValidatePackBarcodeScanFragment.message = mes;
                    ValidatePackBarcodeMannualFragment.isFailed2 = true;
                    ValidatePackBarcodeMannualFragment.message2 = mes;
                }).show();
    }


    private void warningAlert() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Alert ").setContentText("Do you want to save pack logs ?").setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                    savePackLog();
                }).setNeutralButton("No", new StylishAlertDialog.OnStylishClickListener() {
                    @Override
                    public void onClick(StylishAlertDialog StylishAlertDialog) {
                        StylishAlertDialog.dismiss();
                    }
                }).show();
    }


    private void successAlert(String message) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.SUCCESS);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Success ").setContentText(message).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                    ValidatePackBarcodeScanFragment.isCompleted = true;
                    ValidatePackBarcodeMannualFragment.isCompleted2 = true;
                    finish();
                }).show();
    }

    private void successAlert() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.SUCCESS);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Success ").setContentText("SuccessFully Scanned").setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                }).show();
    }

    @Override
    public void onBackPressed() {

        if (!isDataPartVisible) {
            binding.linearData.setVisibility(View.VISIBLE);
            binding.linearFragment.setVisibility(View.GONE);
            isDataPartVisible = true;
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public void onAdded(int pos) {
        if (skuListAdapter != null) {
            Log.e("TAG", "onAdded: Sku " + skus.get(pos).toString());

            if (!skus.get(pos).isScanned()) {
                isFirst = true;
                skus.get(pos).setScanned(true);
                skuListAdapter.notifyItemChanged(pos);
                binding.linearData.setVisibility(View.VISIBLE);
                binding.linearFragment.setVisibility(View.GONE);
                isDataPartVisible = true;

                for (int i = 0; i < skus.size(); i++) {
                    if (!skus.get(i).isScanned()) isAllComplete = false;
                }

                if (isAllComplete) {
                    binding.continueBtn.setBackgroundResource(R.color.green);
                } else {
                    binding.continueBtn.setBackgroundResource(R.color.background_gray);
                }

                successAlert();
            } else {
                failAlert();
            }

        }

    }


    private void failAlert() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Failed").setContentText("Already Scanned").setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                }).show();
    }

    @Override
    public void onChange(boolean val, int pos) {

    }
}