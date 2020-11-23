package com.mahitab.ecommerce.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.CategoryModel;

import java.util.List;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.MainCategoryViewHolder> {
    private final List<CategoryModel> categoryList;
    private int selectedPosition;

    public MainCategoryAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainCategoryViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(categoryList.get(position).getImage())
                .thumbnail(Glide.with(holder.itemView.getContext()).load(R.drawable.loadimg))//.thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
//                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivCategoryImage);

        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setEnabled(selectedPosition != position);

        if (selectedPosition == position) {
            Intent intent = new Intent("mainCategoryAdapter");
            intent.putExtra("category", categoryList.get(position));
            LocalBroadcastManager.getInstance(holder.itemView.getContext()).sendBroadcast(intent);
        }

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.tvItemSelected.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        } else {
            holder.itemView.setBackgroundColor(0);
            holder.tvItemSelected.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MainCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemSelected;
        private final ImageView ivCategoryImage;

        public MainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemSelected = itemView.findViewById(R.id.tvItemSelected_MainCategoryItem);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage_MainCategoryItem);
            itemView.setOnClickListener(v -> {
                notifyItemChanged(selectedPosition); // remove color from previous item
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
