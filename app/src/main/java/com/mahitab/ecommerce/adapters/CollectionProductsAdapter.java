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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CollectionProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final CollectionModel collectionModel;
    private final List<ProductModel> productList;

    public static final int VIEW_TYPE_COLLECTION = 1;
    public static final int VIEW_TYPE_PRODUCTS = 2;

    private CollectionClickListener collectionClickListener;
    private ProductClickListener productClickListener;

    private final DisplayMetrics displaymetrics = new DisplayMetrics();

    public CollectionProductsAdapter(CollectionModel collectionModel) {
        this.collectionModel = collectionModel;
        this.productList = collectionModel.getPreviewProducts();
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

                if (collectionModel.getImage() != null)
                    Glide.with(viewHolder1.itemView.getContext())
                            .load(collectionModel.getImage())
                            .thumbnail(Glide.with(holder.itemView.getContext()).load(R.drawable.loadimg))//.thumbnail(/*sizeMultiplier*/ 0.25f)
                            .apply(new RequestOptions())
//                            .placeholder(R.drawable.ic_image_gray_24dp)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .dontTransform()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(viewHolder1.ivCollectionImage);
                else
                    Glide.with(viewHolder1.itemView.getContext()).clear(viewHolder1.ivCollectionImage);

                viewHolder1.tvCollectionTitle.setText(collectionModel.getTitle());
                break;
            case VIEW_TYPE_PRODUCTS:
                ProductViewHolder viewHolder2 = (ProductViewHolder) holder;

                ProductModel product = productList.get(position - 1);

                Glide.with(holder.itemView.getContext())
                        .load(product.getImages()[0])
                        .thumbnail(Glide.with(holder.itemView.getContext()).load(R.drawable.loadimg))//.thumbnail(/*sizeMultiplier*/ 0.25f)
                        .apply(new RequestOptions())
//                        .placeholder(R.drawable.ic_image_gray_24dp)
                        .fallback(R.drawable.ic_image_gray_24dp)
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder2.ivImage);

                viewHolder2.tvTitle.setText(product.getTitle());

                String price;
                String oldPrice;
                if (product.getVariants() != null) {
                    if (product.getVariants().get(0).getOldPrice() != null &&
                            product.getVariants().get(0).getOldPrice().compareTo(product.getVariants().get(0).getPrice()) > 0 &&
                            product.getVariants().get(0).isAvailableForSale()) {
                        oldPrice = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getOldPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                        viewHolder2.tvOldPrice.setText(oldPrice);
                        viewHolder2.tvOldPrice.setPaintFlags(viewHolder2.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                        viewHolder2.tvPrice.setVisibility(View.VISIBLE);
                        viewHolder2.tvPrice.setText(price);

                        float mPrice = product.getVariants().get(0).getPrice().floatValue();
                        float mOldPrice = product.getVariants().get(0).getOldPrice().floatValue();

                        float ratioDiscount = ((mOldPrice - mPrice) / mOldPrice) * 100;
                        String discountPercentage = (int) Math.ceil(ratioDiscount) + viewHolder2.itemView.getContext().getResources().getString(R.string.discount_percentage);
                        viewHolder2.tvDiscount.setText(discountPercentage);
                        viewHolder2.tvDiscount.setVisibility(View.VISIBLE);

                    } else {
                        viewHolder2.tvPrice.setVisibility(View.INVISIBLE);
                        viewHolder2.tvOldPrice.setVisibility(View.VISIBLE);
                        price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                        viewHolder2.tvOldPrice.setText(price);
                        viewHolder2.tvDiscount.setVisibility(View.GONE);
                    }
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
        // return productList.size() + 1;

        if (productList.size() != 0) {
            return Math.min(productList.size(), 6);
        }
        return 0;
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
                    collectionClickListener.onCollectionClick(collectionModel);
                }
            });
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvTitle;
        private final TextView tvPrice;
        private final TextView tvOldPrice;
        private final TextView tvDiscount;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage_ProductItem);
            tvTitle = itemView.findViewById(R.id.tvTitle_ProductItem);
            tvPrice = itemView.findViewById(R.id.tvPrice_ProductItem);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice_ProductItem);
            tvDiscount = itemView.findViewById(R.id.tvDiscount_ProductItem);

            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need three fix imageview in width
            itemView.getLayoutParams().width = displaymetrics.widthPixels / 3;

            itemView.setOnClickListener(v -> {
                if (productClickListener != null) {
                    productClickListener.onProductClick(productList.get(getAdapterPosition() - 1));
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

    public void setOnCollectionClickListener(CollectionClickListener collectionClickListener) {
        this.collectionClickListener = collectionClickListener;
    }
}
