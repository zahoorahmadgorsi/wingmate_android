package com.app.wingmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.app.wingmate.events.RefreshUserStatus;
import com.app.wingmate.ui.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.parse.PLog;
import com.parse.PushRouter;
import com.parse.fcm.ParseFCM;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class WingMateParseFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WingMatePFMService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        ParseFCM.register(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived");

        String pushId = remoteMessage.getData().get("push_id");
        String timestamp = remoteMessage.getData().get("time");
        String dataString = remoteMessage.getData().get("data");
        String channel = remoteMessage.getData().get("channel");

        JSONObject data = null;
        if (dataString != null) {
            try {
                data = new JSONObject(dataString);
                dataString = data.getString("alert");
                System.out.println("==WingMatePFMService=="+data.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Ignoring push because of JSON exception while processing: " + dataString, e);
                return;
            }
        }

//        PushRouter.getInstance().handlePush(pushId, timestamp, channel, data);
        EventBus.getDefault().post(new RefreshUserStatus());
        showNotification(getApplicationContext(), pushId, timestamp, dataString, channel, data);
    }


    public static void showNotification(Context mContext, String pushId, String timestamp, String dataString, String channel, JSONObject data) {
        NotificationCompat.BigTextStyle InboxStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Wing Mate")
                .bigText(dataString);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, pushId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Wing Mate")
                    .setContentText(dataString)
                    .setAutoCancel(true)
                    .setSound(null)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setStyle(InboxStyle);
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Wing Mate")
                    .setContentText(dataString)
                    .setAutoCancel(true)
                    .setSound(null)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setStyle(InboxStyle);
        }
        Intent resultIntent = new Intent(mContext, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 020;
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = channel;
            String CHANNEL_NAME = dataString;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mNotifyMgr.createNotificationChannel(mChannel);
        }
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


}