package com.mahitab.ecommerce.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ProductModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<ProductModel> productList;

    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder viewHolder2, int position) {
        ProductModel singleItem = productList.get(position);

        Glide.with(viewHolder2.itemView.getContext())
                .load(singleItem.getImages()[0])
                .thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder2.ivImage);

        viewHolder2.tvTitle.setText(singleItem.getTitle());

//        if (singleItem.getVariants().get(0).getOldPrice() != null &&
//                singleItem.getVariants().get(0).getOldPrice().compareTo(singleItem.getVariants().get(0).getPrice()) > 0 &&
//                singleItem.getVariants().get(0).isAvailableForSale()) {
//            viewHolder2.tvOldPrice.setText(singleItem.getVariants().get(0).getOldPrice().toString() + ' ');
//            viewHolder2.tvOldPrice.setPaintFlags(viewHolder2.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            viewHolder2.tvPrice.setVisibility(View.VISIBLE);
//            viewHolder2.tvPrice.setText(singleItem.getVariants().get(0).getPrice().toString() + " EGP");
//        } else {
//            viewHolder2.tvPrice.setVisibility(View.INVISIBLE);
//            viewHolder2.tvOldPrice.setVisibility(View.VISIBLE);
//            viewHolder2.tvOldPrice.setText(singleItem.getVariants().get(0).getPrice().toString() + " EGP");
//        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvTitle;
        private final TextView tvPrice;
        private final TextView tvOldPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage_ProductItem);
            tvTitle = itemView.findViewById(R.id.tvTitle_ProductItem);
            tvPrice = itemView.findViewById(R.id.tvPrice_ProductItem);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice_ProductItem);
        }
    }
}
