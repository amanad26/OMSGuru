package com.oms.omsguru.return_processing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.MainActivity;
import com.oms.omsguru.MainActivity2;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.BaseUrls;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.models.FileModel;
import com.oms.omsguru.models.PresignedUrlModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.FileUtils;
import com.oms.omsguru.utils.ImageCompression;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UnslectedImageInteface;
import com.oms.omsguru.utils.UtilsClass;
import com.oms.omsguru.utils.VideoCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class UploadFileActivity extends AppCompatActivity implements UnslectedImageInteface {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView;
    private String presignedUrl = "";
    public static String videUrl = "";
    String key;
    boolean isDone = false;
    boolean isVideoAdded = false;
    public static String Path1 = "";
    public static String Path2 = "";
    Session session;
    ArrayList<FileModel> models = new ArrayList<>();
    ArrayList<FileModel> models2 = new ArrayList<>();
    ArrayList<FileModel> deletedModels = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();
    ArrayList<File> files2 = new ArrayList<>();
    RecyclerView images_recyler;
    TextView total, total_videos;
    ClickedImagesAdapter adapter;
    private Bitmap bitmap;
    File image;
    ProgressDialog pd;
    public static Button recordVidep;
    public Button downloadVideo;
    public static Activity activity;
    private CountDownTimer countDownTimer;

    File large = null, small = null;

    Map<String, String> params = new HashMap();
    private String id = "";
    public static LinearLayout linear_video;
    public static ProgressBar pregressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        activity = this;

        session = new Session(activity);

        imageView = findViewById(R.id.imageViewNew);
        images_recyler = findViewById(R.id.images_recyler);
        recordVidep = findViewById(R.id.recordVidep);
        total = findViewById(R.id.total_images);
        downloadVideo = findViewById(R.id.downloadVideo);
        linear_video = findViewById(R.id.linear_video);
        pregressBar = findViewById(R.id.pregressBar);
        Button buttonChoose = findViewById(R.id.chooseBtn);
        Button buttonUpload = findViewById(R.id.submitBtn);
        pd = new ProgressDialog(UploadFileActivity.this);

        ImageView ic_back = findViewById(R.id.ic_back);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files2.size() != 0) {
                    FileUtils.copyFileToDownloads(activity, files2.get(0), files2.get(0).getName());
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (files2.size() == 0) {
                recordVidep.setOnClickListener(v -> {
                    if (isVideoAdded) {
                        showDeleteVideo();
                    } else {
                        startActivity(new Intent(UploadFileActivity.this, MainActivity2.class));
                    }
                });
            }
        } else {
            Dexter.withContext(activity).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    if (files2.size() == 0) {
                        recordVidep.setOnClickListener(v -> {
                            if (isVideoAdded) {
                                showDeleteVideo();
                            } else {
                                startActivity(new Intent(UploadFileActivity.this, MainActivity2.class));
                            }
                        });
                    }

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(activity, "Permission is required!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }

        buttonChoose.setOnClickListener(v -> {
            if (files.size() <= 10) openFileChooser();
        });

        if (getIntent() != null) {
            params = (Map<String, String>) getIntent().getSerializableExtra("data");
            id = getIntent().getStringExtra("id");
        }

        Log.e("TAG", "onResponse: All Params " + params);

        requestStoragePermission();

        adapter = new ClickedImagesAdapter(files, UploadFileActivity.this, UploadFileActivity.this);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(UploadFileActivity.this, 3);
        images_recyler.setLayoutManager(linearLayoutManager);
        images_recyler.setAdapter(adapter);

        total.setText(String.valueOf(files.size()));

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() != 0) {
                    if (files.size() >= 2) {
                        acceptRetutn();
                    } else {
                        Toast.makeText(UploadFileActivity.this, "Not enough photos selected. Must select at least 2", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadFileActivity.this, "Not enough photos selected. Must select at least 2", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDeleteVideo() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.WARNING);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Delete!").setContentText("Are you sure do you want to delete video ?").setCancellable(false).setNeutralButton("No", StylishAlertDialog -> StylishAlertDialog.dismiss()).setConfirmButton("Yes", new StylishAlertDialog.OnStylishClickListener() {
            @Override
            public void onClick(StylishAlertDialog StylishAlertDialog) {
                for (int i = 0; i < models2.size(); i++) {
                    if (models2.get(i).isVideo()) {
                        deletedModels.add(models2.get(i));
                        models2.remove(i);
                    }
                }
                isVideoAdded = false;
                files2.clear();
                recordVidep.setText("Record Video");
                linear_video.setVisibility(View.GONE);
                stylishDialog.dismiss();
            }
        }).show();
    }

    public static String getMimeTypeFromFile(File file) {
        String extension = getFileExtension(file);
        return extension != null ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) : null;
    }


    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? null : name.substring(lastIndexOf + 1).toLowerCase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Path1.equalsIgnoreCase("")) {
            large = new File(Path1);
        }

        if (!Path2.equalsIgnoreCase("")) {
            small = new File(Path2);
        }

        if (large != null && small != null) {
//            videoWaitForUpload(small);
            if (!Path1.equalsIgnoreCase("") && !Path2.equalsIgnoreCase("")) {
                pregressBar.setVisibility(View.VISIBLE);
                recordVidep.setVisibility(View.GONE);
                new CompressAndSaveTask().execute();
            }

        }

        Path1 = "";
        Path2 = "";

    }

