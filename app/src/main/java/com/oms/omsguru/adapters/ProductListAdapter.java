package com.oms.omsguru.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ProductListLayoutBinding;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ReturnProductDetailsActivity;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductListModel> models;


    public ProductListAdapter(Context context, ArrayList<ProductListModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.qty.setText(String.valueOf(models.get(position).getQty()));
        holder.binding.rackSpace.setText(models.get(position).getRackSpace());
        holder.binding.zone.setText(models.get(position).getZone());
        holder.binding.skuName.setText(models.get(position).getSkuName());
        holder.binding.invoice.setText(models.get(position).getInvoice());
        holder.binding.date.setText(models.get(position).getDate());


        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ReturnProductDetailsActivity.class)));

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ProductListLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductListLayoutBinding.bind(itemView);
        }
    }

}
