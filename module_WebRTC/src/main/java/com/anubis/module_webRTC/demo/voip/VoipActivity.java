package com.anubis.module_webRTC.demo.voip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.database.CoreDB;
import com.anubis.module_webRTC.database.HistoryBean;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHSDKHelper;
import com.starrtc.starrtcsdk.api.XHVoipManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;
import com.starrtc.starrtcsdk.core.pusher.XHScreenRecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.anubis.kt_extends.EExtendsKt.eLog;

public class VoipActivity extends BaseActivity implements View.OnClickListener {
    private InputMethodManager inputMethodManager;
    private XHVoipManager voipManager;
    private StarPlayer targetPlayer;
    private StarPlayer selfPlayer;
    public static VoipActivity mVoipActivity = null;
    public static String ACTION = "ACTION";
    public static String RING = "RING";
    public static String CALLING = "CALLING";
    private String action;
    private String targetId;
    private Long outTime;
    private Boolean isTalking = false;
    private Handler mHandler;
    private TextView tvOutTime;
    private int cameraId = 0;
    private Boolean isRemoteVideo = false;
    private TextView tvTimer;
    private StarRTCAudioManager starRTCAudioManager;
    private XHSDKHelper xhsdkHelper;
    private TimerTask mTimerTask;
    private TimerTask mTalkingTiTask;
    private static Boolean status = false;

//    private PushUVCTest pushUVCTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starRTCAudioManager = StarRTCAudioManager.create(this.getApplicationContext());
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set availableAudioDevices) {
                eLog(this, selectedAudioDevice.name(), "TAG");
            }
        });
        starRTCAudioManager.setDefaultAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voip);
        mVoipActivity = this;
        voipManager = XHClient.getInstance().getVoipManager();
        voipManager.setRecorder(new XHCameraRecorder());
        voipManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        addListener();
        mHandler = new Handler();
        tvOutTime = (TextView) findViewById(R.id.tv_outtime);
        targetId = getIntent().getStringExtra("targetId");
        cameraId = getIntent().getIntExtra("cameraId", 0);
        isRemoteVideo = getIntent().getBooleanExtra("isisRemoteVideo", false);
        outTime = getIntent().getLongExtra("outTime", 60);
        final Long[] time = {outTime};
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                time[0]--;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvOutTime.setText("等待倒计时:" + time[0]);
                        if (time[0] == 0) {
                            eLog(this, "倒计时关闭", "TAG");
                            //接听等待
                            onClick(findViewById(R.id.calling_hangup));
                        }
                    }
                });
            }
        };
        new Timer().schedule(mTimerTask, outTime, 1000L);
        action = getIntent().getStringExtra(ACTION);
        targetPlayer = (StarPlayer) findViewById(R.id.voip_surface_target);
        selfPlayer = (StarPlayer) findViewById(R.id.voip_surface_self);
        selfPlayer.setZOrderMediaOverlay(true);
        tvTimer = (TextView) findViewById(R.id.timer);
        targetPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTalking) {
                    findViewById(R.id.talking_view).setVisibility(findViewById(R.id.talking_view).getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                }
            }
        });

//        final XHCustomRecorder recorder = new XHCustomRecorder(480,480,0,false);
//        voipManager.setRecorder(recorder);
//        pushUVCTest = new PushUVCTest(recorder);
//        pushUVCTest.startRecoder();


        ((TextView) findViewById(R.id.targetid_text)).setText(targetId);
//        ((ImageView)findViewById(R.id.head_img)).setImageResource(MLOC.getHeadImage(VoipActivity.this,targetId));
        findViewById(R.id.calling_hangup).setOnClickListener(this);
        findViewById(R.id.talking_hangup).setOnClickListener(this);
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voipManager.switchCamera();
            }
        });
        findViewById(R.id.screen_btn).setOnClickListener(this);
        findViewById(R.id.mic_btn).setSelected(true);
        findViewById(R.id.mic_btn).setOnClickListener(this);
        findViewById(R.id.camera_btn).setSelected(true);
        findViewById(R.id.camera_btn).setOnClickListener(this);
        findViewById(R.id.speaker_on_btn).setOnClickListener(this);
        findViewById(R.id.speaker_off_btn).setOnClickListener(this);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (action.equals(CALLING)) {
            showCallingView();
            eLog(this, "newVoip-CALLING", "TAG");
            xhsdkHelper = new XHSDKHelper();
            xhsdkHelper.setDefaultCameraId(cameraId);
            xhsdkHelper.startPerview(this, ((StarPlayer) findViewById(R.id.voip_surface_target)));
//            voipManager.addListener();
            voipManager.call(this, targetId, new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                    eLog(data, "newVoip-call success! RecSessionId:", "TAG");
                }

                @Override
                public void failed(String errMsg) {
                    eLog(this, "newVoip", "TAG");
                    stopAndFinish();
                }
            });
        } else {
            eLog(this, "newVoip", "TAG");
            onPickup();
        }
