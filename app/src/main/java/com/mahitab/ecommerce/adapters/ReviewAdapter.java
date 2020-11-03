package com.mahitab.ecommerce.adapters;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.ProductReviewModel;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final List<ProductReviewModel> reviewList;
    private final DisplayMetrics displaymetrics = new DisplayMetrics();

    public ReviewAdapter(List<ProductReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ProductReviewModel review = reviewList.get(position);

        String fullName = review.getFirstName() + " " + review.getLastName();
        holder.tvFullName.setText(fullName);

        holder.tvMessage.setText(review.getMessage());

        holder.rbRating.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFullName;
        private final TextView tvMessage;
        private final RatingBar rbRating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName_ReviewItem);
            tvMessage = itemView.findViewById(R.id.tvMessage_ReviewItem);
            rbRating = itemView.findViewById(R.id.rbRating_ReviewItem);
            if (getAdapterPosition() > 1) {
                ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need three fix imageview in width
                itemView.getLayoutParams().width = (int) (displaymetrics.widthPixels / 1.5);
            }
        }
    }
}
