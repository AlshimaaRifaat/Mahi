package com.mahitab.ecommerce.adapters;

import android.content.Context;
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
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.ProductModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.mahitab.ecommerce.utils.CommonUtils.getImageThumbnailURL;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItemQuantity> cartItemQuantities;
    private final Context context;
    private CartProductClickListener listener;

    public CartAdapter(Context context, List<CartItemQuantity> cartItemQuantities) {
        this.context = context;
        this.cartItemQuantities = cartItemQuantities;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel product = DataManager.getInstance().getProductByID(cartItemQuantities.get(position).getProductID());

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
                    .into(holder.ivProductImage);
        } else Glide.with(holder.itemView).clear(holder.ivProductImage);

        holder.tvProductTitle.setText(product.getTitle());

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
                String discountPercentage = NumberFormat.getInstance(new Locale("ar")).format(Math.ceil(ratioDiscount)) + holder.itemView.getContext().getResources().getString(R.string.discount_percentage);
                holder.tvDiscount.setText(discountPercentage);
                holder.tvDiscount.setVisibility(View.VISIBLE);

            } else {
                holder.tvPrice.setVisibility(View.INVISIBLE);
                holder.tvOldPrice.setVisibility(View.VISIBLE);
                price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + holder.itemView.getContext().getResources().getString(R.string.egp);
                holder.tvOldPrice.setText(price);
                holder.tvDiscount.setVisibility(View.GONE);
            }
        }

        if (product.getTitle().startsWith("قماش")) {
            String type = context.getResources().getString(R.string.meter);
            holder.tvQuantityType.setText(type);
            holder.tvQuantityType.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuantityType.setVisibility(View.GONE);
        }

        holder.tvQuantity.setText(String.valueOf(cartItemQuantities.get(position).getQuantity()));
        holder.itemView.setOnClickListener(v -> listener.onProductClick(cartItemQuantities.get(position).getProductID()));
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
        private final TextView tvDiscount;

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
            tvDiscount = itemView.findViewById(R.id.tvDiscount_CartItem);
            ImageView ivDecreaseQuantity = itemView.findViewById(R.id.ivDecreaseQuantity_CartItem);

            ivDelete.setOnClickListener(this);
            ivIncreaseQuantity.setOnClickListener(this);
            ivDecreaseQuantity.setOnClickListener(this);
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onProductClick(cartItemQuantities.get(getAdapterPosition()).getVariantId().toString());
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
