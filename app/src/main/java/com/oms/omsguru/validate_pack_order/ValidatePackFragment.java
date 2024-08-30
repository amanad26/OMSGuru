package com.oms.omsguru.validate_pack_order;

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

import com.oms.omsguru.adapters.ShipedProductListAdapter;
import com.oms.omsguru.databinding.FragmentValidatePackBinding;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ViewAllProductActivity;
import com.oms.omsguru.ships.ShipPageChangeAdapter;
import com.oms.omsguru.ships.ShipedProductListActivity;
import com.oms.omsguru.utils.ProductListInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class ValidatePackFragment extends Fragment implements ProductListInterface {

    FragmentValidatePackBinding binding;
    Activity activity;
    ShipedProductListAdapter pAdapter;
    String items[] = {"Scan Bar Code", "Manual Enter"};

    ArrayList<ProductListModel> productList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentValidatePackBinding.inflate(getLayoutInflater());
        activity = requireActivity();

        ValidatePackOrderPageChangeAdapter adapter = new ValidatePackOrderPageChangeAdapter(requireActivity(), activity, this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(items[position]);
            }
        }).attach();

        pAdapter = new ShipedProductListAdapter(activity, productList, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.productRecycler.setLayoutManager(linearLayoutManager);
        binding.productRecycler.setAdapter(pAdapter);

        binding.viewAllTv.setOnClickListener(v -> {
            if (productList.size() != 0)
                startActivity(new Intent(activity, ShipedProductListActivity.class).putExtra("products", (Serializable) productList));
        });

        return binding.getRoot();
    }

    @Override
    public void onAdded(ProductListModel model, String code, String message) {
        productList.add(model);
        binding.productRecycler.setAdapter(pAdapter);
        binding.allProductLinar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}