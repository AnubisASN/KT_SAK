package com.anubis.app_nertc
import com.anubis.app_nertc.Utils.token
import com.anubis.app_nertc.Utils.userId
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.netease.lava.nertc.sdk.NERtcCallback
import com.netease.lava.nertc.sdk.NERtcEx
import com.netease.lava.nertc.sdk.video.NERtcVideoView


/**
 * Author  ： AnubisASN   on 21-6-8 上午8:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
object NeRTC {
// 示例
    private var remoteView:NERtcVideoView?=null
      var callback:NERtcCallback  get()  = object :NERtcCallback{
    override fun onUserVideoStop(p0: Long) {
    }

    override fun onUserAudioStop(p0: Long) {
    }

    override fun onUserLeave(p0: Long, p1: Int) {
    }

    override fun onJoinChannel(p0: Int, p1: Long, p2: Long) {
    }

    override fun onUserVideoStart(p0: Long, p1: Int) {
        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteView, p0)
    }

    override fun onUserJoined(p0: Long) {
    }

    override fun onUserAudioStart(p0: Long) {
    }

    override fun onLeaveChannel(p0: Int) {
    }

    override fun onDisconnect(p0: Int) {
    }

    override fun onClientRoleChange(p0: Int, p1: Int) {
    }

}
 set(value) {}

    fun    initializeSDK(callback:NERtcCallback) {
        try {
      NERtcEx.getInstance().init(APP.mAPP,Utils.appKey,callback,null);
        } catch (e:Exception ) {
     e.eLogE("初始化失败")
        }
    eLog("初始化成功")
    }
    fun joinChannel(channelName:String,localView:NERtcVideoView,remoteView:NERtcVideoView){
        this.remoteView=remoteView
        // 示例
        NERtcEx.getInstance().joinChannel(token,channelName, userId)
        // 示例
        // 开启本地视频采集并发送
        // 示例
        // 开启本地视频采集并发送
        NERtcEx.getInstance().enableLocalVideo(true)
        // 设置本地预览画布
        // 设置本地预览画布
        NERtcEx.getInstance().setupLocalVideoCanvas(localView)
    }
fun closeChannel(){
    // 退出通话房间
    NERtcEx.getInstance().leaveChannel()
}
    fun Destroy(){
        // 销毁实例
        NERtcEx.getInstance().release();
    }
}
