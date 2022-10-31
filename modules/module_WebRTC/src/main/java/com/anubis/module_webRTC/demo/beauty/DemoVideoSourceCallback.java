package com.anubis.module_webRTC.demo.beauty;

import com.anubis.module_webRTC.demo.MLOC;
import com.starrtc.starrtcsdk.core.videosrc.StarAudioData;
import com.starrtc.starrtcsdk.core.videosrc.XHVideoSourceCallback;
import com.starrtc.starrtcsdk.core.videosrc.StarVideoData;

public class DemoVideoSourceCallback extends XHVideoSourceCallback {
    @Override
    public StarVideoData onVideoFrame(StarVideoData videoData){
        MLOC.INSTANCE.d("DemoVideoSourceCallback","视频源数据已经接到了，不做处理，直接再丢回去"+videoData.getDataLength());
        //可直接对videoData里的数据进行处理，处理后将videoData对象返回即可。
        return videoData;
    }
    @Override
    public StarAudioData onAudioFrame(StarAudioData audioData){
        MLOC.INSTANCE.d("DemoVideoSourceCallback","音频源数据已经接到了，不做处理，直接再丢回去"+audioData.getDataLength());
        //可直接对audioData里的数据进行处理，处理后将audioData对象返回即可。
        return audioData;
    }
}
