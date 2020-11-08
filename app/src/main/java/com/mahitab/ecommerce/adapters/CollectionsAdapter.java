package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;

import java.util.ArrayList;
import java.util.List;


public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionProductsViewHolder> implements
        CollectionProductsAdapter.CollectionClickListener
        , CollectionProductsAdapter.ProductClickListener {

    private List<CollectionModel> collectionList;
    private final Context context;

    private CollectionClickListener listener;

    public CollectionsAdapter(Context context) {
        this.collectionList = new ArrayList<>();
        this.context=context;
    }

    @NonNull
    @Override
    public CollectionProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionProductsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_products_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull CollectionProductsViewHolder holder, int position) {
        CollectionModel collection = collectionList.get(position);

        holder.rvProducts.setHasFixedSize(true);
        CollectionProductsAdapter collectionProductsAdapter = new CollectionProductsAdapter(collection);
        GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        holder.rvProducts.setLayoutManager(layoutManager);
        holder.rvProducts.setAdapter(collectionProductsAdapter);
        collectionProductsAdapter.setOnProductClickListener(this);
        collectionProductsAdapter.setOnCollectionClickListener(this);
    }


    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    @Override
    public void onProductClick(ProductModel product) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra("productId", product.getID().toString());
        context.startActivity(intent);
    }

    @Override
    public void onCollectionClick(CollectionModel collection) {
        Intent intent = new Intent(context, CollectionProductsActivity.class);
        intent.putExtra("collectionId", collection.getID().toString());
        context.startActivity(intent);
    }

    public void setCollections(List<CollectionModel> collectionList) {
        this.collectionList = collectionList;
        notifyDataSetChanged();
    }

    public class CollectionProductsViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rvProducts;

        public CollectionProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProducts = itemView.findViewById(R.id.rvProducts_CollectionProductsItem);
            TextView tvSeeAll = itemView.findViewById(R.id.tvSeeAll_CollectionProducts_Item);
            tvSeeAll.setOnClickListener(v -> {
                if (listener!=null&&getAdapterPosition()!=RecyclerView.NO_POSITION)
                    listener.onCollectionClick(collectionList.get(getAdapterPosition()));
            });
        }
    }

    public interface CollectionClickListener {
        void onCollectionClick(CollectionModel collection);
    }
    public void setOnCollectionClickListener(CollectionClickListener collectionClickListener){
        this.listener=collectionClickListener;
    }
}
