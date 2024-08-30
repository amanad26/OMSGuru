package com.oms.omsguru.ships;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;

import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductDetailsSkuListAdapter;
import com.oms.omsguru.adapters.ShipSkuListAdapter;
import com.oms.omsguru.databinding.ActivityDetailProductForAllBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.UtilsClass;

public class DetailProductForAllActivity extends AppCompatActivity {

    ActivityDetailProductForAllBinding binding;
    Activity activity;
    Session session;
    ProductListModel productListModel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductForAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        binding.icBack.setOnClickListener(v -> finish());

        productListModel = (ProductListModel) getIntent().getSerializableExtra("data");


        if (productListModel != null) {


            FetchDetailsModel.Result data = productListModel.getData();
            if (data != null) {

                binding.invioceId.setText(data.getInvoiceId());
                binding.orderId.setText(data.getOrderId());
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
                binding.skuRecler.setAdapter(new ShipSkuListAdapter(activity, data.getSku()));

            }

        }

    }


}