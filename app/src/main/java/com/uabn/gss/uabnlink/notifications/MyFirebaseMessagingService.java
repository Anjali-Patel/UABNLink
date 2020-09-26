package com.uabn.gss.uabnlink.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uabn.gss.uabnlink.Activities.ChatList;
import com.uabn.gss.uabnlink.Activities.ChatMessages;
import com.uabn.gss.uabnlink.Activities.MainActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.RefreshChat;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;

import java.util.Random;

import static com.uabn.gss.uabnlink.Activities.ChatMessages.refreshchat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferenceUtils preferances;
    NotificationManager notificationManager;
    int n = 0;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        preferances = SharedPreferenceUtils.getInstance(this);
        String count = preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT, "");

        if (count == null || count.equalsIgnoreCase("") || count.equalsIgnoreCase("null") || count.equalsIgnoreCase(" ")) {
            n = n + 1;
        } else {
            n = Integer.parseInt(count) + 1;
        }
        preferances.setValue(CommonUtils.NOTIFICATIONCOUNT, String.valueOf(n));

        if (remoteMessage.getData().containsKey("tag")) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"));
        } else {

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d ("123",remoteMessage.getData().toString());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }
            int notificationId = new Random().nextInt(60000);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "123")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

            //showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), "tag");
        }



    }

    @Override
    public void onNewToken(String s) { super.onNewToken(s);

        preferances=SharedPreferenceUtils.getInstance(this);

        String refreshedToken = s;
        preferances.setValue(CommonUtils.FCMTOCKEN, s);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "test";
        String adminChannelDescription = "123";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("123", adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
    public void showNotification(String title, String body, String tag) {

        Intent i = null;

        if (tag.equals("chat") && refreshchat != null) {
            refreshchat.ChatRefresh();
            i = new Intent(this, ChatList.class);

        }
        else if (tag.equals("chat")){
            i = new Intent(this, ChatList.class);
        }
        else if (tag.equals("friend_request")) {
            i.putExtra("NotificationType", "friend_req");
            i = new Intent(this, MainActivity.class);
        }
        else if (tag.equals("post_comment")) {
            i = new Intent(this, MainActivity.class);
        }
        else if (tag.equals("pbcr_comment")) {
            i.putExtra("NotificationType", "pbcr");
            i = new Intent(this, MainActivity.class);
        }
        else if (tag.equals(" wall_to_wall")) {
            i.putExtra("NotificationType", "walltowall");
            i = new Intent(this, MainActivity.class);
        }
        else {
            i = new Intent(this, MainActivity.class);
        }

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String channelId = "channel ";
        String channelName = "ChannelName";

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.uabnlogo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }




}

