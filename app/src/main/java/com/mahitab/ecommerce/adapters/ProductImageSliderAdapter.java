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


import java.util.List;

public class ProductImageSliderAdapter extends LoopingPagerAdapter<String> {
    private final List<String> imageList;
    private final Context context;

    public ProductImageSliderAdapter(Context context, List<String> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
        imageList = itemList;
        this.context = context;
    }


    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.product_image_slide, container, false);
    }

    @Override
    protected void bindView(View convertView, final int listPosition, int viewType) {

        final ImageView ivSlideImage = convertView.findViewById(R.id.ivSlideImage_ProductImageSlide);

        Glide.with(context)
                .load(imageList.get(listPosition))
                .thumbnail(/*sizeMultiplier=*/ 0.25f)
                .apply(new RequestOptions())
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivSlideImage);

    }
}
