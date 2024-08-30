package com.oms.omsguru.ships;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oms.omsguru.dispatch.DispatchBarcodeMannualFragment;
import com.oms.omsguru.dispatch.DispatchBarcodeScanFragment;
import com.oms.omsguru.utils.ProductListInterface;

public class ShipPageChangeAdapter extends FragmentStateAdapter {

    Context context;
    ProductListInterface productListInterface;

    public ShipPageChangeAdapter(@NonNull FragmentActivity fm, Context context, ProductListInterface productListInterface) {
        super(fm);
        this.context = context;
        this.productListInterface = productListInterface;
    }

    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ShipBarcodeScanFragment(productListInterface);
        } else return new ShipBarcodeMannualFragment(productListInterface);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
