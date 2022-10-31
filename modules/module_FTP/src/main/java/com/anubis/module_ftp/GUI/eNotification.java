/*
Copyright 2011-2013 Pieter Pareit

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.anubis.module_ftp.GUI;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import com.anubis.module_ftp.FsService;
import com.anubis.module_ftp.FsSettings;
import com.anubis.module_ftp.R;
import java.net.InetAddress;


public class eNotification extends BroadcastReceiver {
    private static final String TAG = eNotification.class.getSimpleName();
    private final int NOTIFICATIONID = 7890;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive broadcast: " + intent.getAction());
        if (intent.getAction().equals(FsService.Companion.getACTION_STARTED())) {
            setupNotification(context);
        } else if (intent.getAction().equals(FsService.Companion.getACTION_STOPPED())) {
            clearNotification(context);
        }
    }

    @SuppressLint("NewApi")
    private void setupNotification(Context context) {
        Log.d(TAG, "Setting up the notification");
        // Get NotificationManager reference
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);

        // get ip address
        InetAddress address = FsService.Companion.getLocalInetAddress();
        if (address == null) {
            Log.w(TAG, "Unable to retreive the local ip address");
            return;
        }
        String iptext = "ftp://" + address.getHostAddress() + ":"
                + FsSettings.getPortNumber() + "/";

        // Instantiate a Notification
        int icon = R.drawable.notification;
        CharSequence tickerText = String.format(
                context.getString(R.string.notif_server_starting), iptext);
        long when = System.currentTimeMillis();

        // Define Notification's message and Intent
        CharSequence contentTitle = context.getString(R.string.notif_title);
        CharSequence contentText = String.format(context.getString(R.string.notif_text),
                iptext);

        Intent notificationIntent = new Intent(context, eFTPUIs.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        int stopIcon = android.R.drawable.ic_menu_close_clear_cancel;
        CharSequence stopText = context.getString(R.string.notif_stop_text);
        Intent stopIntent = new Intent(FsService.Companion.getACTION_STOP_FTPSERVER());
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0,
                stopIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder nb = new Notification.Builder(context) //
                .setContentTitle(contentTitle) //
                .setContentText(contentText) //
                .setContentIntent(contentIntent) //
                .setSmallIcon(icon) //
                .setTicker(tickerText) //
                .setWhen(when) //
                .setOngoing(true);

        Notification notification = null;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            nb.addAction(stopIcon, stopText, stopPendingIntent);
            notification = nb.build();
        } else {
            notification = nb.getNotification();
        }

        // Pass Notification to NotificationManager
        nm.notify(NOTIFICATIONID, notification);

        Log.d(TAG, "Notication setup done");
    }


    private void clearNotification(Context context) {
        Log.d(TAG, "Clearing the notifications");
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
        nm.cancelAll();
        Log.d(TAG, "Cleared notification");
    }
}
