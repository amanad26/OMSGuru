package com.oms.omsguru.validate_pack_order;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Intent;
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
import com.oms.omsguru.databinding.FragmentValidateBarcodeMannualBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidatePackBarcodeMannualFragment extends Fragment {
    String[] country = {"  123  ", "  abc  "};


    public static boolean isCompleted2 = false;
    public static boolean isFailed2 = false;
    public static String message2 = "";


    FragmentValidateBarcodeMannualBinding binding;
    Activity activity;
    ArrayList<String> codeLIst = new ArrayList<>();
    ProductListInterface productListInterface;
    int i = 1;
    ProgressDialog pd;
    Session session;

    private Handler handler;
    private Handler handler2;
    private SoundPool soundPool;
    private int soundId;
    private int soundId2;
    private SoundPool soundPool2;
    private FetchDetailsModel.Result data = null;
    private String barCodeText = "";

    public ValidatePackBarcodeMannualFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentValidateBarcodeMannualBinding.inflate(getLayoutInflater());
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
                getInvoiceDetails(binding.barCodeEt.getText().toString());
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
        binding.successLienar.setVisibility(View.VISIBLE);
        binding.ErrorLienar.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        setupSpinner();
        if (isCompleted2) {
            if (data != null) {
                addProduct(data, barCodeText);
                sucessDialog("Product Scanned..");
            }
        }
        isCompleted2 = false;

        if (isFailed2) {
            failedDialog(message2, "200", barCodeText);
            message2 = "";
        }
        isFailed2 = false;

    }



    private void addProduct(FetchDetailsModel.Result data, String barCode) {

        String sku_name = "No SKU", product_name, invoice, qty, amount, date;
        if (data.getSku().size() != 0) {
            sku_name = data.getSku().get(0).getSkuCode();
        }
        product_name = data.getProductName();
        invoice = data.getInvoiceId();
        qty = data.getQty();
        amount = data.getAmount();
        date = data.getOrderDate();

        List<FetchDetailsModel.Result.Sku> skuLIst = data.getSku();


        ProductListModel pm = new ProductListModel(sku_name, product_name, amount, Float.parseFloat(qty), invoice, date, skuLIst, data, barCode);

        productListInterface.onAdded(pm, "", "");
        i++;

    }


    private void getInvoiceDetails(String barCode) {
        pd.show();
        RetrofitClient.getClient(activity).getInvoiceDetails("get_order", session.getWarehouseId(), session.getCompanyId(), barCode, UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<GetOrderModel>() {
            @Override
            public void onResponse(Call<GetOrderModel> call, Response<GetOrderModel> response) {
                pd.dismiss();
                if (response.code() == 200) if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        if (response.body().getCode() == -2) {
                            failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
//                                failAlert(response.body().getMessage(), String.valueOf(response.body().getCode()));
                            playErroBeep(UtilsClass.ErrorBeep);
                        }
                        try {
                            if (response.body().getResults() != null) {
                                playBeep(UtilsClass.SuccessBeep);
                                // sucessDialog(response.body().getResults());
                                startActivity(new Intent(activity, ValidateAndPackActivity.class).putExtra("product", (Serializable) response.body().getResults()));
                                fetchDetails(response.body().getResults().getInvoice().getShipment_tracker());

                            }

                        } catch (Exception e) {
                            failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                            playErroBeep(UtilsClass.ErrorBeep);
                        }
                    } else {
                        playErroBeep(UtilsClass.ErrorBeep);
                        failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                    }


                }


            }

            @Override
            public void onFailure(Call<GetOrderModel> call, Throwable t) {
                playErroBeep(UtilsClass.ErrorBeep);
            }
        });


    }

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

    private void fetchDetails(String barCode) {
        RetrofitClient.getClient(activity).fetchDetailsNew("fetch_details", session.getWarehouseId(), session.getCompanyId(), barCode, UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<FetchDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<FetchDetailsModel> call, @NonNull Response<FetchDetailsModel> response) {
                if (response.code() == 200) if (response.body() != null) {
                    if (response.body().isSuccess()) {
                        if (response.body().getCode() == -2) {

                        }
                        try {
                            if (response.body().getResults() != null) {
                                if (response.body().getResults().size() != 0) {
                                    data = response.body().getResults().get(0);
                                    barCodeText = barCode;

                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(activity, "Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<FetchDetailsModel> call, @NonNull Throwable t) {
                pd.dismiss();
                playErroBeep(UtilsClass.ErrorBeep);
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
                    session.setP3("0");
                } else {
                    session.setP3("1");
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        if (session.getP3().equalsIgnoreCase("0")) {
            binding.spinner1.setSelection(0);
        } else {
            binding.spinner1.setSelection(1);
        }


    }


}

