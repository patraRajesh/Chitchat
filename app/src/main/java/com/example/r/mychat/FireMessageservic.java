package com.example.r.mychat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by r on 1/11/2018.
 */

public class FireMessageservic extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notiTitle=remoteMessage.getNotification().getTitle();
        String notiMess=remoteMessage.getNotification().getBody();
        String clickaction=remoteMessage.getNotification().getClickAction();
        String uid=remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder   nbulider= new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.alert_light_frame)
                .setContentTitle(notiTitle).setContentText(notiMess);

        Intent resultin=new Intent(clickaction);
        resultin.putExtra("uid",uid);
        PendingIntent resultpendingin=PendingIntent.getActivity(this,0,resultin,PendingIntent.FLAG_UPDATE_CURRENT);

        int mNotiid=(int) System.currentTimeMillis();
        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotiid,nbulider.build());
    }
}
