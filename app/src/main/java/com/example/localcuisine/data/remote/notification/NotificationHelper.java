package com.example.localcuisine.data.remote.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.localcuisine.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "review_reply_channel";
    private static final String CHANNEL_NAME = "Phản hồi đánh giá";

    private static boolean initialized = false;

    private static void ensureChannel(Context ctx) {
        if (initialized) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        initialized = true;
    }

    public static void notifyReply(
            Context ctx,
            String foodName,
            String replyContent
    ) {
        ensureChannel(ctx);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Có phản hồi mới")
                        .setContentText(
                                "Món \"" + foodName + "\": " + replyContent
                        )
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
