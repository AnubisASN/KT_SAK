package com.anubis.module_eventbus.post

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anubis.kt_extends.eBReceiver
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eRegex
import com.anubis.module_eventbus.core.EventBusCore
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.store.ApplicationScopeViewModelProvider
import java.io.Serializable

//_______________________________________
//          post event
//_______________________________________

/**消息总线
 * @param event: T,消息内容
 * @param scope:Any?=null,协程范围
 * @param timeMillis: Long = 0L，延时时间
 * */
inline fun <reified T> ePostEvent(event: T, scope: Any? = null, timeMillis: Long = 0L) =
    when (scope) {
        //Activity范围的事件
        is ComponentActivity -> ViewModelProvider(scope).get(EventBusCore::class.java)
            .postEvent(T::class.java.name, event!!, timeMillis)
        //Fragment范围的事件
        is Fragment -> ViewModelProvider(scope).get(EventBusCore::class.java)
            .postEvent(T::class.java.name, event!!, timeMillis)
        //Application范围的事件
        else -> ApplicationScopeViewModelProvider.getApplicationScopeViewModel(EventBusCore::class.java)
            ?.postEvent(T::class.java.name, event!!, timeMillis)
    }

/**跨进程发送
 * @param  value: Any，消息内容
* */
  fun  ePostSpan( value: Any,tIntent:Intent?=null,intent:Intent=Intent("eEventBus")) {
    tIntent?.let { intent.putExtras(it) }
    when (value) {
        is String -> intent.putExtra("eEventBus", value)
        is Boolean -> intent.putExtra("eEventBus", value)
        is Float -> intent.putExtra("eEventBus", value)
        is Int -> intent.putExtra("eEventBus", value)
        is Long -> intent.putExtra("eEventBus", value)
        is Double -> intent.putExtra("eEventBus", value)
        is Byte -> intent.putExtra("eEventBus", value)
        is Short -> intent.putExtra("eEventBus", value)
        is Serializable -> intent.putExtra("eEventBus", value)
    }
    eEventBus.application.sendBroadcast(intent)
}

/**移除粘性事件
* @param scope: Any?=null,协程范围
 * @param event: Class<T> 消息内容类型
* */
inline fun <reified T> eRemoveStickyEvent( event: T ,scope: Any?=null) =when(scope){
    is ComponentActivity-> ViewModelProvider(scope).get(EventBusCore::class.java)
        .removeStickEvent(T::class.java.name)
    is Fragment-> ViewModelProvider(scope).get(EventBusCore::class.java)
        .removeStickEvent(T::class.java.name)
    else->ApplicationScopeViewModelProvider.getApplicationScopeViewModel(EventBusCore::class.java)
        ?.removeStickEvent(T::class.java.name)
}




