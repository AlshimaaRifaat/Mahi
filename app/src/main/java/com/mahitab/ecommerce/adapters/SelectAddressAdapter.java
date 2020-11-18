package com.mahitab.ecommerce.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.AddressModel;
import com.mahitab.ecommerce.models.MyOrdersModel;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;
import java.util.List;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.AddressViewHolder> {
    private List<AddressModel> addressList;
    Context context;
     SelectAddressItemInterface selectAddressItemInterface;
    private  Dialog dialog;
    private static final long MIN_CLICK_INTERVAL = 3000; //in millis
    private long lastClickTime = 0;
    public SelectAddressAdapter( Context context,List<AddressModel> addressList,Dialog dialog) {
        this.addressList = addressList;
        this.context=context;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public SelectAddressAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectAddressAdapter.AddressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_item, parent, false));
    }

    public void onClickItemSelectAddress(SelectAddressItemInterface selectAddressItemInterface)
    {
        this.selectAddressItemInterface=selectAddressItemInterface;
    }
    @Override
    public void onBindViewHolder(@NonNull SelectAddressAdapter.AddressViewHolder holder, int position) {
        AddressModel address=addressList.get(position);

        holder.tvAddressLine1.setText(address.getAddress1());
        holder.tvAddressLine2.setText(address.getAddress2());
        holder.tvMobileNumber.setText(address.getPhone());

        holder.btnSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
                    lastClickTime = currentTime;
                    loadDialog();
                    selectAddressItemInterface.navigateToPaymentCashOnDelivery(address,position);
                }




            }
        });


    }

    // using to Show Load Dialog
    public void loadDialog()
    {
      /*  dialog = new Dialog(context);
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.load_dialog,null,false));*/
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAddressLine1;
        private TextView tvAddressLine2;
        private TextView tvMobileNumber;
        private Button btnSelectAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressLine1 = itemView.findViewById(R.id.tvAddressLine1_AddressItem);
            tvAddressLine2 = itemView.findViewById(R.id.tvAddressLine2_AddressItem);
            tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber_AddressItem);
            btnSelectAddress = itemView.findViewById(R.id.btnSelectAddress);
        }


    }

    public interface SelectAddressItemInterface {
        void navigateToPaymentCashOnDelivery(AddressModel addressModel, int Position);
    }

}

