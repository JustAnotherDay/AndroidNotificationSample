package com.example.notificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notificationdemo.ConnectionHelper;

import static com.example.notificationdemo.NotificationSender.CHANNEL_1_ID;
import static com.example.notificationdemo.NotificationSender.CHANNEL_2_ID;

public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = NotificationManagerCompat.from(this);

        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                NotificationSender nf = new NotificationSender();
                ConnectionHelper ConnectionHelper = new ConnectionHelper();
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection\
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && ConnectionHelper.isOnline) {
                                ConnectionHelper.isOnline = false;
                                Log.i("NETWORK123","Connection lost");
                                nf.sendOnChannel2();
                                //manager.cancelAll();
                            }
                        }
                    } else {
                        Log.i("NETWORK123","Connected");

                        nf.sendOnChannel1();
                        //showNotifications("APP" , "It is working");
                        // Perform your actions here
                        ConnectionHelper.isOnline = true;

                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
