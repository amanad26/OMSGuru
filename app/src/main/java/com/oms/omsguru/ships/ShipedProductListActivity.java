package com.oms.omsguru.ships;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;

import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductListAdapter;
import com.oms.omsguru.adapters.ShipedProductListAdapter;
import com.oms.omsguru.databinding.ActivityShipedProductListBinding;
import com.oms.omsguru.databinding.ActivityViewAllProductBinding;
import com.oms.omsguru.models.ProductListModel;

import java.util.ArrayList;

public class ShipedProductListActivity extends AppCompatActivity {

    ActivityShipedProductListBinding binding;
    Activity activity;
    ArrayList<ProductListModel> models = new ArrayList<>();
    ShipedProductListAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShipedProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        models = (ArrayList<ProductListModel>) getIntent().getSerializableExtra("products");
        binding.icBack.setOnClickListener(v -> finish());

        pAdapter = new ShipedProductListAdapter(activity, models, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(pAdapter);

    }
}