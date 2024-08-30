package com.oms.omsguru.validate_pack_order.SkuCheck;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentSkuCheckBarcodeMannualBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.UtilsClass;
import com.oms.omsguru.utils.ValidateInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkuCheckBarcodeMannualFragment extends Fragment {


    FragmentSkuCheckBarcodeMannualBinding binding;
    Activity activity;
    ProductListInterface productListInterface;
    int i = 1;
    ProgressDialog pd;
    Session session;
    ValidateInterface validateInterface;
    private List<GetOrderModel.Results.Sku> sks = new ArrayList<>();

    public SkuCheckBarcodeMannualFragment(ProductListInterface productListInterface, List<GetOrderModel.Results.Sku> sks, ValidateInterface validateInterface) {
        this.productListInterface = productListInterface;
        this.validateInterface = validateInterface;
        this.sks = sks;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSkuCheckBarcodeMannualBinding.inflate(getLayoutInflater());
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
                checkSkus(binding.barCodeEt.getText().toString());
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

        return binding.getRoot();

    }

    private void failAlert(String mes, String code) {
        StylishAlertDialog stylishDialog = new StylishAlertDialog(activity, StylishAlertDialog.ERROR);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.
                setTitleText("Failed Failed with code : " + code).setContentText("Message: " + mes).setCancellable(false)
                //.setCancelledOnTouchOutside(false)
                .setConfirmButton("Ok", StylishAlertDialog -> {
                    StylishAlertDialog.dismiss();
                })
                .show();
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
            validateInterface.onAdded(pos);
        } else {
            failAlert("Invalid SKU Code! ", "-2");
        }

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
                } else {
                    binding.barCodeEt.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

}

