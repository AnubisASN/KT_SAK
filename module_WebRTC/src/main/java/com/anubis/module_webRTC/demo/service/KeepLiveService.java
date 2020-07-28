package com.anubis.module_webRTC.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.anubis.module_webRTC.demo.beauty.DemoVideoSourceCallback;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.demo.p2p.VoipP2PRingingActivity;
import com.anubis.module_webRTC.demo.voip.VoipAudioRingingActivity;
import com.anubis.module_webRTC.demo.voip.VoipRingingActivity;
import com.anubis.module_webRTC.listener.XHChatManagerListener;
import com.anubis.module_webRTC.listener.XHGroupManagerListener;
import com.anubis.module_webRTC.listener.XHLoginManagerListener;
import com.anubis.module_webRTC.listener.XHVoipManagerListener;
import com.anubis.module_webRTC.listener.XHVoipP2PManagerListener;
import com.anubis.module_webRTC.utils.AEvent;
import com.anubis.module_webRTC.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.apiInterface.IXHErrorCallback;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.videosrc.XHVideoSourceManager;

import java.util.Random;


/**
 * Created by zhangjt on 2017/8/6.
 */

public class KeepLiveService extends Service implements IEventListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        removeListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSDK();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSDK(){
        MLOC.INSTANCE.init(this);
        initFree();
    }

    private boolean isLogin = false;
    //开放版SDK初始化
    private void initFree(){
        MLOC.INSTANCE.d("KeepLiveService","initFree");
        isLogin = XHClient.getInstance().getIsOnline();
        if(!isLogin){
            if(MLOC.INSTANCE.getUserId().equals("")){
                MLOC.INSTANCE.setUserId("" + (new Random().nextInt(900000) + 100000));
                MLOC.INSTANCE.saveUserId(MLOC.INSTANCE.getUserId());
            }
            addListener();

            XHCustomConfig customConfig =  XHCustomConfig.getInstance(this);
            customConfig.setChatroomServerUrl(MLOC.INSTANCE.getCHATROOM_SERVER_URL());
            customConfig.setLiveSrcServerUrl(MLOC.INSTANCE.getLIVE_SRC_SERVER_URL());
            customConfig.setLiveVdnServerUrl(MLOC.INSTANCE.getLIVE_VDN_SERVER_URL());
            customConfig.setImServerUrl(MLOC.INSTANCE.getIM_SERVER_URL());
            customConfig.setVoipServerUrl(MLOC.INSTANCE.getVOIP_SERVER_URL());
//            customConfig.setLogEnable(false); //关闭SDK调试日志
//            customConfig.setDefConfigOpenGLESEnable(false);
//            customConfig.setDefConfigCameraId(1);//设置默认摄像头方向  0后置  1前置
//            customConfig.setDefConfigVideoSize(XHConstants.XHCropTypeEnum.STAR_VIDEO_CONFIG_360BW_640BH_180SW_320SH);
//            customConfig.setLogDirPath(Environment.getExternalStorageDirectory().getPath()+"/starrtcLog");
//            customConfig.setDefConfigCamera2Enable(false);
//            StarCamera.setFrameBufferEnable(false);
            customConfig.initSDKForFree(MLOC.INSTANCE.getUserId(), new IXHErrorCallback() {
                @Override
                public void error(final String errMsg, Object data) {
                    MLOC.INSTANCE.e("KeepLiveService","error:"+errMsg);
                    MLOC.INSTANCE.showMsg( errMsg);
                }
            },new Handler());

            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getGroupManager().addListener(new XHGroupManagerListener());
            XHClient.getInstance().getVoipManager().addListener(new XHVoipManagerListener());
            XHClient.getInstance().getVoipP2PManager().addListener(new XHVoipP2PManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());
            XHVideoSourceManager.getInstance().setVideoSourceCallback(new DemoVideoSourceCallback());

            XHClient.getInstance().getLoginManager().loginFree(new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    MLOC.INSTANCE.d("KeepLiveService","loginSuccess");
                    isLogin = true;
                }
                @Override
                public void failed(final String errMsg) {
                    MLOC.INSTANCE.d("KeepLiveService","loginFailed "+errMsg);
                    MLOC.INSTANCE.showMsg( errMsg);
                }
            });
        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_VOIP_REV_CALLING:{
                Intent intent = new Intent(this, VoipRingingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("targetId",eventObj.toString());
                startActivity(intent);
            }
            break;
            case AEvent.AEVENT_VOIP_REV_CALLING_AUDIO:{
                Intent intent = new Intent(this, VoipAudioRingingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("targetId",eventObj.toString());
                startActivity(intent);
            }
            break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING:
                if(MLOC.INSTANCE.getCanPickupVoip()){
                    Intent intent = new Intent(this, VoipP2PRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING_AUDIO:
                if(MLOC.INSTANCE.getCanPickupVoip()){
                    Intent intent = new Intent(this, VoipP2PRingingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("targetId",eventObj.toString());
                    startActivity(intent);
                }
                break;
            case AEvent.AEVENT_C2C_REV_MSG:
            case AEvent.AEVENT_REV_SYSTEM_MSG:
                MLOC.INSTANCE.setHasNewC2CMsg(true);
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                MLOC.INSTANCE.setHasNewGroupMsg(true);
                break;
            case AEvent.AEVENT_LOGOUT:
                removeListener();
                this.stopSelf();
                break;
            case AEvent.AEVENT_USER_KICKED:
            case AEvent.AEVENT_CONN_DEATH:
                MLOC.INSTANCE.d("KeepLiveService","AEVENT_USER_KICKED OR AEVENT_CONN_DEATH");
                XHClient.getInstance().getLoginManager().loginFree(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {
                        MLOC.INSTANCE.d("KeepLiveService","loginSuccess");
                        isLogin = true;
                    }
                    @Override
                    public void failed(final String errMsg) {
                        MLOC.INSTANCE.d("KeepLiveService","loginFailed "+errMsg);
                        MLOC.INSTANCE.showMsg( errMsg);
                    }
                });
                break;
        }
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_REV_SYSTEM_MSG,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_USER_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_CONN_DEATH,this);
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LOGOUT,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO,this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_REV_SYSTEM_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_USER_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_CONN_DEATH,this);
    }

}
