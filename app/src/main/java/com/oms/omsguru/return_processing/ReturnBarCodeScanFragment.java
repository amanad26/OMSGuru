package com.oms.omsguru.return_processing;


import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.murgupluoglu.qrreader.QRCameraConfiguration;
import com.murgupluoglu.qrreader.QRReaderFragment;
import com.murgupluoglu.qrreader.QRReaderListener;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.BaseUrls;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.models.FetchDetailsModel;
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

public class ReturnBarCodeScanFragment extends Fragment {
    Activity activity;
    public static QRReaderFragment qrCodeReader;
    View view;
    String barcode1;
    ArrayList<SelectedSkuModel> skuList = new ArrayList<>();

    private QRCameraConfiguration config;
    ProductListInterface productListInterface;
    public static boolean isAccept = false;
    int i = 0;
    boolean isScanned = false;
    public static boolean isScannedNew = false;
    Session session;
    ProgressDialog pd;
    private Handler handler;
    private Handler handler2;
    private SoundPool soundPool;
    private SoundPool soundPool2;
    private int soundId;
    private int soundId2;
    public static RelativeLayout mainLinear;
    public static LinearLayout sucessLinear;
    public static LinearLayout errorLinear;
    public static TextView errorCode;
    public static TextView errorMessage;
    public static TextView barCode;
    public static TextView successMessage;
    public static CardView okBtn;

    public ReturnBarCodeScanFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_return_bar_code_scan, container, false);

        activity = requireActivity();
        session = new Session(activity);
        pd = new ProgressDialog(activity);

        //Old
        qrCodeReader = (QRReaderFragment) getChildFragmentManager().findFragmentById(R.id.scanner);

        mainLinear = view.findViewById(R.id.miin_linear);
        sucessLinear = view.findViewById(R.id.successLienar);
        errorLinear = view.findViewById(R.id.ErrorLienar);
        errorCode = view.findViewById(R.id.errorCode);
        errorMessage = view.findViewById(R.id.errorMessage);
        okBtn = view.findViewById(R.id.okBtn);
        barCode = view.findViewById(R.id.barCode);
        successMessage = view.findViewById(R.id.successMessage);

        BarcodeScannerOptions qrOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();

        config = new QRCameraConfiguration(CameraSelector.LENS_FACING_BACK, qrOptions);
        qrCodeReader.enableTorch(true);

        qrCodeReader.setListener(new QRReaderListener() {
            @Override
            public void onRead(@NonNull Barcode barcode, @NonNull List<? extends Barcode> list) {

                if (!isScannedNew) {
                    if (!isScanned) {
                        Log.e("TAG", "onRead: " + barcode.getDisplayValue());
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(100);
                        isScanned = true;
                        isScannedNew = true;
                        fetchDetails(barcode.getRawValue());
                        sleepFor10Second();
                    }
                }


            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });

        setUpSound();
        setUpErrorSound();
        return view;

    }

    private void failedDialog(String mess, String code, String barcode) {
        mainLinear.setVisibility(View.GONE);
        sucessLinear.setVisibility(View.GONE);
        errorLinear.setVisibility(View.VISIBLE);
        errorCode.setText("Failed with code : " + code);
        errorMessage.setText("Message : " + mess);
        barCode.setText("Barcode : " + barcode);

        okBtn.setOnClickListener(v -> {
            mainLinear.setVisibility(View.VISIBLE);
            sucessLinear.setVisibility(View.GONE);
            errorLinear.setVisibility(View.GONE);
            ReturnProcessingFragment.isSuccessButBacked = false;
            isScannedNew = false;
        });

    }

    private void sucessDialog(String barCode) {
        mainLinear.setVisibility(View.GONE);
        sucessLinear.setVisibility(View.VISIBLE);
        errorLinear.setVisibility(View.GONE);
        successMessage.setText(barCode);

        new Thread(() -> {
            try {
                sleep(UtilsClass.SleepTime);
                //  stylishDialog.dismiss();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainLinear.setVisibility(View.VISIBLE);
                        sucessLinear.setVisibility(View.GONE);
                        errorLinear.setVisibility(View.GONE);
                        isScannedNew = false;
                    }
                });


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
        isScannedNew = false;
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


    private void sleepFor10Second() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    isScanned = false;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        getPermission();
    }


    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    qrCodeReader.startCamera(requireActivity(), config);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {

                    Toast.makeText(activity, response.getPermissionName() + " Is denied..", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        } else {
            Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    qrCodeReader.startCamera(requireActivity(), config);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    Toast.makeText(activity, response.getPermissionName() + " Is denied..", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        }
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("TAG", "getParams: All Parametrs " + params);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }

    public static boolean showWaringDialog(Context context) {
        mainLinear.setVisibility(View.GONE);
        isScannedNew = true;
        // ReturnBarCodeManualFragment.miin_linear.setVisibility(View.GONE);
        StylishAlertDialog stylishDialog = new StylishAlertDialog(context, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Warning").setContentText("Are you sure you want automation bad quantity accept without claim").setCancellable(false)
                .setNeutralButton("No", StylishAlertDialog -> {
                    stylishDialog.dismiss();
                    mainLinear.setVisibility(View.GONE);
                    isScannedNew = true;
                })

                .setConfirmButton("yes", StylishAlertDialog -> {
                    mainLinear.setVisibility(View.VISIBLE);
                    isScannedNew = false;
                    stylishDialog.dismiss();
                }).show();


        return isAccept;
    }

    public static void onChange() {
        mainLinear.setVisibility(View.VISIBLE);
    }


}