package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.MyOrdersModel;
import com.mahitab.ecommerce.models.SelectedOptions;

import java.util.ArrayList;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>  {

    Context context;

    private ArrayList<MyOrdersModel> myOrdersModelArrayList = new ArrayList<>();

    private SelectedOptions selectedOptions = new SelectedOptions();
    public MyOrdersAdapter(Context context, ArrayList<MyOrdersModel> dataList) {
        this.context = context;
        this.myOrdersModelArrayList = dataList;



    }


    @NonNull
    @Override
    public MyOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new MyOrdersAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.ViewHolder holder, final int position) {

        MyOrdersModel singleItem = myOrdersModelArrayList.get(position);
        holder.tOrderNumber.setText(context.getResources().getString(R.string.OrderNumber)+": "+singleItem.getOrderNumber().toString());
        holder.tTotalPrice.setText(context.getResources().getString(R.string.TotalPrice)+": "+singleItem.getTotalPrice().toString());
        String date = singleItem.getProcessedAt().toString();
        String[] arr = date.split("T");

        holder.tDate.setText(context.getResources().getString(R.string.Date)+": "+arr[0].trim());
        holder.tMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(singleItem.getStatutsUrl().toString()));
                context.startActivity(intent);

            }
        });




    }


    public int getItemCount() {
        return myOrdersModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tOrderNumber;
        protected TextView tTotalPrice;
        protected TextView tDate;
        protected TextView tMoreDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            tOrderNumber = itemView.findViewById(R.id.tOrderNumber);
            tTotalPrice = itemView.findViewById(R.id.tTotalPrice);
            tDate = itemView.findViewById(R.id.tDate);
            tMoreDetails = itemView.findViewById(R.id.tMoreDetails);

        }
    }

}
