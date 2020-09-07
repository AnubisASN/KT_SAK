package com.anubis.module_webRTC.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.IEventListener
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage
import org.json.JSONException
import org.json.JSONObject

open class BaseActivity : androidx.appcompat.app.AppCompatActivity(), IEventListener {

    override fun onResume() {
        super.onResume()
        if (findViewById<View>(R.id.c2c_new) != null) {
            findViewById<View>(R.id.c2c_new).visibility = if (MLOC.hasNewC2CMsg) View.VISIBLE else View.INVISIBLE
        }
        if (findViewById<View>(R.id.im_new) != null) {
            findViewById<View>(R.id.im_new).visibility = if (MLOC.hasNewC2CMsg || MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
        }
        if (findViewById<View>( R.id.group_new) != null) {
            findViewById<View>(R.id.group_new).visibility = if (MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
        }
        if (findViewById<View>(R.id.im_new) != null) {
            findViewById<View>(R.id.im_new).visibility = if (MLOC.hasNewC2CMsg || MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
        }
        if (findViewById<View>(R.id.voip_new) != null) {
            findViewById<View>(R.id.voip_new).visibility = if (MLOC.hasNewVoipMsg) View.VISIBLE else View.INVISIBLE
        }
        if (findViewById<View>(R.id.loading) != null) {
            if (XHClient.getInstance().isOnline) {
                findViewById<View>(R.id.loading).visibility = View.INVISIBLE
            } else {
                findViewById<View>(R.id.loading).visibility = View.VISIBLE
            }
        }
        addListener()
    }

    override fun onPause() {
        super.onPause()
        removeListener()
    }

    private fun addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this)
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG, this)
        AEvent.addListener(AEvent.AEVENT_REV_SYSTEM_MSG, this)
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG, this)
        AEvent.addListener(AEvent.AEVENT_USER_ONLINE, this)
        AEvent.addListener(AEvent.AEVENT_USER_OFFLINE, this)
        AEvent.addListener(AEvent.AEVENT_CONN_DEATH, this)
    }

    private fun removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this)
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG, this)
        AEvent.removeListener(AEvent.AEVENT_REV_SYSTEM_MSG, this)
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG, this)
        AEvent.removeListener(AEvent.AEVENT_USER_ONLINE, this)
        AEvent.removeListener(AEvent.AEVENT_USER_OFFLINE, this)
        AEvent.removeListener(AEvent.AEVENT_CONN_DEATH, this)
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        when (aEventID) {
            AEvent.AEVENT_VOIP_REV_CALLING -> {
            }
            AEvent.AEVENT_VOIP_P2P_REV_CALLING -> {
            }
            AEvent.AEVENT_C2C_REV_MSG, AEvent.AEVENT_REV_SYSTEM_MSG -> {
                MLOC.hasNewC2CMsg = true
                if (findViewById<View>(R.id.c2c_new) != null) {
                    findViewById<View>(R.id.c2c_new).visibility = if (MLOC.hasNewC2CMsg) View.VISIBLE else View.INVISIBLE
                }
                if (findViewById<View>(R.id.im_new) != null) {
                    findViewById<View>(R.id.im_new).visibility = if (MLOC.hasNewC2CMsg || MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
                }
                try {
                    val revMsg = eventObj as XHIMMessage
                    val alertData = JSONObject()
                    alertData.put("listType", 0)
                    alertData.put("farId", revMsg.fromId)
                    alertData.put("msg", "收到一条新消息")
                    MLOC.showDialog(this@BaseActivity, alertData)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            AEvent.AEVENT_GROUP_REV_MSG -> {
                MLOC.hasNewGroupMsg = true
                if (findViewById<View>(R.id.group_new) != null) {
                    findViewById<View>(R.id.group_new).visibility = if (MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
                }
                if (findViewById<View>(R.id.im_new) != null) {
                    findViewById<View>(R.id.im_new).visibility = if (MLOC.hasNewC2CMsg || MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
                }
                try {
                    val revMsg = eventObj as XHIMMessage
                    val alertData = JSONObject()
                    alertData.put("listType", 1)
                    alertData.put("farId", revMsg.targetId)
                    alertData.put("msg", "收到一条群消息")
                    MLOC.showDialog(this@BaseActivity, alertData)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            AEvent.AEVENT_USER_OFFLINE -> {
                MLOC.showMsg(this@BaseActivity, "服务已断开")
                (findViewById<View>(R.id.loading) as TextView).text = "连接中..."
                if (findViewById<View>(R.id.loading) != null) {
                    if (XHClient.getInstance().isOnline) {
                        findViewById<View>(R.id.loading).visibility = View.INVISIBLE
                    } else {
                        findViewById<View>(R.id.loading).visibility = View.VISIBLE
                    }
                }
            }
            AEvent.AEVENT_USER_ONLINE -> if (findViewById<View>(R.id.loading) != null) {
                if (XHClient.getInstance().isOnline) {
                    findViewById<View>(R.id.loading).visibility = View.INVISIBLE
                } else {
                    findViewById<View>(R.id.loading).visibility = View.VISIBLE
                }
                (findViewById<View>(R.id.userinfo_head) as ImageView).setImageResource(MLOC.getHeadImage(this, MLOC.userId!!))
                (findViewById<View>(R.id.userinfo_id) as TextView).text = MLOC.userId
            }
            AEvent.AEVENT_CONN_DEATH -> {
                MLOC.showMsg(this@BaseActivity, "服务已断开")
                if (findViewById<View>(R.id.loading) != null) {
                    (findViewById<View>(R.id.loading) as TextView).text = "连接异常，请重新登录"
                    if (XHClient.getInstance().isOnline) {
                        findViewById<View>(R.id.loading).visibility = View.INVISIBLE
                    } else {
                        findViewById<View>(R.id.loading).visibility = View.VISIBLE
                    }
                    (findViewById<View>(R.id.userinfo_head) as ImageView).setImageResource(MLOC.getHeadImage(this, MLOC.userId!!))
                    (findViewById<View>(R.id.userinfo_id) as TextView).text = MLOC.userId
                }
            }
        }
        //                if(!MLOC.canPickupVoip){
        //                    MLOC.hasNewVoipMsg = true;
        //                    try {
        //                        JSONObject alertData = new JSONObject();
        //                        alertData.put("listType",2);
        //                        alertData.put("farId",eventObj.toString());
        //                        alertData.put("msg","收到视频通话请求");
        //                        MLOC.showDialog(BaseActivity.this,alertData);
        //                    } catch (JSONException e) {
        //                        e.printStackTrace();
        //                    }
        //                }
        //                if(MLOC.canPickupVoip){
        //                    MLOC.hasNewVoipMsg = true;
        //                    Intent intent = new Intent(BaseActivity.this,VoipP2PRingingActivity.class);
        //                    intent.putExtra("targetId",eventObj.toString());
        //                    startActivity(intent);
        //                }
    }

}
