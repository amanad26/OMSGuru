package com.oms.omsguru.return_processing;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marsad.stylishdialogs.StylishAlertDialog;
import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ImageRecyclerLayoutBinding;
import com.oms.omsguru.utils.UnslectedImageInteface;

import java.io.File;
import java.util.ArrayList;

public class ClickedImagesAdapter extends RecyclerView.Adapter<ClickedImagesAdapter.ViewHolder> {

    ArrayList<File> files;
    Context context;
    UnslectedImageInteface unslectedImageInteface;

    public ClickedImagesAdapter(ArrayList<File> files, Context context, UnslectedImageInteface unslectedImageInteface) {
        this.files = files;
        this.context = context;
        this.unslectedImageInteface = unslectedImageInteface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.image.setImageURI(Uri.fromFile(files.get(position)));

        holder.binding.imageRemove.setOnClickListener(v -> {
            int pos = position;
            showDeleteImage(pos);
        });

        holder.itemView.setOnClickListener(v -> UploadFileActivity.showFullScreenImageDialog(files.get(position), context));


    }


    private void showDeleteImage(int pos) {
        Log.e("TAG", "showDeleteImage: Pos " + pos);
        int pos2 = pos;
        StylishAlertDialog stylishDialog = new StylishAlertDialog(context, StylishAlertDialog.WARNING);
        stylishDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        stylishDialog.setTitleText("Delete!").setContentText("Are you sure do you want to delete image ?").setCancellable(false)
                .setConfirmButton("Yes", StylishAlertDialog -> {
                    unslectedImageInteface.onRemove(pos2);
                    stylishDialog.dismiss();
                }).setCancelButton("No", StylishAlertDialog -> StylishAlertDialog.dismiss()).show();
    }


    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageRecyclerLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageRecyclerLayoutBinding.bind(itemView);
        }
    }
}
