package com.mahitab.ecommerce.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ImageSliderModel;

import java.util.List;

public class ImageSliderAdapter extends LoopingPagerAdapter<ImageSliderModel> {
    private List<ImageSliderModel> imageSliderModelList;
    private Context context;

    public ImageSliderAdapter(Context context, List<ImageSliderModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
        imageSliderModelList = itemList;
        this.context = context;
    }


    @Override
    protected void bindView(View view, int position, int viewType) {
        ImageView ivImage = view.findViewById(R.id.ivImage);
        TextView tvTitle = view.findViewById(R.id.tvTitle);

        Glide.with(context)
                .load(imageSliderModelList.get(position).getImage())
                .thumbnail(/*sizeMultiplier*/ 0.25f)
                .apply(new RequestOptions())
                .placeholder(R.drawable.ic_image_gray_24dp)
                .fallback(R.drawable.ic_image_gray_24dp)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImage);

        tvTitle.setText(imageSliderModelList.get(position).getTitle());
    }

    @Override
    protected View inflateView(int i, ViewGroup viewGroup, int i1) {
        return LayoutInflater.from(context).inflate(R.layout.image_slider_item, viewGroup, false);
    }
}
