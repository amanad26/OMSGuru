package com.oms.omsguru.return_processing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductListAdapter;
import com.oms.omsguru.databinding.ActivityViewAllProductBinding;
import com.oms.omsguru.models.ProductListModel;

import java.util.ArrayList;

public class ViewAllProductActivity extends AppCompatActivity {

    ActivityViewAllProductBinding binding;
    Activity activity;

    ArrayList<ProductListModel> models = new ArrayList<>();
    ProductListAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        models = (ArrayList<ProductListModel>) getIntent().getSerializableExtra("products");
        binding.icBack.setOnClickListener(v -> finish());

        pAdapter = new ProductListAdapter(activity, models);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(pAdapter);


    }
}