package com.oms.omsguru.return_processing;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oms.omsguru.utils.ProductListInterface;

public class ReturnProcessingPageChangeAdapter extends FragmentStateAdapter {

    Context context;
    ProductListInterface productListInterface;

    public ReturnProcessingPageChangeAdapter(@NonNull FragmentActivity fm, Context context, ProductListInterface productListInterface) {
        super(fm);
        this.context = context;
        this.productListInterface = productListInterface;
    }

    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ReturnBarCodeScanFragment(productListInterface);
        } else return new ReturnBarCodeManualFragment(productListInterface);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