//        if (isRemoteVideo){
//            onClick(findViewById(R.id.camera_btn));
//        }
        findViewById(R.id.editText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }, 300);
                }
            }
        });
        findViewById(R.id.editText).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT) {
                        status = true;
                        return true;
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_0) {
                        Boolean st = findViewById(R.id.calling_view).getVisibility() == View.VISIBLE;
                        if (st)
                            findViewById(R.id.calling_hangup).callOnClick();
                        else
                            findViewById(R.id.talking_hangup).callOnClick();
                    }
                    if (status) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_8:
                                Boolean st = findViewById(R.id.calling_view).getVisibility() == View.VISIBLE;
                                if (st)
                                    findViewById(R.id.calling_hangup).callOnClick();
                                else
                                    findViewById(R.id.talking_hangup).callOnClick();
                                break;
                        }
                    }
                }
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.editText).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);
    }

    private void setupViews() {
        voipManager.setupView(selfPlayer, targetPlayer, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                eLog(data, "newVoip- setupView success", "TAG");
            }

            @Override
            public void failed(String errMsg) {
                eLog(errMsg, "setupView failed", "TAG");
                stopAndFinish();
            }
        });
    }

    public void addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED, this);
    }

    public void removeListener() {
        MLOC.INSTANCE.setCanPickupVoip(true);
        AEvent.removeListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MLOC.INSTANCE.setCanPickupVoip(false);
        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_VOIP);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setConversationId(targetId);
        historyBean.setNewMsgCount(1);
        MLOC.INSTANCE.addHistory(historyBean, true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        addListener();
    }

    @Override
    public void onDestroy() {
        removeListener();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(VoipActivity.this).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mTalkingTiTask.cancel();
                        voipManager.hangup(new IXHResultCallback() {
                            @Override
                            public void success(Object data) {
                                removeListener();
                                stopAndFinish();
                            }

                            @Override
                            public void failed(final String errMsg) {
                                eLog(errMsg, "AEVENT_VOIP_ON_STOP errMsg:", "TAG");
                                MLOC.INSTANCE.showMsg(VoipActivity.this, errMsg);
                            }
                        });
                    }
                }
        ).show();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID, success, eventObj);
        eLog(this, "dispatchEvent:" + aEventID + "--" + success + "--" + eventObj, "TAG");
        switch (aEventID) {
            case AEvent.AEVENT_VOIP_REV_BUSY:
                eLog(this, "对方线路忙", "TAG");
                MLOC.INSTANCE.showMsg(VoipActivity.this, "对方线路忙");
                if (xhsdkHelper != null) {
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_REFUSED:
                eLog(this, "对方拒绝通话", "TAG");
                MLOC.INSTANCE.showMsg(VoipActivity.this, "对方拒绝通话");
                if (xhsdkHelper != null) {
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_HANGUP:
                eLog(this, "对方已挂断", "TAG");
                MLOC.INSTANCE.showMsg(VoipActivity.this, "对方已挂断");
                mTalkingTiTask.cancel();
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_REV_CONNECT:
                eLog(this, "对方允许通话", "TAG");
                mTimerTask.cancel();
                showTalkingView();
                break;
            case AEvent.AEVENT_VOIP_REV_ERROR:
                eLog(this, "AEVENT_VOIP_REV_ERROR", (String) eventObj);
                if (xhsdkHelper != null) {
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                }
                stopAndFinish();
                break;
            case AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED:
                findViewById(R.id.state).setBackgroundColor(((int) eventObj == 0) ? 0xFFFFFF00 : 0xFF299401);
                break;
            case AEvent.AEVENT_C2C_REV_MSG:
             String[] strs=   ((XHIMMessage) eventObj).contentData.split("=");
             if (strs[0].equals("targetPlayer")){
                 if (strs[1].equals("true")){
                     //隐藏广告
                     findViewById(R.id.advertising).setVisibility(View.GONE);
                     eLog(this, "隐藏广告","TAG");
                 }else{
                     File file=new File("/sdcard/SAK-RTC-AVD.png");
                     findViewById(R.id.advertising).setVisibility(View.VISIBLE);
                     if (file.exists()){
                         ((ImageView ) findViewById(R.id.advertising)).setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                     }else{
                         ((ImageView ) findViewById(R.id.advertising)).setBackgroundColor(Color.BLACK);
                     }
//                     targetPlayer.setBackground();
                     eLog(this, "显示广告","TAG");
                 }

             }
                break;
        }
    }


    private void showCallingView() {
        findViewById(R.id.calling_view).setVisibility(View.VISIBLE);
        findViewById(R.id.talking_view).setVisibility(View.GONE);
    }

    private void showTalkingView() {
        isTalking = true;
        findViewById(R.id.calling_view).setVisibility(View.GONE);
        findViewById(R.id.talking_view).setVisibility(View.VISIBLE);

        Long[] time;
        try {
            time = new Long[]{Long.parseLong(MLOC.INSTANCE.getMaxTime())};
        } catch (NumberFormatException e) {
            time = new Long[]{outTime};
        }
        final Long[] finalTime = time;
        mTalkingTiTask = new TimerTask() {
            @Override
            public void run() {
                finalTime[0]--;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvTimer.setText("剩余时间:" + finalTime[0]);
                        if (finalTime[0] == 0) {
                            onClick(null);
                            eLog(this, "倒计时关闭", "TAG");
                        }
                    }
                });
            }
        };
        new Timer().schedule(mTalkingTiTask, outTime, 1000L);

        setupViews();
    }

    private void onPickup() {
        voipManager.accept(this, targetId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                eLog(data, "newVoip-onPickup OK! RecSessionId:", "TAG");
            }

            @Override
            public void failed(String errMsg) {
                eLog(this, "newVoip-onPickup failed ", "TAG");
                stopAndFinish();
            }
        });
        showTalkingView();
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            voipManager.hangup(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    stopAndFinish();
                }

                @Override
                public void failed(String errMsg) {
                    stopAndFinish();
                }
            });
            return;
        }
        int i = v.getId();
        if (i == R.id.calling_hangup) {
            voipManager.cancel(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    stopAndFinish();
                }

                @Override
                public void failed(String errMsg) {
                    stopAndFinish();
                }
            });
            if (xhsdkHelper != null) {
                xhsdkHelper.stopPerview();
                xhsdkHelper = null;
            }

        } else if (i == R.id.talking_hangup) {
            voipManager.hangup(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    stopAndFinish();
                }

                @Override
                public void failed(String errMsg) {
                    stopAndFinish();
                }
            });

        } else if (i == R.id.screen_btn) {
            if (!XHCustomConfig.getInstance(this).getHardwareEnable()) {
                MLOC.INSTANCE.showMsg(this, "需要打开硬编模式");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mRecorder != null) {
                    findViewById(R.id.screen_btn).setSelected(false);
                    voipManager.resetRecorder(new XHCameraRecorder());
                    mRecorder = null;
                } else {
                    if (mMediaProjectionManager == null) {
                        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    }
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
            } else {
                MLOC.INSTANCE.showMsg(this, "系统版本过低，无法使用录屏功能");
            }

        } else if (i == R.id.camera_btn) {
            if (findViewById(R.id.camera_btn).isSelected()) {
                findViewById(R.id.camera_btn).setSelected(false);
                voipManager.setVideoEnable(false);
            } else {
                findViewById(R.id.camera_btn).setSelected(true);
                voipManager.setVideoEnable(true);
            }

        } else if (i == R.id.mic_btn) {
            if (findViewById(R.id.mic_btn).isSelected()) {
                findViewById(R.id.mic_btn).setSelected(false);
                voipManager.setAudioEnable(false);
            } else {
                findViewById(R.id.mic_btn).setSelected(true);
                voipManager.setAudioEnable(true);
            }

        } else if (i == R.id.speaker_on_btn) {//                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE);
            starRTCAudioManager.setSpeakerphoneOn(true);
            findViewById(R.id.speaker_on_btn).setSelected(true);
            findViewById(R.id.speaker_off_btn).setSelected(false);

        } else if (i == R.id.speaker_off_btn) {//                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.EARPIECE);
            starRTCAudioManager.setSpeakerphoneOn(false);
            findViewById(R.id.speaker_on_btn).setSelected(false);
            findViewById(R.id.speaker_off_btn).setSelected(true);
        }
    }

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private XHScreenRecorder mRecorder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRecorder = new XHScreenRecorder(this, resultCode, data);
        voipManager.resetRecorder(mRecorder);
        findViewById(R.id.screen_btn).setSelected(true);
    }

    public void stopAndFinish() {
        if (starRTCAudioManager != null) {
            starRTCAudioManager.stop();
        }
        VoipActivity.this.finish();
    }


}
