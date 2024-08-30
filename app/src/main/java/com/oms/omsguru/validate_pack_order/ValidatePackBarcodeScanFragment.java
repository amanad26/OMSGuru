package com.oms.omsguru.validate_pack_order;

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

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
import com.oms.omsguru.apis.RetrofitClient;
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

public class ValidatePackBarcodeScanFragment extends Fragment {

    public static boolean isCompleted = false;
    public static boolean isFailed = false;
    public static String message = "";

    public boolean isScannedValidate = false;
    private Handler handler;
    private SoundPool soundPool;


    private SoundPool soundPool2;
    private int soundId;
    private int soundId2;
    private Handler handler2;

    private RelativeLayout mainLinear;
    private LinearLayout sucessLinear;
    private LinearLayout errorLinear;
    private TextView errorCode;
    private TextView errorMessage;
    private FetchDetailsModel.Result data = null;
    String barCodeText = "";


    public ValidatePackBarcodeScanFragment(ProductListInterface productListInterface) {
        this.productListInterface = productListInterface;
    }

    Activity activity;
    private QRReaderFragment qrCodeReader;
    View view;
    private QRCameraConfiguration config;
    ArrayList<String> codeLIst = new ArrayList<>();
    ProductListInterface productListInterface;
    int i = 0;
    boolean isScanned = false;
    Session session;
    ProgressDialog pd;
    private TextView barCode;
    private CardView okBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_validate_barcode_scan, container, false);

        activity = requireActivity();
        session = new Session(activity);
        pd = new ProgressDialog(activity);

        qrCodeReader = (QRReaderFragment) getChildFragmentManager().findFragmentById(R.id.scanner);

        mainLinear = view.findViewById(R.id.miin_linear);
        sucessLinear = view.findViewById(R.id.successLienar);
        errorLinear = view.findViewById(R.id.ErrorLienar);
        errorCode = view.findViewById(R.id.errorCode);
        errorMessage = view.findViewById(R.id.errorMessage);
        okBtn = view.findViewById(R.id.okBtn);
        barCode = view.findViewById(R.id.barCode);

        BarcodeScannerOptions qrOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();

        config = new QRCameraConfiguration(CameraSelector.LENS_FACING_BACK, qrOptions);
        qrCodeReader.enableTorch(true);

        qrCodeReader.setListener(new QRReaderListener() {
            @Override
            public void onRead(@NonNull Barcode barcode, @NonNull List<? extends Barcode> list) {
                if (!isScannedValidate) {
                    if (!isScanned) {
                        Log.e("TAG", "onRead: " + barcode.getDisplayValue());
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(100);
                        isScanned = true;
                        isScannedValidate = true;
                        getInvoiceDetails(barcode.getDisplayValue());
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

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLinear.setVisibility(View.VISIBLE);
                sucessLinear.setVisibility(View.GONE);
                errorLinear.setVisibility(View.GONE);
                isScannedValidate = false;
            }
        });

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(UtilsClass.SleepTime);
                    //  stylishDialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLinear.setVisibility(View.VISIBLE);
                            sucessLinear.setVisibility(View.GONE);
                            errorLinear.setVisibility(View.GONE);
                        }
                    });


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();*/
    }

    private void sucessDialog() {
        mainLinear.setVisibility(View.GONE);
        sucessLinear.setVisibility(View.VISIBLE);
        errorLinear.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(UtilsClass.SleepTime);
                    //  stylishDialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            startActivity(new Intent(activity, ValidateAndPackActivity.class)
//                                    .putExtra("product", (Serializable) results)
//                            );
                            mainLinear.setVisibility(View.VISIBLE);
                            sucessLinear.setVisibility(View.GONE);
                            errorLinear.setVisibility(View.GONE);
                            isScannedValidate = false;
                        }
                    });


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool2.release();
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

//                                    StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.SUCCESS);
//                                    stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                                    stylishDialog.setTitleText("Product").setContentText("Now you can validate the order ").setCancellable(false)
//                                            .setConfirmButton("Ok", new StylishAlertDialog.OnStylishClickListener() {
//                                                @Override
//                                                public void onClick(StylishAlertDialog StylishAlertDialog) {
//                                                    startActivity(new Intent(activity, ValidateAndPackActivity.class)
//                                                            .putExtra("product", (Serializable) response.body().getResults())
//                                                    );
//                                                    //  fetchDetails(response.body().getResults().getInvoice()., response.body().getMessage());
//                                                    StylishAlertDialog.dismiss();
//                                                }
//                                            })
//                                            .show();
                            }

                        } catch (Exception e) {
                            failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
                            playErroBeep(UtilsClass.ErrorBeep);
                            //  Toast.makeText(activity, "Try again later", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        playErroBeep(UtilsClass.ErrorBeep);
                        failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()), barCode);
//                            failAlert(response.body().getMessage(), String.valueOf(response.body().getCode()));
                    }


                }


            }

            @Override
            public void onFailure(Call<GetOrderModel> call, Throwable t) {
                playErroBeep(UtilsClass.ErrorBeep);
            }
        });


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
                    } else {
//                                    playErroBeep(UtilsClass.ErrorBeep);
//                                    failedDialog(response.body().getMessage(), String.valueOf(response.body().getCode()));
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


    private void sleepFor10Second() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
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
        if (isCompleted) {
            if (data != null) {
                addProduct(data, barCodeText);
                sucessDialog();
            }
        }
        isCompleted = false;

        if (isFailed) {
            failedDialog(message, "200", barCodeText);
            message = "";
        }
        isFailed = false;

    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Toast.makeText(activity, "If Part ", Toast.LENGTH_SHORT).show();
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


}
