package com.mahitab.ecommerce.adapters;

import android.graphics.Paint;
import android.media.Image;
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
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemQuantity> cartItemQuantities;

    public CartAdapter() {
        cartItemQuantities = new ArrayList<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel product = DataManager.getInstance().getProductByID(cartItemQuantities.get(position).getProductID());

        Glide.with(holder.itemView.getContext())
                .load(product.getImages()[0])
                .thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProductImage);

        holder.tvProductTitle.setText(product.getTitle());

        if (product.getVariants().get(0).getOldPrice() != null &&
                product.getVariants().get(0).getOldPrice().compareTo(product.getVariants().get(0).getPrice()) > 0 &&
                product.getVariants().get(0).isAvailableForSale()) {
            holder.tvOldPrice.setText(product.getVariants().get(0).getOldPrice().toString() + ' ');
            holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(product.getVariants().get(0).getPrice().toString() + " EGP");
        } else {
            holder.tvPrice.setVisibility(View.INVISIBLE);
            holder.tvOldPrice.setVisibility(View.VISIBLE);
            holder.tvOldPrice.setText(product.getVariants().get(0).getPrice().toString() + " EGP");
        }

        holder.tvQuantity.setText(String.valueOf(cartItemQuantities.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartItemQuantities.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private ImageView ivDelete;
        private TextView tvProductTitle;
        private TextView tvPrice;
        private TextView tvOldPrice;
        private TextView tvQuantityType;
        private ImageView ivIncreaseQuantity;
        private TextView tvQuantity;
        private ImageView ivDecreaseQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage_CartItem);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle_CartItem);
            ivDelete = itemView.findViewById(R.id.ivDelete_CartItem);
            tvPrice = itemView.findViewById(R.id.tvPrice_CartItem);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice_CartItem);
            tvQuantityType = itemView.findViewById(R.id.tvQuantityType_CartItem);
            ivIncreaseQuantity = itemView.findViewById(R.id.ivIncreaseQuantity_CartItem);
            tvQuantity = itemView.findViewById(R.id.tvQuantity_CartItem);
            ivDecreaseQuantity = itemView.findViewById(R.id.ivDecreaseQuantity_CartItem);
        }
    }

    public void setCartItemQuantities(List<CartItemQuantity> cartItemQuantities) {
        this.cartItemQuantities = cartItemQuantities;
        notifyDataSetChanged();
    }
}
