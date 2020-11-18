package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.SelectedOptions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Observer {

    private ArrayList<ProductModel> productList;
    private int sectionWidth;
    private RecyclerView.LayoutManager layoutManager;
    private ProductClickListener listener;

    private ArrayList<ProductModel> productsDataList;
    private SelectedOptions selectedOptions = new SelectedOptions();

    public ProductAdapter(ArrayList<ProductModel> productList) {
        this.productsDataList = productList;
    }

    public ProductAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        updateList();
    }

    public ProductAdapter(LinearLayoutManager layoutManager,int sectionWidth, ArrayList<ProductModel> productList) {
        this.layoutManager=layoutManager;
        this.sectionWidth = sectionWidth;
        this.productList = productList;
        updateList();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productsDataList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(product.getImages()[0])
                .thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivImage);

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

                float mPrice = product.getVariants().get(0).getPrice().floatValue();
                float mOldPrice = product.getVariants().get(0).getOldPrice().floatValue();

                float ratioDiscount = ((mOldPrice - mPrice) / mOldPrice) * 100;
                String discountPercentage = (int) Math.ceil(ratioDiscount) + holder.itemView.getContext().getResources().getString(R.string.discount_percentage);
                holder.tvDiscount.setText(discountPercentage);
                holder.tvDiscount.setVisibility(View.VISIBLE);

            } else {
                holder.tvPrice.setVisibility(View.GONE);
                holder.tvOldPrice.setVisibility(View.VISIBLE);
                holder.tvOldPrice.setPaintFlags(0);
                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvOldPrice.setText(price);
                holder.tvDiscount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productsDataList.size();
    }

    public void updateList() {
        productsDataList = new ArrayList<>();

        ArrayList<ProductModel> aux = new ArrayList<>();
        Log.d("mohamed", "onQueryTextChange: 2");
        Log.d("mohamed", "onQueryTextChange: 2" + productList);

        for (ProductModel p : productList) {
            if (p.getTitle().toLowerCase().contains(selectedOptions.getSearchCriteria().toLowerCase())) {
                aux.add(p);
            }
        }
        productsDataList.addAll(aux);
        Log.d("ab", "updateList: " + productsDataList.toString());
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof SelectedOptions) {
            selectedOptions = (SelectedOptions) observable;
            this.updateList();
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
            if (layoutManager instanceof LinearLayoutManager && ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL){
                itemView.getLayoutParams().width = (int) (sectionWidth / 2.8);
            }
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onProductClick(productsDataList.get(getAdapterPosition()).getID().toString());
            });
        }
    }

    public interface ProductClickListener {
        void onProductClick(String productId);
    }

    public void setProductClickListener(ProductClickListener listener) {
        this.listener = listener;
    }

}
