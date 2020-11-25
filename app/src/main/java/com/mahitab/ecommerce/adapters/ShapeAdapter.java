package com.mahitab.ecommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ShapeModel;

import java.util.List;

public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ShapeViewHolder> {
    private final List<ShapeModel> shapeList;

    public ShapeAdapter(List<ShapeModel> shapeModelList) {
        this.shapeList = shapeModelList;
    }

    @NonNull
    @Override
    public ShapeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShapeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shape_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShapeViewHolder holder, int position) {
        ShapeModel shape=shapeList.get(position);

        if (shape.getImage()!= null)
            Glide.with(holder.itemView)
                    .load(shape.getImage())
                    .thumbnail(/*sizeMultiplier*/ 0.50f)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform())
                    .into(holder.ivShapeImage);
        else Glide.with(holder.itemView).clear(holder.ivShapeImage);

        holder.tvShapeName.setText(shape.getTitle());
    }

    @Override
    public int getItemCount() {
        return shapeList.size();
    }

    public static class ShapeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivShapeImage;
        private final TextView tvShapeName;

        public ShapeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShapeImage = itemView.findViewById(R.id.ivShapeImage_ShapeItem);
            tvShapeName = itemView.findViewById(R.id.tvShapeName_ShapeItem);
        }
    }
}
