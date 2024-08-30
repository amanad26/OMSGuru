package com.oms.omsguru.validate_pack_order.SkuCheck;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;
import com.oms.omsguru.utils.ValidateInterface;

import java.util.ArrayList;
import java.util.List;

public class SkuCheckBarcodeScanFragment extends Fragment {

    public boolean isScannedSku = false;
    private Handler handler, handler2;

    private SoundPool soundPool;
    private int soundId;
    private SoundPool soundPool2;
    private int soundId2;

    public SkuCheckBarcodeScanFragment(ProductListInterface productListInterface, List<GetOrderModel.Results.Sku> sks, ValidateInterface validateInterface) {
        this.productListInterface = productListInterface;
        this.sks = sks;
        this.validateInterface = validateInterface;
    }


    List<GetOrderModel.Results.Sku> sks = new ArrayList<>();
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
    ValidateInterface validateInterface;
    private RelativeLayout mainLinear;
    private LinearLayout sucessLinear;
    private LinearLayout errorLinear;
    private TextView errorCode;
    private TextView errorMessage;
    private TextView barCode;
    private CardView okBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sku__barcode_scan, container, false);

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


        BarcodeScannerOptions qrOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        config = new QRCameraConfiguration(CameraSelector.LENS_FACING_BACK, qrOptions);
        qrCodeReader.enableTorch(true);

        qrCodeReader.setListener(new QRReaderListener() {
            @Override
            public void onRead(@NonNull Barcode barcode, @NonNull List<? extends Barcode> list) {
                if (!isScannedSku) {
                    if (!isScanned) {
                        Log.e("TAG", "onRead: " + barcode.getDisplayValue());
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(100);
                        isScannedSku = true;
                        isScanned = true;
                        checkSkus(barcode.getRawValue());
                        sleepFor10Second();
                    }
                }

            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });

        setUpErrorSound();
        setUpSound();

        return view;

    }


    private void failedDialog(String mess, String code, String barcode) {
        mainLinear.setVisibility(View.GONE);
        sucessLinear.setVisibility(View.GONE);
        errorLinear.setVisibility(View.VISIBLE);
        errorCode.setText("Failed with code : " + code);
        errorMessage.setText("Message : " + mess);
        barCode.setVisibility(View.GONE);

        okBtn.setOnClickListener(v -> {
            mainLinear.setVisibility(View.VISIBLE);
            sucessLinear.setVisibility(View.GONE);
            errorLinear.setVisibility(View.GONE);
            isScannedSku = false;
        });

    }




    private void checkSkus(String barCode) {

        boolean isAva = false;
        int pos = 0;
        for (int i = 0; i < sks.size(); i++) {
            if (barCode.equalsIgnoreCase(sks.get(i).getSkuCode())) {
                isAva = true;
                pos = i;
                break;
            }
        }

        if (isAva) {
            isScannedSku = false;
            playBeep(UtilsClass.SuccessBeep);
            validateInterface.onAdded(pos);
        } else {
            playErroBeep(UtilsClass.ErrorBeep);
            failedDialog("Invalid SKU Code ", "-2", "");
        }

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


}
