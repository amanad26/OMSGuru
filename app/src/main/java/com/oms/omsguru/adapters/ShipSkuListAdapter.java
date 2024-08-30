package com.oms.omsguru.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.SkuListLayoutBinding;
import com.oms.omsguru.databinding.SkuShipOrderLayoutBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.GetOrderModel;

import java.util.List;

public class ShipSkuListAdapter extends RecyclerView.Adapter<ShipSkuListAdapter.ViewHolder> {

    Context context;
    List<FetchDetailsModel.Result.Sku> models;


    public ShipSkuListAdapter(Context context, List<FetchDetailsModel.Result.Sku> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sku_ship_order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.skuCode.setText(models.get(position).getSkuCode());
        holder.binding.qty.setText(models.get(position).getQty());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SkuShipOrderLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SkuShipOrderLayoutBinding.bind(itemView);
        }
    }

}
