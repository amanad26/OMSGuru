package com.oms.omsguru;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.databinding.ActivityMainBinding;
import com.oms.omsguru.dispatch.DispatchBarcodeScanFragment;
import com.oms.omsguru.dispatch.DispatchFragment;
import com.oms.omsguru.getStartedActivities.ProfileActivity;
import com.oms.omsguru.getStartedActivities.SelectWherehouseTypeActivity;
import com.oms.omsguru.home.HomeFragment;
import com.oms.omsguru.return_processing.OfferListActivity;
import com.oms.omsguru.return_processing.ReturnBarCodeScanFragment;
import com.oms.omsguru.return_processing.ReturnProcessingFragment;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.ships.ShipFragment;
import com.oms.omsguru.utils.HomeInterface;
import com.oms.omsguru.utils.UtilsClass;
import com.oms.omsguru.validate_pack_order.ValidatePackBarcodeScanFragment;
import com.oms.omsguru.validate_pack_order.ValidatePackFragment;

public class MainActivity extends AppCompatActivity implements HomeInterface {

    ActivityMainBinding binding;
    Activity activity;
    Session session;

    boolean isHome = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);

        if (!session.getWarehouseName().equalsIgnoreCase(""))
            binding.warehouseTextview.setText(session.getWarehouseName());

        binding.icMenu.setOnClickListener(view -> binding.drawerLayout.open());

        if (!session.getEmail().equalsIgnoreCase("")) binding.userName.setText(session.getEmail());
        else binding.userName.setText("Login Please");

        binding.returnProcessingRelative.setOnClickListener(view -> {
            if (isChannelSelected()) drawerListener(binding.returnProcessing);
            else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();

        });
        binding.navHome.setOnClickListener(view -> {
            drawerListener(binding.navHome);

        });
        binding.shipOrder.setOnClickListener(view -> {
            if (isChannelSelected()) drawerListener(binding.shipOrder);
            else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();

        });
        binding.disptch.setOnClickListener(view -> {
            if (isChannelSelected()) drawerListener(binding.disptch);
            else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();

        });
        binding.packOrders.setOnClickListener(view -> {
            if (isChannelSelected()) drawerListener(binding.packOrders);
            else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();
        });
        binding.offers.setOnClickListener(view -> {
            if (isChannelSelected()) drawerListener(binding.offers);
            else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();

        });

        binding.logoutNav.setOnClickListener(v -> {
            logout();
        });

        binding.profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.close();
                startActivity(new Intent(activity, ProfileActivity.class));
            }
        });

        loadFrag(new HomeFragment(MainActivity.this));

        binding.warehouseTextview.setOnClickListener(v -> startActivity(new Intent(activity, SelectWherehouseTypeActivity.class)));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
            }


        } else {
            Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(activity, "Camera Permission Required..", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(activity, "Permission Required!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1000);
            }

        } else {
            Dexter.withContext(activity).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(activity, "Permission Required.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }
    }

    private void logout() {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.WARNING);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Logout").setContentText("Are you sure do you want to logout ?").setCancellable(false).setConfirmButton("Yes", StylishAlertDialog -> {
                    session.logout();
                    finish();
                }).setNeutralButton("No", StylishAlertDialog -> StylishAlertDialog.dismiss())
                //.setCancelledOnTouchOutside(false)
                .show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing storage
                Toast.makeText(activity, "Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Denied", Toast.LENGTH_SHORT).show();
                // Permission denied, show an error message or alternative flow
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close();
        }
        if (!isHome) {
            loadFrag(new HomeFragment(MainActivity.this));
            isHome = true;
        } else {
            StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.WARNING);
            stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            stylishDialog.setTitleText("Exit").setContentText("Are you sure do you want to exit ?").setCancellable(false).setConfirmButton("Yes", StylishAlertDialog -> {
                        super.onBackPressed();
                    }).setNeutralButton("No", StylishAlertDialog -> StylishAlertDialog.dismiss())
                    //.setCancelledOnTouchOutside(false)
                    .show();

        }

    }

    private void loadFrag(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(binding.fullscreenContainer.getId(), fragment);
        ft.commit();
    }

    private void drawerListener(TextView item) {
        isHome = false;
        if (item.getId() == binding.returnProcessing.getId()) {
            loadFrag(new ReturnProcessingFragment());
            isHome = false;
        } else if (item.getId() == binding.navHome.getId()) {
            loadFrag(new HomeFragment(MainActivity.this));
            isHome = true;
        } else if (item.getId() == binding.shipOrder.getId()) {
//            startActivity(new Intent(activity, PutWaysListActivity.class));
            loadFrag(new ShipFragment());
            isHome = false;
        } else if (item.getId() == binding.packOrders.getId()) {
            loadFrag(new ValidatePackFragment());
            isHome = false;
        } else if (item.getId() == binding.offers.getId()) {
            startActivity(new Intent(activity, OfferListActivity.class));
            isHome = false;
        } else if (item.getId() == binding.disptch.getId()) {
            loadFrag(new DispatchFragment());
            isHome = false;
        }
        binding.drawerLayout.close();
    }

    @Override
    public void onClick(int number) {
        if (isChannelSelected()) {
            if (number == 2) {
                binding.shipOrder.performClick();
                isHome = false;
            } else if (number == 4) {
                binding.disptch.performClick();
            } else if (number == 3) {
                isHome = false;
                binding.packOrders.performClick();
            } else {
                isHome = false;
                loadFrag(new ReturnProcessingFragment());
            }
        } else Toast.makeText(activity, "Please select channel", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSkuSelect() {


    }

    @Override
    public void onValidatePackSelect() {


    }


    private boolean isChannelSelected() {
        return !UtilsClass.channelId.equalsIgnoreCase("");
    }
}