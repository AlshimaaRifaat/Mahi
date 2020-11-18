package com.mahitab.ecommerce.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
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
    protected void bindView(View view, int position, int viewType) {
        ImageView ivImage = view.findViewById(R.id.ivImage);
        Glide.with(context)
                .load(imageSliderModelList.get(position).getImage())
                .thumbnail(Glide.with(context).load(R.drawable.loadimg))//.thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
//                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImage);

        view.setOnClickListener(v -> {
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
