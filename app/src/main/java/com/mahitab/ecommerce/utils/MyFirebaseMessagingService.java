package com.mahitab.ecommerce.utils;


import android.app.PendingIntent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mahitab.ecommerce.R;

import java.util.Map;
import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {

            for(String key: remoteMessage.getData().keySet()){

                Map<String, String> extraData = remoteMessage.getData();

                String brandId = extraData.get("key");
//                Log.i(getString(R.string.DEBUG_TAG), "onMessageReceived: brandId: " + brandId);
            }
        }

        if (remoteMessage.getNotification() != null) {
            MyApplication application = (MyApplication) getApplicationContext();

//            application.triggerNotificationWithBackStack(ProActivity.class,
//                    getString(R.string.NEWS_CHANNEL_ID),
//                    Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),
//                    remoteMessage.getNotification().getBody(),
//                    remoteMessage.getNotification().getBody(),
//                    NotificationCompat.PRIORITY_HIGH,
//                    true,
//                    getResources().getInteger(R.integer.notificationId),
//                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public interface GetResultInterface {
        void getResult(String key);
    }

}
