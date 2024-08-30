package com.oms.omsguru.dispatch;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.MainActivity;
import com.oms.omsguru.R;
import com.oms.omsguru.adapters.DisptchProductListAdapter;
import com.oms.omsguru.adapters.ShipedProductListAdapter;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentDispatchBinding;
import com.oms.omsguru.models.DispatchListModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ViewAllProductActivity;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.ships.ShipedProductListActivity;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchFragment extends Fragment implements ProductListInterface {

    FragmentDispatchBinding binding;
    Activity activity;
    ShipedProductListAdapter pAdapter;
    String items[] = {"Scan Bar Code", "Manual Enter"};
    Session session;
    ArrayList<ProductListModel> productList = new ArrayList<>();
    private List<DispatchListModel.Result> data = null;
    private SoundPool soundPool;
    private SoundPool soundPool2;
    private Handler handler;
    private Handler handler2;
    private int soundId;
    private int soundId2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDispatchBinding.inflate(getLayoutInflater());
        activity = requireActivity();
        session = new Session(activity);

        DispatchPageChangeAdapter adapter = new DispatchPageChangeAdapter(requireActivity(), activity, this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(items[position]);
            }
        }).attach();

        pAdapter = new ShipedProductListAdapter(activity, productList, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.productRecycler.setLayoutManager(linearLayoutManager);
        binding.productRecycler.setAdapter(pAdapter);

        binding.viewScannedProduct.setOnClickListener(v -> {
            if (productList.size() != 0)
                startActivity(new Intent(activity, ShipedProductListActivity.class).putExtra("products", (Serializable) productList));
        });

        binding.viewAllTv.setOnClickListener(v -> dispatch());

        getDispatchList();

        setUpErrorSound();
        setUpSound();

        return binding.getRoot();
    }

    private void dispatch() {
        ArrayList<String> tracker = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            tracker.add(productList.get(i).getShipment_traker());
        }

        String data = tracker.toString().replace(", ", ",").replaceAll("[\\[.\\]]", "");


        RetrofitClient.getClient(activity).dispatchOrders("dispatch_orders", session.getWarehouseId(), "0", data, UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<DispatchListModel>() {
            @Override
            public void onResponse(Call<DispatchListModel> call, Response<DispatchListModel> response) {
                if (response.code() == 200)
                    if (response.body() != null) if (response.body().getSuccess()) {
                        sucessDialog(response.body().getMessage());

                    } else {
                        failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), "");
                    }
            }

            @Override
            public void onFailure(Call<DispatchListModel> call, Throwable t) {
                failAlert(t.getMessage(), "500");
            }
        });

    }

    private void failAlert(String mes, String s) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Failed with code : " + s).setContentText("Message " + mes).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                }).show();
    }

    @Override
    public void onAdded(ProductListModel model, String code, String message) {

        boolean isAdded = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getShipmentTracker().equalsIgnoreCase(model.getShipment_traker())) {
                isAdded = true;
                break;
            }
        }

        if (isAdded) {

            boolean isAlreadyAdded = false;
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getShipment_traker().equalsIgnoreCase(model.getShipment_traker())) {
                    isAlreadyAdded = true;
                    break;
                }
            }

            if (isAlreadyAdded) {
                failedDialog("Product is already added the  in dispatch list!", "", "");
                playErroBeep(UtilsClass.ErrorBeep);
            } else {
                productList.add(model);
                binding.productRecycler.setAdapter(pAdapter);
                if (productList.size() != 0) binding.viewLinear.setVisibility(View.VISIBLE);
                else binding.viewLinear.setVisibility(View.GONE);

                sucessDialog("Product Added Successfully");
                binding.scanned.setText(String.valueOf(productList.size()));
                playBeep(UtilsClass.SuccessBeep);
            }


        } else {
            failedDialog("Product not found in dispatch list!", "", "");
            playErroBeep(UtilsClass.ErrorBeep);
        }


    }

    private void setUpSound() {
        handler = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();

        soundId = soundPool.load(activity, R.raw.beep_success, 1);

    }

    private void playBeep(int time) {
        final int streamId = soundPool.play(soundId, 1, 1, 1, 0, 1.0f);

        // Stop the sound after 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.stop(streamId);
            }
        }, time);
    }

    private void setUpErrorSound() {
        handler2 = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

        soundPool2 = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();

        soundId2 = soundPool2.load(activity, R.raw.scan_error, 1);

    }

    private void playErroBeep(int time) {
        final int streamId = soundPool2.play(soundId2, 1, 1, 1, 0, 1.0f);

        // Stop the sound after 1 second
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool2.stop(streamId);
            }
        }, time);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool2.release();
    }


    private void getDispatchList() {

        RetrofitClient.getClient(activity).getDispatchList("list_dispatch_orders", session.getWarehouseId(), "0", UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<DispatchListModel>() {
            @Override
            public void onResponse(@NonNull Call<DispatchListModel> call, @NonNull Response<DispatchListModel> response) {
                binding.scanProgress.setVisibility(View.GONE);
                if (response.code() == 200)
                    if (response.body() != null) if (response.body().getSuccess()) {
                        data = response.body().getResults();
                        binding.total.setText(String.valueOf(data.size()));
                        binding.scanned.setText(String.valueOf(productList.size()));
                    }
            }

            @Override
            public void onFailure(@NonNull Call<DispatchListModel> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
                binding.scanProgress.setVisibility(View.GONE);
            }
        });

    }

    private void failedDialog(String mess, String code, String barcode) {
        binding.dataLinear.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.GONE);
        binding.errorCode.setText("Code :" + code);
        binding.errorMessage.setText("Message :" + mess);
        binding.barCode.setVisibility(View.GONE);
        binding.ErrorLienar.setVisibility(View.VISIBLE);

        binding.okBtn.setOnClickListener(v -> {
            binding.dataLinear.setVisibility(View.GONE);
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.ErrorLienar.setVisibility(View.GONE);
            DispatchBarcodeScanFragment.isScannedDispacthed = false;
        });

    }

    private void sucessDialog(String message) {

        binding.viewPager.setVisibility(View.GONE);
        binding.dataLinear.setVisibility(View.VISIBLE);
        binding.successMessage.setText(message);
        binding.successLienar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                sleep(UtilsClass.SleepTime);
                activity.runOnUiThread(() -> {
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.dataLinear.setVisibility(View.GONE);
                    binding.successLienar.setVisibility(View.GONE);
                    DispatchBarcodeScanFragment.isScannedDispacthed = false;
                });


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


}