package com.vysiontech.sewagemonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String channel_id="personal notification";
    NotificationCompat.Builder notification;
    private static final int  notificationid=1223;
    Map<String,String> payload;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            payload = remoteMessage.getData();
            notification = new NotificationCompat.Builder(this, channel_id);
            showNotification(payload);
        }
    }
    public void showNotification(Map<String,String> payload){
        createNotificationChannel();


        notification.setSmallIcon(R.mipmap.ic_vysion);
        notification.setTicker("Regarding sewer in Jodhpur");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(payload.get("title"));
        notification.setContentText(payload.get("body"));
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("From", "notifyFrag");
        intent.putExtra("value",1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //notification.addAction(R.drawable.ic_sms_black_24dp,"Yes",pendingIntent);
        notification.setContentIntent(pendingIntent);


        notification.setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationid, notification.build());
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            CharSequence name = "Personal notification";
            String description = "Include all personal notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager ab = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            ab.createNotificationChannel(notificationChannel);
        }


    }

}