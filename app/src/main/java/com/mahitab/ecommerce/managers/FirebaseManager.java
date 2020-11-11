package com.mahitab.ecommerce.managers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.mahitab.ecommerce.models.BannerModel;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";

    public static List<BannerModel> getBanners(DataSnapshot dataSnapshot) {
        List<BannerModel> list = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.child("banners").getChildren()) {
            if (child.child("id").getValue() instanceof String
                    && child.child("type").getValue() instanceof String
                    && child.child("image").getValue() instanceof String
                    && child.child("numberOfClicks").getValue() instanceof Long) {
                BannerModel banner = child.getValue(BannerModel.class);
                banner.setReference(child.getRef());
                list.add(banner);
            }
        }
        return list;
    }

    public static void incrementBannerNoOfClicks(DatabaseReference bannerReference) {
        bannerReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.child("numberOfClicks").getValue() instanceof Long) {
                    Long currentNumberOfClicks = (Long) currentData.child("numberOfClicks").getValue();
                    if (currentNumberOfClicks == null) {
                        currentData.child("numberOfClicks").setValue(1);
                    } else {
                        currentData.child("numberOfClicks").setValue(currentNumberOfClicks + 1);
                    }
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Transaction failed");
                } else {
                    Log.e(TAG, "Transaction success");
                }
            }
        });
    }

}
