package com.mahitab.ecommerce.adapters;

import android.app.Activity;
import android.graphics.Paint;
import android.util.DisplayMetrics;
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
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;

import java.util.List;

public class CollectionProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final CollectionModel collection;
    private final List<ProductModel> productList;

    public static final int VIEW_TYPE_COLLECTION = 1;
    public static final int VIEW_TYPE_PRODUCTS = 2;

    private CollectionClickListener collectionClickListener;
    private ProductClickListener productClickListener;

    private final DisplayMetrics displaymetrics = new DisplayMetrics();

    public CollectionProductsAdapter(CollectionModel collection) {
        this.collection = collection;
        this.productList = collection.getPreviewProducts();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_COLLECTION)
            return new CollectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_item, parent, false));
        else
            return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_COLLECTION:
                CollectionViewHolder viewHolder1 = (CollectionViewHolder) holder;

                collection.setImage("https://cdn.shopify.com/s/files/1/0339/2074/5609/files/4_1000x_crop_center.jpg?v=1584268288");

                Glide.with(viewHolder1.itemView.getContext())
                        .load(collection.getImage())
                        .thumbnail(/*sizeMultiplier*/ 0.25f)
                        .apply(new RequestOptions())
                        .placeholder(R.drawable.ic_image_gray_24dp)
                        .fallback(R.drawable.ic_image_gray_24dp)
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder1.ivCollectionImage);

                viewHolder1.tvCollectionTitle.setText(collection.getTitle());
                break;
            case VIEW_TYPE_PRODUCTS:
                ProductViewHolder viewHolder2 = (ProductViewHolder) holder;

                ProductModel singleItem = productList.get(position - 1);

                Glide.with(holder.itemView.getContext())
                        .load(singleItem.getImages()[0])
                        .thumbnail(/*sizeMultiplier*/ 0.25f)
                        .apply(new RequestOptions())
                        .placeholder(R.drawable.ic_image_gray_24dp)
                        .fallback(R.drawable.ic_image_gray_24dp)
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder2.ivImage);

                viewHolder2.tvTitle.setText(singleItem.getTitle());

                if (singleItem.getVariants().get(0).getOldPrice() != null &&
                        singleItem.getVariants().get(0).getOldPrice().compareTo(singleItem.getVariants().get(0).getPrice()) > 0 &&
                        singleItem.getVariants().get(0).isAvailableForSale()) {
                    viewHolder2.tvOldPrice.setText(singleItem.getVariants().get(0).getOldPrice().toString() + ' ');
                    viewHolder2.tvOldPrice.setPaintFlags(viewHolder2.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder2.tvPrice.setVisibility(View.VISIBLE);
                    viewHolder2.tvPrice.setText(singleItem.getVariants().get(0).getPrice().toString() + " EGP");
                } else {
                    viewHolder2.tvPrice.setVisibility(View.INVISIBLE);
                    viewHolder2.tvOldPrice.setVisibility(View.VISIBLE);
                    viewHolder2.tvOldPrice.setText(singleItem.getVariants().get(0).getPrice().toString() + " EGP");
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_COLLECTION;
        else
            return VIEW_TYPE_PRODUCTS;
    }

    @Override
    public int getItemCount() {
        return productList.size() + 1;
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCollectionImage;
        private final TextView tvCollectionTitle;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCollectionImage = itemView.findViewById(R.id.ivCollectionImage_CollectionItem);
            tvCollectionTitle = itemView.findViewById(R.id.tvCollectionTitle_CollectionItem);

            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need three fix imageview in width
            itemView.getLayoutParams().width = displaymetrics.widthPixels / 3;

            itemView.setOnClickListener(v -> {
                if (collectionClickListener != null) {
                    collectionClickListener.onCollectionClick(collection);
                }
            });
        }
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

            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need three fix imageview in width
            itemView.getLayoutParams().width = displaymetrics.widthPixels / 3;

            itemView.setOnClickListener(v -> {
                if (productClickListener != null) {
                    productClickListener.onProductClick(productList.get(getAdapterPosition()-1));
                }
            });
        }
    }


    public interface ProductClickListener {
        void onProductClick(ProductModel product);
    }

    public interface CollectionClickListener {
        void onCollectionClick(CollectionModel collection);
    }

    public void setOnProductClickListener(ProductClickListener productClickListener) {
        this.productClickListener = productClickListener;
    }

    public void setOnCollectionClickListener(CollectionClickListener collectionClickListener){
        this.collectionClickListener=collectionClickListener;
    }
}
