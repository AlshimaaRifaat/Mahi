package com.mahitab.ecommerce.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.BannerModel;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<BannerModel> bannerList;
    private final int sectionWidth;
    private BannerClickListener listener;

    public BannerAdapter(int sectionWidth) {
        this.bannerList = new ArrayList<>();
        this.sectionWidth = sectionWidth;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (bannerList.get(position).getImage() != null)
            Glide.with(holder.itemView)
                    .load(bannerList.get(position).getImage())
                    .thumbnail(/*sizeMultiplier*/ 0.50f)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform())
                    .into(holder.ivImage);
        else Glide.with(holder.itemView).clear(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage_BannerItem);

            if (bannerList.size() == 1)
                itemView.getLayoutParams().width = (int) (sectionWidth/1.06);
            else if (bannerList.size() == 2)
                itemView.getLayoutParams().width = (int) (sectionWidth / 2.16);
            else
                itemView.getLayoutParams().width = (int) (sectionWidth / 3.16);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onBannerClick(bannerList.get(getAdapterPosition()));
            });
        }
    }

    public void setBannerList(List<BannerModel> bannerList) {
        this.bannerList = bannerList;
        notifyDataSetChanged();
    }

    public interface BannerClickListener {
        void onBannerClick(BannerModel banner);
    }

    public void setBannerClickListener(BannerClickListener listener) {
        this.listener = listener;
    }
}
