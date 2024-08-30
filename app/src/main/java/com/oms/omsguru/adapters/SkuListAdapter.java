package com.oms.omsguru.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.DisptchListLaoutBinding;
import com.oms.omsguru.databinding.SkuListLayoutBinding;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ReturnProductDetailsActivity;
import com.oms.omsguru.utils.SkuInterface;

import java.util.ArrayList;
import java.util.List;

public class SkuListAdapter extends RecyclerView.Adapter<SkuListAdapter.ViewHolder> {

    Context context;
    List<GetOrderModel.Results.Sku> models;
    SkuInterface skuInterface ;

    public SkuListAdapter(Context context, List<GetOrderModel.Results.Sku> models, SkuInterface skuInterface) {
        this.context = context;
        this.models = models;
        this.skuInterface = skuInterface ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sku_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.skuCode.setText(models.get(position).getSkuCode());
        holder.binding.qty.setText(models.get(position).getQty());

        if (models.get(position).isScanned()) {
            holder.binding.sataus.setVisibility(View.VISIBLE);
            holder.binding.qty.setText("1");
            holder.binding.mainLinear.setBackgroundColor(context.getResources().getColor(R.color.green_light));
        } else {
            holder.binding.sataus.setVisibility(View.INVISIBLE);
            holder.binding.mainLinear.setBackgroundColor(context.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SkuListLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SkuListLayoutBinding.bind(itemView);
        }
    }

}
