/*******************************************************************************
 * Copyright (c) 2013 Pieter Pareit.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Pieter Pareit - initial API and implementation
 ******************************************************************************/
package com.anubis.module_ftp.GUI;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import com.anubis.module_ftp.FsService;
import com.anubis.module_ftp.R;


/**
 * Simple widget for FTP Server.
 * 
 * @author ppareit
 */
public class eWidgetProvider extends AppWidgetProvider {

    private static final String TAG = eWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received broadcast: " + intent.getAction());
        // 监视由ftp服务器进行的广播，并在需要时更新小部件
        final String action = intent.getAction();
        if (action.equals(FsService.Companion.getACTION_STARTED())
                || action.equals(FsService.Companion.getACTION_STOPPED())) {
            Intent updateIntent = new Intent(context, UpdateService.class);
            context.startService(updateIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        Log.d(TAG, "updated called");
        // let the updating happen by a service
        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    public static class UpdateService extends Service {
        // all real work is done in a service to avoid ANR messages
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "UpdateService start command");
            // We need to create the correct pending intent for when the widget is clicked
            final String action = FsService.Companion.isRunning() ? FsService.Companion.getACTION_STOP_FTPSERVER()
                    : FsService.Companion.getACTION_START_FTPSERVER();
            Intent startIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    startIntent, 0);
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            // we need to put the correct image on the widget
            final int drawable = FsService.Companion.isRunning() ? R.drawable.widget_on
                    : R.drawable.widget_off;
            views.setImageViewResource(R.id.widget_button, drawable);
            // new info is on widget, update it
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            ComponentName widget = new ComponentName(this, eWidgetProvider.class);
            manager.updateAppWidget(widget, views);
            // service has done it's work, android may kill it
            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
