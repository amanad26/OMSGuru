package com.oms.omsguru.home;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.leo.searchablespinner.SearchableSpinner;
import com.leo.searchablespinner.interfaces.OnItemSelectListener;
import com.oms.omsguru.databinding.FragmentHomeBinding;
import com.oms.omsguru.dispatch.DispatchBarcodeScanFragment;
import com.oms.omsguru.models.VerifyOtpModel;
import com.oms.omsguru.return_processing.ReturnBarCodeScanFragment;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.HomeInterface;
import com.oms.omsguru.utils.UtilsClass;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    HomeInterface homeInterface;
    FragmentHomeBinding binding;
    Activity activity;
    Session session;
    VerifyOtpModel.Results results = null;
    ArrayList<String> chanelNames = new ArrayList<>();
    ArrayList<String> chanelIds = new ArrayList<>();

    String selectedChanelId = "";

    SearchableSpinner searchableSpinner;

    public HomeFragment(HomeInterface homeInterface) {
        this.homeInterface = homeInterface;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        activity = requireActivity();
        session = new Session(activity);

        results = session.getVerifyModel(activity);
        searchableSpinner = new SearchableSpinner(activity);
        if (results != null) {
            setChannelSpinner();

            setUpSearchbleSpinner();
            binding.searchRelative.setOnClickListener(v -> searchableSpinner.show());

        }


        binding.startProcessing.setOnClickListener(v -> homeInterface.onClick(1));

        binding.dispacth.setOnClickListener(v -> homeInterface.onClick(4));
        binding.validateAndPack.setOnClickListener(v -> homeInterface.onClick(3));

        binding.shipOrders.setOnClickListener(v -> homeInterface.onClick(2));

        String versionName = getVersionName(activity);
        // You can now use the versionName variable as needed
        binding.versionName.setText(versionName);

        return binding.getRoot();
    }

    private void setUpSearchbleSpinner() {
        chanelIds.clear();
        chanelNames.clear();
        for (int i = 0; i < results.getChannels().size(); i++) {
            chanelIds.add(String.valueOf(results.getChannels().get(i).getId()));
            chanelNames.add(results.getChannels().get(i).getName());
        }

        searchableSpinner.setWindowTitle("Select Channel ");
        searchableSpinner.setSpinnerListItems(chanelNames);

        searchableSpinner.setOnItemSelectListener((i, s) -> {
            selectedChanelId = chanelIds.get(i);
            UtilsClass.channelId = selectedChanelId;
            binding.searchRelative.setText(s);
        });

        if (UtilsClass.channelId.equalsIgnoreCase("0")) {
            try {
                binding.searchRelative.setText(chanelNames.get(0));
            } catch (Exception e) {
                Log.e("TAG", "setChannelSpinner: ");
            }
        }


    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void setChannelSpinner() {

        chanelIds.clear();
        chanelNames.clear();
        for (int i = 0; i < results.getChannels().size(); i++) {
            chanelIds.add(String.valueOf(results.getChannels().get(i).getId()));
            chanelNames.add(results.getChannels().get(i).getName());
        }

        ArrayAdapter<String> AreaAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_spinner_item, chanelNames);
        AreaAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerChennel.setHint("Select Channel");
        binding.spinnerChennel.setAdapter(AreaAdapter);

        binding.spinnerChennel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChanelId = chanelIds.get(position);
                UtilsClass.channelId = selectedChanelId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (UtilsClass.channelId.equalsIgnoreCase("0")) {
            try {
                binding.spinnerChennel.setSelection(0);
            } catch (Exception e) {
                Log.e("TAG", "setChannelSpinner: ");
            }
        }

        Log.e("TAG", "setChannelSpinner: All Data" + results.getChannels().toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        ReturnBarCodeScanFragment.isScannedNew = false;
        DispatchBarcodeScanFragment.isScannedDispacthed = false;
    }
}