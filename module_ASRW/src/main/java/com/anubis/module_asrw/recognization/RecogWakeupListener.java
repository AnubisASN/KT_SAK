package com.anubis.module_asrw.recognization;

import android.os.Handler;

import com.anubis.module_asrw.wakeup.SimpleWakeupListener;
import com.anubis.module_asrw.wakeup.WakeUpResult;

/**
 * Created by fujiayi on 2017/9/21.
 */

public class RecogWakeupListener extends SimpleWakeupListener implements IStatus {

    private static final String TAG = "RecogWakeupListener";

    private Handler handler;

    public RecogWakeupListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(String word, WakeUpResult result) {
        super.onSuccess(word, result);
        handler.sendMessage(handler.obtainMessage(STATUS_WAKEUP_SUCCESS));
    }
}
