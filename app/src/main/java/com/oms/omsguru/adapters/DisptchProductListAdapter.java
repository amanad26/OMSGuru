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
import com.oms.omsguru.models.ProductListModel;
import com.oms.omsguru.return_processing.ReturnProductDetailsActivity;

import java.util.ArrayList;

public class DisptchProductListAdapter extends RecyclerView.Adapter<DisptchProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductListModel> models;


    public DisptchProductListAdapter(Context context, ArrayList<ProductListModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ship_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tracker.setText(models.get(position).getShipment_traker());
        holder.binding.invoice.setText(models.get(position).getInvoice());

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ReturnProductDetailsActivity.class)));

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        DisptchListLaoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DisptchListLaoutBinding.bind(itemView);
        }
    }

}
