package com.oms.omsguru.return_processing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductDetailsSkuListAdapter;
import com.oms.omsguru.databinding.ActivityReturnProductDetailsBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.utils.SkuInterface;
import com.oms.omsguru.utils.UtilsClass;

import java.util.ArrayList;
import java.util.List;

public class ReturnProductDetailsActivity extends AppCompatActivity implements SkuInterface {

    ActivityReturnProductDetailsBinding binding;
    Activity activity;
    boolean isAccepted = true;

    FetchDetailsModel.Result data = null;
    String selectedCurrReturn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReturnProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;


        data = (FetchDetailsModel.Result) getIntent().getSerializableExtra("product");
        if (data != null) {

            binding.invioceId.setText(data.getInvoiceId());
            binding.subOrderId.setText(data.getSubOrderId());
            binding.productName.setText(data.getProductName());
            binding.coustomerName.setText(data.getCustomerName());
            binding.qty.setText(data.getQty());
            binding.invioceAmount.setText(data.getAmount());
            binding.invioceDate.setText(data.getOrderDate());
            if (!data.getOrderDate().equalsIgnoreCase("")) {
                binding.invioceDate.setText(UtilsClass.convertDateFormat(data.getOrderDate()));
            }

            binding.sattlementAmount.setText(data.getSettlementAmount());
            binding.status.setText(data.getStatus());
            binding.daySinceShiped.setText(String.valueOf(data.getDaysSinceShipped()));
            binding.returnReason.setText(String.valueOf(data.getReturnReason()));
            binding.subReason.setText(String.valueOf(data.getReturnSubReason()));
            binding.comments.setText(String.valueOf(data.getReturnComments()));

            binding.skuRecler.setLayoutManager(new LinearLayoutManager(activity));
            binding.skuRecler.setAdapter(new ProductDetailsSkuListAdapter(activity, data.getSku(), this, 1));

        }


        binding.acceptReturn.setChecked(true);
        binding.acceptReturn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.fileClaim.setChecked(false);
                binding.clamLinear.setVisibility(View.GONE);
                isAccepted = true;
            } else {
                binding.clamLinear.setVisibility(View.GONE);
            }
        });
        binding.fileClaim.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.acceptReturn.setChecked(false);
                binding.clamLinear.setVisibility(View.VISIBLE);
                isAccepted = false;
            } else {
                binding.clamLinear.setVisibility(View.GONE);
            }
        });

        binding.icBack.setOnClickListener(v -> onBackPressed());

        setupSpinner();

    }

    private void setupSpinner() {

        List<String> footballPlayers = new ArrayList<>();
        footballPlayers.add("Currier Return - RTO");
        footballPlayers.add("Customer Return - RTV");

        binding.spinnerRetrunType.setItem(footballPlayers);

        binding.spinnerRetrunType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCurrReturn = footballPlayers.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    @Override
    public void onChange(boolean val, int pos) {

    }
}