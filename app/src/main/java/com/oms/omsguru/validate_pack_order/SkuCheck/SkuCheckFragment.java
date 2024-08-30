package com.oms.omsguru.validate_pack_order.SkuCheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.oms.omsguru.adapters.DisptchProductListAdapter;
import com.oms.omsguru.databinding.FragmentSkuCheckPackBinding;
import com.oms.omsguru.databinding.FragmentValidatePackBinding;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ViewAllProductActivity;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.validate_pack_order.ValidatePackOrderPageChangeAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class SkuCheckFragment extends Fragment implements ProductListInterface {

    FragmentSkuCheckPackBinding binding;
    Activity activity;
    DisptchProductListAdapter pAdapter;

    String items[] = {"Scan Bar Code", "Manual Enter"};

    ArrayList<ProductListModel> productList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSkuCheckPackBinding.inflate(getLayoutInflater());
        activity = requireActivity();

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(items[position])).attach();

        pAdapter = new DisptchProductListAdapter(activity, productList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.productRecycler.setLayoutManager(linearLayoutManager);
        binding.productRecycler.setAdapter(pAdapter);

        binding.viewAllTv.setOnClickListener(v -> {
            if (productList.size() != 0)
                startActivity(new Intent(activity, ViewAllProductActivity.class).putExtra("products", (Serializable) productList));
        });

        return binding.getRoot();
    }

    @Override
    public void onAdded(ProductListModel model, String code, String message) {
        productList.add(model);
        binding.productRecycler.setAdapter(pAdapter);
        binding.allProductLinar.setVisibility(View.VISIBLE);
    }
}