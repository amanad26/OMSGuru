package com.oms.omsguru.validate_pack_order;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oms.omsguru.ships.ShipBarcodeMannualFragment;
import com.oms.omsguru.ships.ShipBarcodeScanFragment;
import com.oms.omsguru.utils.ProductListInterface;

public class ValidatePackOrderPageChangeAdapter extends FragmentStateAdapter {

    Context context;
    ProductListInterface productListInterface;



    public ValidatePackOrderPageChangeAdapter(@NonNull FragmentActivity fm, Context context, ProductListInterface productListInterface) {
        super(fm);
        this.context = context;
        this.productListInterface = productListInterface;
    }

    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ValidatePackBarcodeScanFragment(productListInterface);
        } else return new ValidatePackBarcodeMannualFragment(productListInterface);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
