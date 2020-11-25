package com.mahitab.ecommerce.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.BannerModel;

import java.util.List;

public class SliderBannersAdapter extends LoopingPagerAdapter<BannerModel> {
    private final List<BannerModel> imageSliderModelList;
    private final Context context;

    private SliderBannerClickListener listener;

    public SliderBannersAdapter(Context context, List<BannerModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
        imageSliderModelList = itemList;
        this.context = context;
    }


    @Override
    protected void bindView(View itemView, int position, int viewType) {
        ImageView ivImage = itemView.findViewById(R.id.ivImage);
        if (imageSliderModelList.get(position).getImage() != null)
            Glide.with(itemView)
                    .load(imageSliderModelList.get(position).getImage())
                    .thumbnail(/*sizeMultiplier*/ 0.50f)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform())
                    .into(ivImage);
        else Glide.with(itemView).clear(ivImage);

        itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onSliderBannerClick(imageSliderModelList.get(position));
        });
    }

    @Override
    protected View inflateView(int i, ViewGroup viewGroup, int i1) {
        return LayoutInflater.from(context).inflate(R.layout.image_slider_item, viewGroup, false);
    }

    public interface SliderBannerClickListener {
        void onSliderBannerClick(BannerModel banner);
    }

    public void setSliderBannerClickListener(SliderBannerClickListener listener) {
        this.listener = listener;
    }
}
