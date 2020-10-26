package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private final List<AddressModel> addressList;
    Context context;
    public AddressAdapter( Context context,List<AddressModel> addressList) {
        this.addressList = addressList;
        this.context=context;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address=addressList.get(position);

        holder.tvAddressLine1.setText(address.getAddress1());
        holder.tvAddressLine2.setText(address.getAddress2());
        holder.tvMobileNumber.setText(address.getPhone());
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAddressLine1;
        private TextView tvAddressLine2;
        private TextView tvMobileNumber;
        private TextView tvEditAddress;

        private ImageView ivDeleteAddress;


        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressLine1 = itemView.findViewById(R.id.tvAddressLine1_AddressItem);
            tvAddressLine2 = itemView.findViewById(R.id.tvAddressLine2_AddressItem);
            tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber_AddressItem);

            tvEditAddress = itemView.findViewById(R.id.tvEditAddress_AddressItem);
            ivDeleteAddress = itemView.findViewById(R.id.ivDeleteAddress_AddressItem);

        }
    }
}
