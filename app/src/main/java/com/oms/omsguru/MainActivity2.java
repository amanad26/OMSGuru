package com.oms.omsguru;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.oms.omsguru.return_processing.UploadFileActivity;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.VideoCompressor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSession;
    private MediaRecorder mediaRecorder;
    private Size videoSize;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    Button buttonRecord;
    String path = "";
    String path2 = "";
    boolean isByMeStop = false;
    ProgressDialog pd;
    boolean isAbhi = true;
    boolean isFinished = false;
    private CountDownTimer countDownTimer;
    TextView timerTextview;
    private int timeElapsed = 0;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        activity = this;

        textureView = findViewById(R.id.textureView);
        buttonRecord = findViewById(R.id.buttonRecord);
        timerTextview = findViewById(R.id.timerTextview);
        pd = new ProgressDialog(this);

        timerTextview.setText("0:00");

        textureView.setSurfaceTextureListener(textureListener);

        Dexter.withContext(MainActivity2.this).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                if (isAbhi) {
                    buttonRecord.setOnClickListener(v -> {
                        if (buttonRecord.getText().toString().equalsIgnoreCase("Stop")) {
                            isByMeStop = true;
                            if (!isFinished) {
                                stopCountUpTimer();
                                timerTextview.setText("Recording finished Please Wait!");
                            }

                            stopRecording();
                            buttonRecord.setText("Start");
                        } else {
                            path = "";
                            isByMeStop = false;
                            buttonRecord.setText("Stop");
                            startRecording();
                            isAbhi = false;
                            startCountdown();
                            setThread();
                        }

                    });
                }

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity2.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    private void setThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    isAbhi = true;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void startPreview() {
        if (cameraDevice == null || !textureView.isAvailable()) return;
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) return;
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        if (cameraDevice == null) return;

        try {
            closePreviewSession();
            setUpMediaRecorder();

            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(videoSize.getWidth(), videoSize.getHeight());
            Surface previewSurface = new Surface(texture);
            Surface recorderSurface = mediaRecorder.getSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureRequestBuilder.addTarget(previewSurface);
            captureRequestBuilder.addTarget(recorderSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, recorderSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                        mediaRecorder.start();
//                        buttonRecord.setText("Stop");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isByMeStop) stopRecording();
                            }
                        }, 61000); // Stop after 1 minute
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, backgroundHandler);
        } catch (IOException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startCountdown() {
        // Set countdown time in milliseconds (e.g., 10 seconds)
        long countdownTime = 60000;
        int t = 0;

        countDownTimer = new CountDownTimer(countdownTime, 1000) { // Interval set to 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                timeElapsed += 1; // Increment the time elapsed
                timerTextview.setText(formatTime(timeElapsed));
                //timerTextview.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                timerTextview.setText("Recording finished Please Wait!");
                isFinished = true;
            }
        };

        // Start the countdown
        countDownTimer.start();
    }

    private void stopCountUpTimer() {
        countDownTimer.cancel();
        timerTextview.setText("Recording Finished Please Wait!"); // Show the current time elapsed
    }


    private void setUpMediaRecorder() throws IOException {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        } else {
            mediaRecorder.reset();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        path = getVideoFilePath();
        Log.e("TAG", "setUpMediaRecorder: Path is " + path);
        mediaRecorder.setOutputFile(path);
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.prepare();
    }

    private String getVideoFilePath() {
        File dir = getExternalFilesDir(null);

        return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + "oms_guru" + new Random().nextInt(10000) + "video.mp4";
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(127);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void stopRecording() {


        if (cameraCaptureSession != null) {
            try {
                cameraCaptureSession.stopRepeating();
                cameraCaptureSession.abortCaptures();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        mediaRecorder.stop();
        mediaRecorder.reset();
        startPreview();

        // pd.show();
//        path2 = getVideoFilePath();
//        VideoCompressor compressor = new VideoCompressor(MainActivity2.this);
//        compressor.compressVideo(path, path2);
//        Toast.makeText(MainActivity2.this, "Saved", Toast.LENGTH_SHORT).show();
//        UploadFileActivity.Path1 = path;
//        UploadFileActivity.Path2 = path2;
        path2 = getVideoFilePath();
        UploadFileActivity.Path1 = path;
        UploadFileActivity.Path2 = path2;
        // new CompressAndSaveTask().execute();
        finish();
        //comress();

        // startActivity(new Intent(this, MainActivity.class));
    }


    private class CompressAndSaveTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            UploadFileActivity.pregressBar.setVisibility(View.VISIBLE);
//            UploadFileActivity.recordVidep.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Example file paths, replace with actual file paths

            //path2 = getVideoFilePath();
            VideoCompressor compressor = new VideoCompressor(UploadFileActivity.activity);
            return compressor.compressVideo(path, path2);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(UploadFileActivity.activity, "Saved", Toast.LENGTH_SHORT).show();
                UploadFileActivity.pregressBar.setVisibility(View.GONE);
                UploadFileActivity.recordVidep.setVisibility(View.VISIBLE);
                UploadFileActivity.linear_video.setVisibility(View.VISIBLE);

                //UploadFileActivity.uploadVideo(r);
            } else {
                Toast.makeText(UploadFileActivity.activity, "Failed", Toast.LENGTH_SHORT).show();
            }


        }

    }


    private void comress() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // pd.dismiss();
                //  onBackPressed();
            }
        }).start();
    }

    private void closePreviewSession() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
