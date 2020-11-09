package com.mahitab.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.MyOrdersModel;
import com.mahitab.ecommerce.models.SelectedOptions;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>  {

    Context context;

    private ArrayList<MyOrdersModel> myOrdersModelArrayList = new ArrayList<>();

    private SelectedOptions selectedOptions = new SelectedOptions();
    MyOrderItemInterface myOrderItemInterface;
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

    public void onClickItemMyOrder(MyOrderItemInterface myOrderItemInterface)
    {
        this.myOrderItemInterface=myOrderItemInterface;
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.ViewHolder holder, final int position) {

        MyOrdersModel singleItem = myOrdersModelArrayList.get(position);
        holder.tOrderNumber.setText(context.getResources().getString(R.string.OrderNumber)+": "+singleItem.getOrderNumber().toString());
        float price=Float.parseFloat(singleItem.getTotalPrice());
        String totalPrice = (int) Math.ceil(price)+"";
        Log.d("price", "onBindViewHolder: "+totalPrice+" ");
        holder.tTotalPrice.setText(context.getResources().getString(R.string.TotalPrice)+": "+totalPrice +" EGP");
        String date = singleItem.getProcessedAt().toString();
        String[] arr = date.split("T");

        holder.tDate.setText(context.getResources().getString(R.string.Date)+": "+arr[0].trim());
        holder.btnMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOrderItemInterface.goToMyOrderDetails(myOrdersModelArrayList.get(position),position);

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
        protected Button btnMoreDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            tOrderNumber = itemView.findViewById(R.id.tOrderNumber);
            tTotalPrice = itemView.findViewById(R.id.tTotalPrice);
            tDate = itemView.findViewById(R.id.tDate);
            btnMoreDetails = itemView.findViewById(R.id.btnMoreDetails);

        }
    }
    public interface MyOrderItemInterface {
        void goToMyOrderDetails(MyOrdersModel myOrdersModel, int Position);
    }

}
