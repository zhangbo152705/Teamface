package com.hjhq.teamface.basis.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.hjhq.teamface.basis.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    private Context mContext;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    public NotificationUtils(Context context) {
        super(context);
    }

    public void createNotificationChannel() {
       /* NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);*/
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder getChannelNotification(String title, String content) {
        return new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.status_bar_icon)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getNotification_25(String title, String content) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("TEAMFACE")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    public void sendNotification(String title, String content) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (title, content).build();
            getManager().notify(1, notification);
        } else {
            Notification notification = getNotification_25(title, content).build();
            getManager().notify(1, notification);
        }
    }
}  