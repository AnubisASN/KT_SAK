package com.anubis.module_ftp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class RequestStartStopReceiver extends BroadcastReceiver {

    static final String TAG = RequestStartStopReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());
        try {
            if (intent.getAction().equals(FsService.Companion.getACTION_START_FTPSERVER())) {
                Intent serverService = new Intent(context, FsService.class);
                if (!FsService.Companion.isRunning()) {
                    warnIfNoExternalStorage();
                    context.startService(serverService);
                }
            } else if (intent.getAction().equals(FsService.Companion.getACTION_STOP_FTPSERVER())) {
                Intent serverService = new Intent(context, FsService.class);
                context.stopService(serverService);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }

    /**
     * Will check if the device contains external storage (sdcard) and display a warning
     * for the user if there is no external storage. Nothing more.
     */
    private void warnIfNoExternalStorage() {
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            Log.v(TAG, "Warning due to storage state " + storageState);
            Toast toast = Toast.makeText(  eDataFTP.INSTANCE.getMAPP(),
                    R.string.storage_warning, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

}