//    private void videoWaitForUpload(File small) {
//
//
//
//    }


    public static void showFullScreenImageDialog(File file, Context context) {
        // Create a new dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.full_screen_image);

        // Set the dialog to be full screen
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Get the ImageView from the dialog layout
        ImageView fullScreenImageView = dialog.findViewById(R.id.full_screen_image_view);

        // Load the image using Glide or any other image loading library
        Glide.with(context).load(file) // Use imageResourceId for drawable or URL for images from the web
                .into(fullScreenImageView);

        // Show the dialog
        dialog.show();

        // Optional: Set a click listener on the full-screen image to close the dialog
        fullScreenImageView.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Path1 = "";
        Path2 = "";

        if (large != null) {
            if (large.exists()) {
                if (large.delete()) {
                    Log.e("TAG", "onDestroy: Delete");
                }
            }
        }

        if (small != null) {
            if (small.exists()) {
                if (small.delete()) Log.e("TAG", "onDestroy: Deleted ");
            }
        }

    }

    private void acceptRetutn() {
        pd.show();

        if (models.size() != 0) {
            for (int i = 0; i < models.size(); i++) {
                FileModel fm = models.get(i);
                params.put("data[uploads][" + i + "][key]", fm.getKey());
                params.put("data[uploads][" + i + "][name]", fm.getName());
                params.put("data[uploads][" + i + "][mimetype]", getMimeTypeFromFile(fm.getFile()));
                params.put("data[uploads][" + i + "][size]", String.valueOf(models.get(i).getFile().length()));
            }
        }

        if (models2.size() != 0) {
            for (int i = 0; i < models2.size(); i++) {
                FileModel fm = models2.get(i);
                params.put("data[uploads][" + (i + models.size()) + "][key]", fm.getKey());
                params.put("data[uploads][" + (i + models.size()) + "][name]", fm.getName());
                params.put("data[uploads][" + (i + models.size()) + "][mimetype]", getMimeTypeFromFile(fm.getFile()));
                params.put("data[uploads][" + (i + models.size()) + "][size]", String.valueOf(models2.get(i).getFile().length()));
            }
        }

        if (deletedModels.size() != 0) {
            for (int i = 0; i < deletedModels.size(); i++) {
                FileModel fm = deletedModels.get(i);
                params.put("data[deletedPhotos][" + i + "][key]", fm.getKey());
                params.put("data[deletedPhotos][" + i + "][name]", fm.getName());
                params.put("data[deletedPhotos][" + i + "][mimetype]", getMimeTypeFromFile(fm.getFile()));
                params.put("data[deletedPhotos][" + i + "][size]", String.valueOf(deletedModels.get(i).getFile().length()));
            }
        }

        Log.e("TAG", "acceptRetutn: All Params " + params);

        pd.show();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, BaseUrls.BASE_URL + BaseUrls.VALIDATE_PACK_DETAIL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                JSONObject jsonObject = null;
                try {
                    pd.dismiss();
                    jsonObject = new JSONObject(response);
                    Log.e("TAG", "onResponse: Response " + response);
                    int code = jsonObject.getInt("code");

                    if (code == 0) {
                        Log.e("TAG", "onResponse: Response " + response);
                        Toast.makeText(UploadFileActivity.this, "Successfully returned", Toast.LENGTH_SHORT).show();
                        ReturnProcessingFragment.isCompleted = true;
                        finish();
                    } else {
                        ReturnProcessingFragment.errorMessageFromApi = jsonObject.getString("message");
                        ReturnProcessingFragment.isFailed = true;
                        finish();
                        Toast.makeText(UploadFileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UploadFileActivity.this, "Unexpected Response ", Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> {
            pd.dismiss();
            ReturnProcessingFragment.errorMessageFromApi = error.getMessage();
            ReturnProcessingFragment.isFailed = true;
            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onErrorResponse: " + error.toString());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("TAG", "getParams: All Parametrs " + params);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(UploadFileActivity.this);
        requestQueue.add(stringRequest);

    }

    private void openFileChooser() {
        ImagePicker.with(this).crop()                    //Crop image(Optional), Check Customization for more option
                .compress(200).cameraOnly()//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Get the file path of the selected image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image = bitmapToFile(UploadFileActivity.this, bitmap);
                files.add(image);
                adapter = new ClickedImagesAdapter(files, UploadFileActivity.this, UploadFileActivity.this);
                images_recyler.setAdapter(adapter);
                total.setText(String.valueOf(files.size()));
                getPresignedUrl(image, 0);
                //Toast.makeText(this, files.size() + "", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPresignedUrl(File file, int i) {
        //pd.show();
        RetrofitClient.getClient(UploadFileActivity.this).getPresignedUrl("get_presigned_url", session.getWarehouseId(), session.getCompanyId(), id, "png", UtilsClass.channelId, session.getUserId(), session.getAuth_code(), session.getPermission_code()).enqueue(new Callback<PresignedUrlModel>() {
            @Override
            public void onResponse(Call<PresignedUrlModel> call, retrofit2.Response<PresignedUrlModel> response) {
                // pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null) if (response.body().getSuccess()) {
                        String key = response.body().getData().getKey();
                        String[] parts = key.split("/");

                        try {
                            Log.e("TAG", "onResponse: Mix " + key);
                            Log.e("TAG", "onResponse: Key " + parts[0]);
                            Log.e("TAG", "onResponse: Name " + parts[1]);

                            FileModel m = new FileModel(key, parts[1], file);

                            if (i == 0) {
                                models.add(m);
                                uploadImage(file, response.body().getData().getUrl());
                                Log.e("TAG", "onRemove: Files " + files.toString());
                                Log.e("TAG", "onRemove: Files Models  " + models.toString());
                            } else {
                                models2.add(m);
                                m.setVideo(true);
                                videUrl = response.body().getData().getUrl();

                                uploadVideo(response.body().getData().getUrl(), file);
                            }
                        } catch (Exception e) {
                            Toast.makeText(UploadFileActivity.this, "Invalid Key", Toast.LENGTH_SHORT).show();
                        }

                    }
            }

            @Override
            public void onFailure(Call<PresignedUrlModel> call, Throwable t) {
                pd.dismiss();
            }
        });

    }


    public static File bitmapToFile(Context mContext, Bitmap bitmap) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(mContext.getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImage(File pFile, String url) {

        //imageView.setImageURI(Uri.fromFile(image));
        File file = new File(pFile.getAbsolutePath());
        byte[] fileBytes = new byte[(int) file.length()];
        try (InputStream inputStream = new FileInputStream(file)) {
            inputStream.read(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        // Create RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), fileBytes);

        // Create Request
        Request request = new Request.Builder().url(url).put(requestBody).build();

        // Execute request
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("TAG", "onFailure: Failed " + e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e("TAG", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()) {
                    // Handle success
                    runOnUiThread(() -> {
                        if (response.code() == 200)
                            Toast.makeText(UploadFileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(UploadFileActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    });
                } else {
                    // Handle failure
                    runOnUiThread(() -> {
                        Toast.makeText(UploadFileActivity.this, "Failed : " + response.toString(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }


    public void uploadVideo(String url, File videoFile) {
//
        //pd.show();
        // Create a request body with the video file

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(videoFile, MediaType.parse("video/*"));

        // Create a PUT request to upload the file to S3 using the pre-signed URL
        Request request = new Request.Builder().url(url).put(requestBody).build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //pd.dismiss();
                e.printStackTrace();
                Log.e("TAG", "onFailure: Failed " + e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e("TAG", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                //pd.dismiss();
                videUrl = "";
                if (response.isSuccessful()) {
                    // Handle success
                    Log.e("TAG", "onResponse: Success " + response);
//
                } else {

                }
            }
        });

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    @Override
    public void onRemove(int pos) {
        Log.e("TAG", "onRemove: pos " + pos);
        Log.e("TAG", "onRemove: size" + files.size());
        Log.e("TAG", "onRemove:File model size" + models.size());

        for (int i = 0; i < files.size(); i++) {
            if (i == pos) {
                files.remove(pos);
                FileModel fileModel = models.get(pos);
                models.remove(pos);
                deletedModels.add(fileModel);
                total.setText(String.valueOf(files.size()));
                adapter.notifyDataSetChanged();
                break;
            }
        }

    }

    public class CompressAndSaveTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            VideoCompressor compressor = new VideoCompressor(activity);
            return compressor.compressVideo(large.getAbsolutePath(), small.getAbsolutePath());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                UploadFileActivity.pregressBar.setVisibility(View.GONE);
                UploadFileActivity.recordVidep.setVisibility(View.VISIBLE);
                UploadFileActivity.linear_video.setVisibility(View.VISIBLE);
                getPresignedUrl(small, 1);
            } else {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
            }


        }

    }

}