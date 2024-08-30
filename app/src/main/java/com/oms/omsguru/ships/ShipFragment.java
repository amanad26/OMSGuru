package com.oms.omsguru.ships;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.oms.omsguru.adapters.DisptchProductListAdapter;
import com.oms.omsguru.adapters.ProductListAdapter;
import com.oms.omsguru.adapters.ShipedProductListAdapter;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentDispatchBinding;
import com.oms.omsguru.databinding.FragmentShiphBinding;
import com.oms.omsguru.dispatch.DispatchPageChangeAdapter;
import com.oms.omsguru.models.DispatchListModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ViewAllProductActivity;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipFragment extends Fragment implements ProductListInterface {

    FragmentShiphBinding binding;
    Activity activity;
    ShipedProductListAdapter pAdapter;

    String items[] = {"Scan Bar Code", "Manual Enter"};

    ArrayList<ProductListModel> productList = new ArrayList<>();
    Session session;
    private List<DispatchListModel.Result> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShiphBinding.inflate(getLayoutInflater());
        activity = requireActivity();
        session = new Session(activity);

        ShipPageChangeAdapter adapter = new ShipPageChangeAdapter(requireActivity(), activity, this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(items[position])).attach();

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

        getShippedList();
        return binding.getRoot();
    }

    @Override
    public void onAdded(ProductListModel model, String code, String message) {
        productList.add(model);
        binding.productRecycler.setAdapter(pAdapter);
        binding.allProductLinar.setVisibility(View.VISIBLE);
        binding.scanned.setText(String.valueOf(productList.size()));
    }


    private void getShippedList() {

        RetrofitClient.getClient(activity).getDispatchList(
                "list_ship_orders",
                session.getWarehouseId(),
                session.getCompanyId(),
                UtilsClass.channelId,
                session.getUserId(),
                session.getAuth_code(),
                session.getPermission_code()
        ).enqueue(new Callback<DispatchListModel>() {
            @Override
            public void onResponse(@NonNull Call<DispatchListModel> call, @NonNull Response<DispatchListModel> response) {
                binding.scanProgress.setVisibility(View.GONE);
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getSuccess()) {
                            data = response.body().getResults();
                            binding.total.setText(String.valueOf(data.size()));
                            binding.scanned.setText(String.valueOf(productList.size()));
                        }
            }

            @Override
            public void onFailure(@NonNull Call<DispatchListModel> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Server not responding!", Toast.LENGTH_SHORT).show();
                binding.scanProgress.setVisibility(View.GONE);
            }
        });


    }


}