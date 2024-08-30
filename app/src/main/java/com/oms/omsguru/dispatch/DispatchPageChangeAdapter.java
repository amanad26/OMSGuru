package com.oms.omsguru.dispatch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oms.omsguru.return_processing.ReturnBarCodeManualFragment;
import com.oms.omsguru.return_processing.ReturnBarCodeScanFragment;
import com.oms.omsguru.utils.ProductListInterface;

public class DispatchPageChangeAdapter extends FragmentStateAdapter {

    Context context;
    ProductListInterface productListInterface;

    public DispatchPageChangeAdapter(@NonNull FragmentActivity fm, Context context, ProductListInterface productListInterface) {
        super(fm);
        this.context = context;
        this.productListInterface = productListInterface;
    }

    public Fragment createFragment(int position) {
        if (position == 0) {
            return new DispatchBarcodeScanFragment(productListInterface);
        } else return new DispatchBarcodeMannualFragment(productListInterface);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
