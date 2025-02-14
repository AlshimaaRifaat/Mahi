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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ProductModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.mahitab.ecommerce.utils.CommonUtils.getImageThumbnailURL;

public class RecentlyViewedProductsAdapter extends RecyclerView.Adapter<RecentlyViewedProductsAdapter.ProductViewHolder> {

    private final ArrayList<ProductModel> productList;
    private RecentlyViewedProductsAdapter.ProductClickListener listener;


    public RecentlyViewedProductsAdapter(ArrayList<ProductModel> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_viewed_product_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        if (product.getImages()[0] != null) {
            String thumbnailURL = getImageThumbnailURL(product.getImages()[0]);
            Glide.with(holder.itemView)
                    .load(thumbnailURL)
                    .thumbnail(/*sizeMultiplier*/ 0.50f)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform())
                    .into(holder.ivImage);
        } else Glide.with(holder.itemView).clear(holder.ivImage);

        holder.tvTitle.setText(product.getTitle());

        String price;
        String oldPrice;
        if (product.getVariants() != null) {
            if (product.getVariants().get(0).getOldPrice() != null &&
                    product.getVariants().get(0).getOldPrice().compareTo(product.getVariants().get(0).getPrice()) > 0 &&
                    product.getVariants().get(0).isAvailableForSale()) {
                oldPrice = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getOldPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvOldPrice.setText(oldPrice);
                holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvPrice.setVisibility(View.VISIBLE);
                holder.tvPrice.setText(price);
            } else {
                holder.tvPrice.setVisibility(View.INVISIBLE);
                holder.tvOldPrice.setVisibility(View.VISIBLE);
                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvOldPrice.setText(price);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
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
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onProductClick(productList.get(getAdapterPosition()).getID().toString());
            });
        }
    }

    public interface ProductClickListener {
        void onProductClick(String productId);
    }

    public void setProductClickListener(RecentlyViewedProductsAdapter.ProductClickListener listener) {
        this.listener = listener;
    }

}

