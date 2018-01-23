package com.cnu_bus_alarm.cnu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by 진수연 on 2018-01-19.
 */

public class BroadcastL extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onReceive(Context context, Intent intent){//알람 시간이 되었을때 이를 호출함

        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent =PendingIntent.getActivities(context,(int)(System.currentTimeMillis()/1000),
                new Intent[]{new Intent(context,MainActivity.class)},PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mbuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.cnu_alarmicon)
                .setTicker("CNU막차 알람")
                .setWhen(System.currentTimeMillis())
                .setNumber(1)
                .setContentTitle("막차 출발 예정알람")
                .setContentText("마지막 버스가 10분 후 출발 예정입니다.")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int)(System.currentTimeMillis()/1000),mbuilder.build());
    }
}
