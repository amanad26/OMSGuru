package com.oms.omsguru.return_processing;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.BaseUrls;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentReturnBarCodeManualBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.models.ReturnModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.SelectedSkuModel;
import com.oms.omsguru.utils.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReturnBarCodeManualFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    FragmentReturnBarCodeManualBinding binding;
    Activity activity;
    public static LinearLayout miin_linear;
    ProductListInterface productListInterface;
    int i = 1;
    ProgressDialog pd;
    Session session;
    private Handler handler2;
    private Handler handler;
    private SoundPool soundPool2;
    private SoundPool soundPool;
    private int soundId2;
    private int soundId;

    ArrayList<SelectedSkuModel> skuList = new ArrayList<>();
    private String barcode1 = "";


    public ReturnBarCodeManualFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReturnBarCodeManualBinding.inflate(getLayoutInflater());


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

        setUpSound();
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
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    session.setP4("0");
                } else {
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    session.setP4("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (session.getP4().equalsIgnoreCase("0")) {
            binding.spinner1.setSelection(0);
        } else {
            binding.spinner1.setSelection(1);
        }


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


    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool2.release();
    }

    private void fetchDetails(String barCode) {
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
                                    List<FetchDetailsModel.Result> list = response.body().getResults();
                                    barcode1 = barCode;

                                    if (ReturnProcessingFragment.selectedProcess == 1) {

                                        if (list.size() > 1) {
                                            startActivity(new Intent(activity, DynamicFragmentActivity.class).putExtra("product", (Serializable) list).putExtra("tracker", barCode));
                                            ReturnProcessingFragment.isSuccessButBacked = true;
                                            ReturnProcessingFragment.barcode1 = barcode1;
                                            playBeep(UtilsClass.SuccessBeep);
                                        } else {
                                            if (list.get(0).getStatus().equalsIgnoreCase("Return Received") || list.get(0).getStatus().equalsIgnoreCase("Cancelled Return Received")) {
                                                failedDialog("Already returned...", "", barCode);
                                                playErroBeep(UtilsClass.ErrorBeep);
                                            } else {
                                                startActivity(new Intent(activity, DynamicFragmentActivity.class).putExtra("product", (Serializable) list).putExtra("tracker", barCode));
                                                ReturnProcessingFragment.isSuccessButBacked = true;
                                                ReturnProcessingFragment.barcode1 = barcode1;
                                                playBeep(UtilsClass.SuccessBeep);
                                            }
                                        }

                                    } else {

                                        if (list.get(0).getStatus().equalsIgnoreCase("Return Received") || list.get(0).getStatus().equalsIgnoreCase("Cancelled Return Received")) {
                                            failedDialog("Already returned...", "", barCode);
                                            playErroBeep(UtilsClass.ErrorBeep);
                                        } else {

                                            if (list.size() > 1) {
                                                startActivity(new Intent(activity, DynamicFragmentActivity.class).putExtra("product", (Serializable) list).putExtra("tracker", barCode));
                                                ReturnProcessingFragment.barcode1 = barcode1;
                                                ReturnProcessingFragment.isSuccessButBacked = true;
                                                playBeep(UtilsClass.SuccessBeep);
                                            } else {

                                                if (list.get(0).getQty().equalsIgnoreCase("1")) {
                                                    playBeep(UtilsClass.SuccessBeep);
                                                    if (ReturnProcessingFragment.selectedProcess == 2) {
                                                        markedAsGood(list.get(0));
                                                    } else if (ReturnProcessingFragment.selectedProcess == 3) {
                                                        markedAsBad(list.get(0));
                                                    }
                                                } else {
                                                    startActivity(new Intent(activity, DynamicFragmentActivity.class).putExtra("product", (Serializable) list).putExtra("tracker", barCode));
                                                    ReturnProcessingFragment.isSuccessButBacked = true;
                                                    ReturnProcessingFragment.barcode1 = barcode1;
                                                    playBeep(UtilsClass.SuccessBeep);
                                                }


                                            }

                                        }

                                    }

                                } else {
                                    playErroBeep(UtilsClass.ErrorBeep);
                                    failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                                }
                            }
                        } catch (Exception e) {
                            playErroBeep(UtilsClass.ErrorBeep);
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void markedAsGood(FetchDetailsModel.Result d1) {

        pd.show();
        FetchDetailsModel.Result data = d1;
        Log.e("TAG", "onClick: Skus " + data.getSku().toString());
        skuList.clear();

        for (int i = 0; i < data.getSku().size(); i++) {
            String good = "0", bad = "0", sku_code;

            sku_code = data.getSku().get(i).getSkuCode();

            if (data.getSku().get(i).getQty().equalsIgnoreCase("1")) {
                if (data.getSku().get(i).isGood()) {
                    good = "1";
                } else {
                    bad = "1";
                }
                SelectedSkuModel sku = new SelectedSkuModel(sku_code, "1", "0");
                skuList.add(sku);
            }
        }

        String confirmReturn = "";
        if (data.getStatus().equalsIgnoreCase("Return Init")) {
            confirmReturn = "customer";
        } else if (data.getStatus().equalsIgnoreCase("Cancel Init")) {
            confirmReturn = "courier";
        }

        Map<String, String> params = new HashMap();
        params.put("data[command]", "process_return");
        params.put("data[warehouse_id]", session.getWarehouseId());
        params.put("data[company_id]", session.getCompanyId());
        params.put("data[return_type]", "1");
        params.put("data[shipment_tracker]", barcode1);
        params.put("data[invoice_id]", data.getInvoiceId());
        params.put("data[confirm_return_type]", confirmReturn);
        params.put("data[channel_id]", UtilsClass.channelId);
        params.put("data[Client][auth_user_id]", session.getUserId());
        params.put("data[Client][auth_code]", session.getAuth_code());
        params.put("data[Client][last_permission_update]", session.getPermission_code());

        for (int i = 0; i < skuList.size(); i++) {
            params.put("data[Sku][" + i + "][sku_code]", skuList.get(i).getSku());
            params.put("data[Sku][" + i + "][good]", skuList.get(i).getGood());
            params.put("data[Sku][" + i + "][bad]", skuList.get(i).getBad());
        }

        acceptRetutn(params);

    }

    private void sucessDialog(String barCode) {
        binding.miinLinear.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.VISIBLE);
        binding.ErrorLienar.setVisibility(View.GONE);
        binding.successMessage.setText(barCode);

        new Thread(() -> {
            try {
                sleep(UtilsClass.SleepTime);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.miinLinear.setVisibility(View.VISIBLE);
                        binding.successLienar.setVisibility(View.GONE);
                        binding.ErrorLienar.setVisibility(View.GONE);
                    }
                });


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void acceptRetutn(Map<String, String> params) {

        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseUrls.BASE_URL + BaseUrls.VALIDATE_PACK_DETAIL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "onResponse: Response " + response);
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String me = jsonObject.getString("message");

                    if (code == 0) {
                        sucessDialog(me);
                    } else {
                        failedDialog(me, code + "", barcode1);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, error -> {
            pd.dismiss();
            failedDialog(error.toString(), "500", barcode1);
            Log.e("TAG", "onErrorResponse: " + error.toString());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Log.e("TAG", "getParams: All Parametrs " + params);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }

    private void markedAsBad(FetchDetailsModel.Result d1) {

        FetchDetailsModel.Result data = d1;
        Log.e("TAG", "onClick: Skus " + data.getSku().toString());
        skuList.clear();

        for (int i = 0; i < data.getSku().size(); i++) {
            String good = "0", bad = "0", sku_code;

            sku_code = data.getSku().get(i).getSkuCode();

            if (data.getSku().get(i).getQty().equalsIgnoreCase("1")) {
                if (data.getSku().get(i).isGood()) {
                    good = "1";
                } else {
                    bad = "1";
                }
                SelectedSkuModel sku = new SelectedSkuModel(sku_code, "0", "1");
                skuList.add(sku);
            }
        }

        String confirmReturn = "";
        if (data.getStatus().equalsIgnoreCase("Return Init")) {
            confirmReturn = "customer";
        } else if (data.getStatus().equalsIgnoreCase("Cancel Init")) {
            confirmReturn = "courier";
        }

        Map<String, String> params = new HashMap();
        params.put("data[command]", "process_return");
        params.put("data[warehouse_id]", session.getWarehouseId());
        params.put("data[company_id]", session.getCompanyId());
        params.put("data[return_type]", "1");
        params.put("data[shipment_tracker]", barcode1);
        params.put("data[invoice_id]", data.getInvoiceId());
        params.put("data[confirm_return_type]", confirmReturn);
        params.put("data[channel_id]", UtilsClass.channelId);
        params.put("data[Client][auth_user_id]", session.getUserId());
        params.put("data[Client][auth_code]", session.getAuth_code());
        params.put("data[Client][last_permission_update]", session.getPermission_code());

        for (int i = 0; i < skuList.size(); i++) {
            params.put("data[Sku][" + i + "][sku_code]", skuList.get(i).getSku());
            params.put("data[Sku][" + i + "][good]", skuList.get(i).getGood());
            params.put("data[Sku][" + i + "][bad]", skuList.get(i).getBad());
        }

        acceptRetutn(params);

    }

    private void failedDialog(String mess, String code, String barcode) {
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
                ReturnProcessingFragment.isSuccessButBacked = false;
            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            binding.barCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }
}