package com.oms.omsguru.return_processing;

import static com.oms.omsguru.return_processing.ReturnBarCodeScanFragment.showWaringDialog;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductListAdapter;
import com.oms.omsguru.databinding.FragmentReturnProcessingBinding;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReturnProcessingFragment extends Fragment implements ProductListInterface {

    FragmentReturnProcessingBinding binding;
    Activity activity;
    ProductListAdapter pAdapter;

    public static boolean isCompleted = false;
    public static String errorMessageFromApi = "";
    public static boolean isFailed = false;
    public static String barcode1 = "";
    public static boolean isSuccessButBacked = false;

    public static int selectedProcess = 1;

    String items[] = {"Scan Bar Code", "Manual Enter"};

    ArrayList<ProductListModel> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReturnProcessingBinding.inflate(getLayoutInflater());
        activity = requireActivity();

        ReturnProcessingPageChangeAdapter adapter = new ReturnProcessingPageChangeAdapter(requireActivity(), activity, this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(items[position]);
            }
        }).attach();

        pAdapter = new ProductListAdapter(activity, productList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        binding.oneByOne.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.markAsGood.setChecked(false);
                binding.markAsBad.setChecked(false);
                selectedProcess = 1;
            }
        });


        binding.markAsGood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.oneByOne.setChecked(false);
                binding.markAsBad.setChecked(false);
                selectedProcess = 2;
            }
        });

        binding.markAsBad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.oneByOne.setChecked(false);
                binding.markAsGood.setChecked(false);
                selectedProcess = 3;
            }
        });


        return binding.getRoot();
    }

    private void setUOSpinner() {


        List<String> footballPlayers = new ArrayList<>();

        footballPlayers.add("Process Manually One By One");
        footballPlayers.add("Mark Scanned Orders as Good Return");
        footballPlayers.add("Mark Scanned Orders as Bad Without Claim");


        binding.spinnerRetrunType.setItem(footballPlayers);


        binding.spinnerRetrunType.setSelection(0);

        binding.spinnerRetrunType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 1)
                    selectedProcess = 2;
                else if (position == 2)
                    selectedProcess = 3;
                else selectedProcess = 1;

                if (position == 2) {
                    boolean b = showWaringDialog(activity);

                } else {
                    ReturnBarCodeScanFragment.onChange();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        ReturnProcessingFragment.selectedProcess = 1;
        setUOSpinner();

        if (isSuccessButBacked) {
            failedDialog("Cancel By User", "-1", barcode1);
        }

        isSuccessButBacked = false;

        if (isCompleted) {
            sucessDialog("Successfully returned accepted");
        }

        isCompleted = false;

        if (isFailed) {
            failedDialog(errorMessageFromApi, "-2", barcode1);
        }
        isFailed = false;
        errorMessageFromApi = "";
        barcode1 = "";

    }

    private void failedDialog(String mess, String code, String barcode) {
        binding.viewPager.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.GONE);
        binding.ErrorLienar.setVisibility(View.VISIBLE);
        binding.dataLinear.setVisibility(View.VISIBLE);
        binding.errorCode.setText("Failed with code : " + code);
        binding.errorMessage.setText("Message : " + mess);
        binding.barCode.setText("Barcode : " + barcode);

        binding.okBtn.setOnClickListener(v -> {
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.dataLinear.setVisibility(View.GONE);
            binding.ErrorLienar.setVisibility(View.GONE);
            binding.successLienar.setVisibility(View.GONE);
            ReturnProcessingFragment.isSuccessButBacked = false;
            ReturnBarCodeScanFragment.isScannedNew = false;
        });

    }


    private void sucessDialog(String barCode) {
        binding.viewPager.setVisibility(View.GONE);
        binding.successLienar.setVisibility(View.VISIBLE);
        binding.ErrorLienar.setVisibility(View.GONE);
        binding.dataLinear.setVisibility(View.VISIBLE);

        binding.successMessage.setText(barCode);

        new Thread(() -> {
            try {
                sleep(UtilsClass.SleepTime);
                activity.runOnUiThread(() -> {
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.successLienar.setVisibility(View.GONE);
                    binding.ErrorLienar.setVisibility(View.GONE);
                    binding.dataLinear.setVisibility(View.GONE);
                    ReturnBarCodeScanFragment.isScannedNew = false;
                });


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    @Override
    public void onAdded(ProductListModel model, String code, String message) {
        productList.add(model);
    }
}