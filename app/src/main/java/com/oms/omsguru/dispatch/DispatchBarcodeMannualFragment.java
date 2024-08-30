package com.oms.omsguru.dispatch;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentDispatchBarcodeMannualBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchBarcodeMannualFragment extends Fragment {

    FragmentDispatchBarcodeMannualBinding binding;
    Activity activity;
    ProductListInterface productListInterface;
    int i = 1;
    ProgressDialog pd;
    Session session;
    private Handler handler2;
    private SoundPool soundPool2;
    private int soundId2;

    public DispatchBarcodeMannualFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDispatchBarcodeMannualBinding.inflate(getLayoutInflater());
        activity = requireActivity();
        pd = new ProgressDialog(activity);
        session = new Session(activity);

        binding.alphaNumeric.setOnClickListener(v -> {
            binding.alphaNumeric.setTextColor(getResources().getColor(R.color.white));
            binding.alphaNumeric.setBackgroundResource(R.drawable.linear_full_bg);

            binding.numeric.setTextColor(getResources().getColor(R.color.app_common_color));
            binding.numeric.setBackgroundResource(R.drawable.linear_light_bg);

            binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
        });

        binding.submitBtn.setOnClickListener(v -> {
            if (!binding.barCodeEt.getText().toString().equalsIgnoreCase("")) {
                fetchDetails(binding.barCodeEt.getText().toString());
                binding.barCodeEt.setText("");
            } else Toast.makeText(activity, "Enter Bar Code ", Toast.LENGTH_SHORT).show();
        });

        binding.numeric.setOnClickListener(v -> {
            binding.numeric.setTextColor(getResources().getColor(R.color.white));
            binding.numeric.setBackgroundResource(R.drawable.linear_full_bg);

            binding.alphaNumeric.setTextColor(getResources().getColor(R.color.app_common_color));
            binding.alphaNumeric.setBackgroundResource(R.drawable.linear_light_bg);
            binding.barCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        setUpErrorSound();
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        setupSpinner();
    }

    private void setupSpinner() {
        List<String> footballPlayers = new ArrayList<>();
        footballPlayers.add("123");
        footballPlayers.add("abc");

        binding.spinner1.setItem(footballPlayers);

        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    session.setP1("0");
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    session.setP1("1");
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (session.getP1().equalsIgnoreCase("0")) {
            binding.spinner1.setSelection(0);
        } else {
            binding.spinner1.setSelection(1);
        }

    }


    private void setUpErrorSound() {
        handler2 = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool2 = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

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


    private void failedDialog(String code, String mess, String barcode) {
        binding.miinLinear.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.GONE);
        binding.ErrorLienar.setVisibility(View.VISIBLE);
        binding.errorCode.setText("Failed with code : " + code);
        binding.errorMessage.setText("Message : " + mess);
        binding.barCode.setText("Barcode : " + barcode);

        binding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.miinLinear.setVisibility(View.VISIBLE);
                binding.successLienar.setVisibility(View.GONE);
                binding.ErrorLienar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool2.release();
    }

    private void addProduct(FetchDetailsModel.Result data, String barCode, String s, String message) {

        String sku_name = "No SKU", product_name, invoice, qty, amount, date;
        if (data.getSku().size() != 0) {
            sku_name = data.getSku().get(0).getSkuCode();
        }
        product_name = data.getProductName();
        invoice = data.getInvoiceId();
        qty = data.getQty();
        amount = data.getAmount();
        date = data.getOrderDate();

        List<FetchDetailsModel.Result.Sku> skuList = data.getSku();

        ProductListModel pm = new ProductListModel(sku_name, product_name, amount, Float.parseFloat(qty), invoice, date, skuList, data, barCode);

        productListInterface.onAdded(pm, s, message);

    }

    private void fetchDetails(String barCode) {
        pd.show();
        RetrofitClient.getClient(activity).
                fetchDetailsNew("fetch_details"
                        , session.getWarehouseId()
                        , session.getCompanyId()
                        , barCode,
                        UtilsClass.channelId,
                        session.getUserId(),
                        session.getAuth_code(),
                        session.getPermission_code()
                ).enqueue(new Callback<FetchDetailsModel>() {
                    @Override
                    public void onResponse(@NonNull Call<FetchDetailsModel> call, @NonNull Response<FetchDetailsModel> response) {
                        pd.dismiss();
                        if (response.code() == 200)
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    if (response.body().getCode() == -2) {
                                        failedDialog(String.valueOf(response.body().getCode()), response.body().getMessage(), barCode);
                                        playErroBeep(UtilsClass.ErrorBeep);
                                    }
                                    try {
                                        if (response.body().getResults() != null) {
                                            if (response.body().getResults().size() != 0) {
                                                FetchDetailsModel.Result data = response.body().getResults().get(0);
                                                addProduct(data, barCode, String.valueOf(response.body().getCode()), response.body().getMessage());
                                            } else {
                                                playErroBeep(UtilsClass.ErrorBeep);
                                                failedDialog(String.valueOf(response.body().getCode()), response.body().getMessage(), barCode);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(activity, "Try again later", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    playErroBeep(UtilsClass.ErrorBeep);
                                    failedDialog(String.valueOf(response.body().getCode()), response.body().getMessage(), barCode);
                                }

                            }

                    }

                    @Override
                    public void onFailure(@NonNull Call<FetchDetailsModel> call, @NonNull Throwable t) {
                        pd.dismiss();
                        playErroBeep(UtilsClass.ErrorBeep);
                        failedDialog(t.getMessage(), "500", barCode);
                    }
                });

    }


}