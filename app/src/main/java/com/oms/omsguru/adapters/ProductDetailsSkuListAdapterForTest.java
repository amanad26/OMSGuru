package com.oms.omsguru.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ProdutDetailSkuLayoutBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.utils.SkuInterface;

import java.util.List;

public class ProductDetailsSkuListAdapterForTest extends RecyclerView.Adapter<ProductDetailsSkuListAdapterForTest.ViewHolder> {

    Context context;
    List<FetchDetailsModel.Result.Sku> models;
    SkuInterface skuInterface;
    int added;

    public ProductDetailsSkuListAdapterForTest(Context context, List<FetchDetailsModel.Result.Sku> models, SkuInterface skuInterface) {
        this.context = context;
        this.models = models;
        this.skuInterface = skuInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.produt_detail_sku_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.sku.setText(models.get(position).getSkuCode());
        holder.binding.qty.setText(models.get(position).getQty());

        int qty = Integer.parseInt(models.get(position).getQty());
        added = 0;

        holder.binding.baadQTYPlus.setVisibility(View.GONE);
        holder.binding.badQTYMinus.setVisibility(View.GONE);
        holder.binding.goodQTYPlus.setVisibility(View.GONE);
        holder.binding.goodQTYMinus.setVisibility(View.GONE);

        if (qty == 1) {
            if (models.get(position).isGood())
                holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
            else holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty1()));

        } else {
            holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
            holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty2()));

        }

        holder.binding.badQTYTv.setFocusable(false);
        holder.binding.goodQTYTv.setFocusable(false);

        holder.binding.acceptedBaadQTYTv.setText(models.get(position).getBad_accepted_qty());
        holder.binding.acceptedGoodQTYTv.setText(models.get(position).getGood_accepted_qty());


        try {
            if (!models.get(position).getImage_url().equalsIgnoreCase("")) {
                Glide.with(context).load(models.get(position).getImage_url()).placeholder(R.drawable.baseline_image_24).into(holder.binding.image);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ProdutDetailSkuLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProdutDetailSkuLayoutBinding.bind(itemView);
        }
    }

}
