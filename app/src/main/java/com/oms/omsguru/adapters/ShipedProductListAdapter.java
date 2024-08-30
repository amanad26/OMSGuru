package com.oms.omsguru.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ShipListLayoutBinding;
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ReturnProductDetailsActivity;
import com.oms.omsguru.ships.DetailProductForAllActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class ShipedProductListAdapter extends RecyclerView.Adapter<ShipedProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductListModel> models;
    int i;


    public ShipedProductListAdapter(Context context, ArrayList<ProductListModel> models, int i) {
        this.context = context;
        this.models = models;
        this.i = i;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ship_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tracker.setText(models.get(position).getShipment_traker());
        holder.binding.invoice.setText(models.get(position).getData().getOrderId());

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ReturnProductDetailsActivity.class)));

        if (i == 1) {
            holder.binding.layoutDetails.setVisibility(View.VISIBLE);
            holder.binding.skhRecycler.setLayoutManager(new LinearLayoutManager(context));
            holder.binding.skhRecycler.setAdapter(new ShipSkuListAdapter(context, models.get(position).getSkuList()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailProductForAllActivity.class)
                        .putExtra("data", (Serializable) models.get(position)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ShipListLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ShipListLayoutBinding.bind(itemView);
        }
    }

}
