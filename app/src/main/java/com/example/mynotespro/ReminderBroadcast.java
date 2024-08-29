package com.example.mynotespro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


    public class ReminderBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            Log.d("ReminderBroadcast", "Broadcast received!");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyNotesProChannel")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle("Reminder")
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Check for notification permission if targeting Android 13 and above
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            notificationManager.notify(200, builder.build());
        }
    }






