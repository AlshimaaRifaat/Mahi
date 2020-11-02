package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.NavigationInterface;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.ProductModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private List<CartItemQuantity> cartItemQuantities;

    private CartProductClickListener listener;
    Context context;
    public CartAdapter(Context context,List<CartItemQuantity> cartItemQuantities) {
        this.context=context;
        this.cartItemQuantities = cartItemQuantities;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel product = DataManager.getInstance().getProductByID(cartItemQuantities.get(position).getId().toString());
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

        String price;
        String oldPrice;
        if (product.getVariants() != null) {
            if (product.getVariants().get(0).getOldPrice() != null &&
                    product.getVariants().get(0).getOldPrice().compareTo(product.getVariants().get(0).getPrice()) > 0 &&
                    product.getVariants().get(0).isAvailableForSale()) {
//                oldPrice = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getOldPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
//                holder.tvOldPrice.setText(oldPrice);
                holder.tvOldPrice.setText(product.getVariants().get(0).getOldPrice()+"");
                holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvPrice.setVisibility(View.VISIBLE);
                holder.tvPrice.setText(price);
            } else {
                holder.tvPrice.setVisibility(View.INVISIBLE);
                holder.tvOldPrice.setVisibility(View.VISIBLE);
//                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
//                holder.tvOldPrice.setText(price);
                holder.tvOldPrice.setText(product.getVariants().get(0).getPrice()+"");
            }
        }


        holder.tvQuantity.setText(String.valueOf(cartItemQuantities.get(position).getQuantity()));
        holder.itemView.setOnClickListener(v -> listener.onProductClick(cartItemQuantities.get(position).getId().toString()));
    }

    @Override
    public int getItemCount() {
        return cartItemQuantities.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView ivProductImage;
        private final TextView tvProductTitle;
        private final TextView tvPrice;
        private final TextView tvOldPrice;
        private final TextView tvQuantityType;
        private final TextView tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage_CartItem);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle_CartItem);
            ImageView ivDelete = itemView.findViewById(R.id.ivDelete_CartItem);
            tvPrice = itemView.findViewById(R.id.tvPrice_CartItem);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice_CartItem);
            tvQuantityType = itemView.findViewById(R.id.tvQuantityType_CartItem);
            ImageView ivIncreaseQuantity = itemView.findViewById(R.id.ivIncreaseQuantity_CartItem);
            tvQuantity = itemView.findViewById(R.id.tvQuantity_CartItem);
            ImageView ivDecreaseQuantity = itemView.findViewById(R.id.ivDecreaseQuantity_CartItem);

            ivDelete.setOnClickListener(this);
            ivIncreaseQuantity.setOnClickListener(this);
            ivDecreaseQuantity.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onProductClick(cartItemQuantities.get(getAdapterPosition()).getId().toString());
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivDelete_CartItem) {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onDeleteProductClick(getAdapterPosition());
            } else if (v.getId() == R.id.ivIncreaseQuantity_CartItem) {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onIncreaseProductQuantityClick(getAdapterPosition());
            } else if (v.getId() == R.id.ivDecreaseQuantity_CartItem) {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onDecreaseProductQuantityClick(getAdapterPosition());
            }
        }
    }

    public interface CartProductClickListener {
        void onIncreaseProductQuantityClick(int position);

        void onDecreaseProductQuantityClick(int position);

        void onDeleteProductClick(int position);

        void onProductClick(String productId);
    }

    public void setCartProductClickListener(CartProductClickListener listener) {
        this.listener = listener;
    }
}
