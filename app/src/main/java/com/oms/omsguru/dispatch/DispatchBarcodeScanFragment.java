package com.oms.omsguru.dispatch;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class DispatchBarcodeScanFragment extends Fragment {
    public static boolean isScannedDispacthed = false;
    private SoundPool soundPool2;
    private RelativeLayout mainLinear;
    private LinearLayout sucessLinear;
    private LinearLayout errorLinear;
    private TextView errorCode;
    private TextView errorMessage;
    private TextView barCode;
    private CardView okBtn;
    private int soundId2;
    private Handler handler2;

    public DispatchBarcodeScanFragment(ProductListInterface productListInterface) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dispatch_barcode_scan, container, false);

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
                if (!isScannedDispacthed) {
                    if (!isScanned) {
                        Log.e("TAG", "onRead: " + barcode.getDisplayValue());
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(100);
                        isScanned = true;
                        isScannedDispacthed = true;
                        fetchDetails(barcode.getRawValue());
                        sleepFor10Second();
                    }
                }

            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });

        setUpErrorSound();

        return view;
    }

    private void failedDialog(String code, String mess, String barcode) {
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
            isScannedDispacthed = false;
        });


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
        soundPool2.release();
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

    private void addProduct(FetchDetailsModel.Result data, String barCode, String code, String message) {

        String sku_name = "No SKU", product_name, invoice, qty, amount, date, shipment_tracker;
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

        productListInterface.onAdded(pm, code, message);

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
                                        playErroBeep(UtilsClass.ErrorBeep);
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
                    }
                });

    }

}
