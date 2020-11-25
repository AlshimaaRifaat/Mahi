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


import java.util.List;

public class ProductImageSliderAdapter extends LoopingPagerAdapter<String> {
    private final List<String> imageList;
    private final Context context;
    private ImageSliderItemClickInterface imageSliderItemClickInterface;
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
    protected void bindView(View itemView,  int listPosition, int viewType) {

        ImageView ivSlideImage = itemView.findViewById(R.id.ivSlideImage_ProductImageSlide);

        if (imageList.get(listPosition) != null)
            Glide.with(itemView)
                    .load(imageList.get(listPosition))
                    .thumbnail(/*sizeMultiplier*/ 0.50f)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .fallback(R.drawable.ic_image_gray_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform())
                    .into(ivSlideImage);
        else Glide.with(itemView).clear(ivSlideImage);

        ivSlideImage.setOnClickListener(v -> imageSliderItemClickInterface.imageSliderItemClick(itemView,listPosition,imageList));

    }

    public interface ImageSliderItemClickInterface {
        void imageSliderItemClick(View view,int position,List<String> imageList);
    }

    public void setImageSliderItemClickListener(ImageSliderItemClickInterface imageSliderItemClickInterface) {
        this.imageSliderItemClickInterface = imageSliderItemClickInterface;
    }

}
