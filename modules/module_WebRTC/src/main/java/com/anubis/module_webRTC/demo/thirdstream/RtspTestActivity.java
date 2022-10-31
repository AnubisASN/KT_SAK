package com.anubis.module_webRTC.demo.thirdstream;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.listener.XHChatroomManagerListener;
import com.anubis.module_webRTC.serverAPI.InterfaceUrls;
import com.anubis.module_webRTC.utils.AEvent;
import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.listener.XHChatroomManagerListener;
import com.anubis.module_webRTC.serverAPI.InterfaceUrls;
import com.anubis.module_webRTC.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHChatroomManager;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RtspTestActivity extends BaseActivity {

    private String name;
    private String streamUrl;
    private String roomId;
    private XHChatroomManager chatroomManager;
    private int createType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_rtsp_test_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("第三方拉流");
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.yes_btn_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPush(MLOC.INSTANCE.getLIST_TYPE_LIVE_PUSH());
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.yes_btn_meeting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPush(MLOC.INSTANCE.getLIST_TYPE_MEETING_PUSH());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AEvent.addListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_ERROR,this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AEvent.removeListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_ERROR,this);
    }
    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj){
        super.dispatchEvent(aEventID,success,eventObj);
        if(aEventID.equals(AEvent.AEVENT_RTSP_FORWARD)){
            if(success){
                try {
                    JSONObject jsonObject = new JSONObject((String) eventObj);
                    if(jsonObject.has("channelId")){
                        try {
                            if(createType==1) {
                                JSONObject info = new JSONObject();
                                info.put("id",jsonObject.getString("channelId")+roomId);
                                info.put("creator", MLOC.INSTANCE.getUserId());
                                info.put("name",name);
                                info.put("url",streamUrl);
                                info.put("listType", MLOC.INSTANCE.getLIST_TYPE_LIVE_PUSH());
                                String infostr = info.toString();
                                infostr = URLEncoder.encode(infostr,"utf-8");
                                if(MLOC.INSTANCE.getAEventCenterEnable()){
                                    InterfaceUrls.demoSaveToList(MLOC.INSTANCE.getUserId(), MLOC.INSTANCE.getLIST_TYPE_LIVE_PUSH(),jsonObject.getString("channelId")+roomId,infostr);
                                }else{
                                    chatroomManager.saveToList(MLOC.INSTANCE.getUserId(), MLOC.INSTANCE.getLIST_TYPE_LIVE_PUSH(), roomId, infostr, null);
                                }
                                MLOC.INSTANCE.showMsg(RtspTestActivity.this,"拉流成功,请到互动直播查看");
                            }else {
                                JSONObject info = new JSONObject();
                                info.put("id",jsonObject.getString("channelId")+roomId);
                                info.put("creator", MLOC.INSTANCE.getUserId());
                                info.put("name",name);
                                info.put("url",streamUrl);
                                info.put("listType", MLOC.INSTANCE.getLIST_TYPE_MEETING_PUSH());
                                String infostr = info.toString();
                                infostr = URLEncoder.encode(infostr,"utf-8");
                                if(MLOC.INSTANCE.getAEventCenterEnable()){
                                    InterfaceUrls.demoSaveToList(MLOC.INSTANCE.getUserId(), MLOC.INSTANCE.getLIST_TYPE_MEETING_PUSH(),jsonObject.getString("channelId")+roomId,infostr);
                                }else {
                                    chatroomManager.saveToList(MLOC.INSTANCE.getUserId(), MLOC.INSTANCE.getLIST_TYPE_MEETING_PUSH(), roomId, infostr, null);
                                }
                                MLOC.INSTANCE.showMsg(RtspTestActivity.this,"拉流成功,请到视频会议查看");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RtspTestActivity.this.finish();
            }else{
                MLOC.INSTANCE.showMsg(RtspTestActivity.this,"拉流失败"+(String)eventObj);
            }
        }else if(aEventID .equals(AEvent.AEVENT_CHATROOM_ERROR)){
            MLOC.INSTANCE.showMsg(RtspTestActivity.this,"ERROR:"+eventObj);
        }
    }

    private void createAndPush(final int type){
        name = ((EditText)findViewById(com.anubis.module_webRTC.R.id.targetid_input)).getText().toString();
        streamUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.rtsp_input)).getText().toString();
//        url = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
//        url = "rtmp://liveali.ifeng.com/live/FHZX";
        createType = type;//1 meeting 2 live
        if(TextUtils.isEmpty(name)){
            MLOC.INSTANCE.showMsg(RtspTestActivity.this,"名字不能为空");
        }else if(TextUtils.isEmpty(streamUrl)){
            MLOC.INSTANCE.showMsg(RtspTestActivity.this,"拉流地址不能为空");
        }else{
            chatroomManager = XHClient.getInstance().getChatroomManager();
            chatroomManager.addListener(new XHChatroomManagerListener());
            chatroomManager.createChatroom(name,XHConstants.XHChatroomType.XHChatroomTypePublic,new IXHResultCallback() {
                @Override
                public void success(final Object data) {
                    roomId = data.toString();
                    String streamType = "";
                    if(streamUrl.indexOf("rtsp://")==0){
                        streamType = "rtsp";
                    }else if(streamUrl.indexOf("rtmp://")==0){
                        streamType = "rtmp";
                    }
                    if(!streamType.equals("")){
                        InterfaceUrls.demoPushStreamUrl(MLOC.INSTANCE.getUserId(), MLOC.INSTANCE.getLIVE_PROXY_SERVER_URL(),name,roomId,type,streamType,streamUrl);
                    }else{
                        MLOC.INSTANCE.showMsg(RtspTestActivity.this,"拉流地址不可用");
                    }
                }

                @Override
                public void failed(String errMsg) {
                    final String err = errMsg;
                    MLOC.INSTANCE.showMsg(RtspTestActivity.this,err.toString());
                }
            });
        }
    }

}
