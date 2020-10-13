package com.mahitab.ecommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.CollectionModel;

import java.util.ArrayList;
import java.util.List;


public class CollectionProductsAdapter extends RecyclerView.Adapter<CollectionProductsAdapter.CollectionProductsViewHolder> {

    private List<CollectionModel> collectionList;

    public CollectionProductsAdapter() {
        this.collectionList = new ArrayList<>();
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
        ProductAdapter productAdapter = new ProductAdapter(collection);
        GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        holder.rvProducts.setLayoutManager(layoutManager);
        holder.rvProducts.setAdapter(productAdapter);
    }


    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public void setCollections(List<CollectionModel> collectionList) {
        this.collectionList = collectionList;
        notifyDataSetChanged();
    }

    public static class CollectionProductsViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvProducts;

        public CollectionProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProducts = itemView.findViewById(R.id.rvProducts_CollectionProductsItem);
        }
    }
}
