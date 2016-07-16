package com.fdv.loggedoff.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.TextViewCompat;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.fdv.loggedoff.Activtys.LoginActivity;
import com.fdv.loggedoff.R;


public class NotificationUtils {
   public static Context mContext;
    private static NotificationUtils ourInstance;

    public static NotificationUtils getInstance(Context context) {

        if(ourInstance == null){
            ourInstance = new NotificationUtils(context);
        }
        return ourInstance;
    }

    private NotificationUtils() {
    }

    private NotificationUtils(Context context){
        mContext = context;
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static void sendNotification(String title,String messageBody) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        RemoteViews remoteViews = new RemoteViews("com.fdv.loggedoff",
                R.layout.widget_notification);
        remoteViews.setTextViewText(R.id.textViewUser,title);
        remoteViews.setTextViewText(R.id.textViewNotification,messageBody);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void sendPersonalizedNotification(String messageBody,String userName) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        RemoteViews remoteViews = new RemoteViews("com.fdv.loggedoff",
                R.layout.widget_notification);
        remoteViews.setTextViewText(R.id.textViewUser,userName);
        remoteViews.setTextViewText(R.id.textViewNotification,messageBody);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
