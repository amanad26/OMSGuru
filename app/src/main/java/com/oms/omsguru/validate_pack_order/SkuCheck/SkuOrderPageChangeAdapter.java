package com.oms.omsguru.validate_pack_order.SkuCheck;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.utils.ProductListInterface;
import com.oms.omsguru.utils.ValidateInterface;
import com.oms.omsguru.validate_pack_order.ValidatePackBarcodeMannualFragment;
import com.oms.omsguru.validate_pack_order.ValidatePackBarcodeScanFragment;

import java.util.List;

public class SkuOrderPageChangeAdapter extends FragmentStateAdapter {

    Context context;
    ProductListInterface productListInterface;
    List<GetOrderModel.Results.Sku> sks;
    ValidateInterface validateInterface;

    public SkuOrderPageChangeAdapter(@NonNull FragmentActivity fm, Context context, ProductListInterface productListInterface, List<GetOrderModel.Results.Sku> skus, ValidateInterface validateInterface) {
        super(fm);
        this.context = context;
        this.productListInterface = productListInterface;
        this.sks = skus;
        this.validateInterface = validateInterface;
    }

    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SkuCheckBarcodeScanFragment(productListInterface, sks, validateInterface);
        } else
            return new SkuCheckBarcodeMannualFragment(productListInterface, sks, validateInterface);


    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
