package com.oms.omsguru.ships;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentShipBarcodeMannualBinding;
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

public class ShipBarcodeMannualFragment extends Fragment {

    FragmentShipBarcodeMannualBinding binding;
    Activity activity;
    ArrayList<String> codeLIst = new ArrayList<>();
    ProductListInterface productListInterface;
    int i = 1;
    ProgressDialog pd;
    Session session;
    private SoundPool soundPool;
    private SoundPool soundPool2;
    private Handler handler;
    private Handler handler2;
    private int soundId;
    private int soundId2;

    public ShipBarcodeMannualFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShipBarcodeMannualBinding.inflate(getLayoutInflater());
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
                shipOrder(binding.barCodeEt.getText().toString());
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

        setUpSound();
        setUpErrorSound();

        return binding.getRoot();

    }


    private void failedDialog(String mess, String code, String barcode) {
        binding.miinLinear.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.GONE);
        binding.ErrorLienar.setVisibility(View.VISIBLE);
        binding.errorCode.setText("Failed with code : " + code);
        binding.errorMessage.setText("Message : " + mess);
        binding.barCode.setText("Barcode : " + barcode);

        binding.okBtn.setOnClickListener(v -> {
            binding.miinLinear.setVisibility(View.VISIBLE);
            binding.successLienar.setVisibility(View.GONE);
            binding.ErrorLienar.setVisibility(View.GONE);
        });

    }

    private void sucessDialog(String message) {

        binding.miinLinear.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.GONE);
        binding.ErrorLienar.setVisibility(View.VISIBLE);
        binding.successMessage.setText(message);


        new Thread(() -> {
            try {
                sleep(UtilsClass.SleepTime);
                activity.runOnUiThread(() -> {
                    binding.miinLinear.setVisibility(View.VISIBLE);
                    binding.successLienar.setVisibility(View.GONE);
                    binding.ErrorLienar.setVisibility(View.GONE);
                });


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void addProduct(FetchDetailsModel.Result data, String message, String barCode) {

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

        productListInterface.onAdded(pm, "", "");
        i++;

    }


    @Override
    public void onResume() {
        super.onResume();
        setupSpinner();
    }

    private void shipOrder(String barCode) {

        pd.show();
        RetrofitClient.getClient(activity).shipOrder("ship_order", session.getWarehouseId(), session.getCompanyId(), barCode, UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<FetchDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<FetchDetailsModel> call, @NonNull Response<FetchDetailsModel> response) {
                pd.dismiss();
                if (response.code() == 200) if (response.body() != null) {
                    if (response.body().isSuccess()) {
                        fetchDetails(barCode, response.body().getMessage());
                    } else {
                        //failedDialog();
                        failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                        playErroBeep(UtilsClass.ErrorBeep);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<FetchDetailsModel> call, @NonNull Throwable t) {
                pd.dismiss();
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchDetails(String barCode, String message) {
        pd.show();
        RetrofitClient.getClient(activity).fetchDetailsNew("fetch_details", session.getWarehouseId(), session.getCompanyId(), barCode, UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<FetchDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<FetchDetailsModel> call, @NonNull Response<FetchDetailsModel> response) {
                pd.dismiss();
                if (response.code() == 200) if (response.body() != null) {
                    if (response.body().isSuccess()) {
                        if (response.body().getCode() == -2) {
                            failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                            playErroBeep(UtilsClass.ErrorBeep);
                        }
                        try {
                            if (response.body().getResults() != null) {
                                if (response.body().getResults().size() != 0) {
                                    FetchDetailsModel.Result data = response.body().getResults().get(0);
                                    addProduct(data, message, barCode);
                                    sucessDialog(message);
                                    playBeep(UtilsClass.SuccessBeep);
                                } else {
                                    playErroBeep(UtilsClass.ErrorBeep);
                                    failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(activity, "Try again later", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        playErroBeep(UtilsClass.ErrorBeep);
                        failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<FetchDetailsModel> call, @NonNull Throwable t) {
                pd.dismiss();
                playErroBeep(UtilsClass.ErrorBeep);
                failedDialog(t.getMessage(), "500", barCode);
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
            }
        });

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
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    session.setP2("0");
                } else {
                    session.setP2("1");
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        if (session.getP2().equalsIgnoreCase("0")) {
            binding.spinner1.setSelection(0);
        } else {
            binding.spinner1.setSelection(1);
        }

    }


    /////

    private void setUpSound() {
        handler = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool2.release();
    }

}

