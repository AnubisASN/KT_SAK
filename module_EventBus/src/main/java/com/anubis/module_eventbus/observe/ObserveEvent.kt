package com.anubis.module_eventbus.observe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.anubis.kt_extends.eBReceiver
import com.anubis.kt_extends.eEvent
import com.anubis.kt_extends.eLog
import com.anubis.module_eventbus.core.EventBusCore
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.store.ApplicationScopeViewModelProvider
import kotlinx.coroutines.*
import java.io.Serializable

//_______________________________________
//          observe event
//_______________________________________


inline fun <reified T> LifecycleOwner.eObserveEvent(
    scope: Fragment? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
) = when (scope) {
    //Activity Scope 事件
    is ComponentActivity -> ViewModelProvider(scope).get(EventBusCore::class.java)
        .observeEvent(
            scope,
            T::class.java.name,
            minActiveState,
            dispatcher,
            isSticky,
            onReceived
        )
    //监听Fragment Scope 事件
    is Fragment -> ViewModelProvider(scope).get(EventBusCore::class.java)
        .observeEvent(
            scope,
            T::class.java.name,
            minActiveState,
            dispatcher,
            isSticky,
            onReceived
        )
    //监听App Scope 事件
    else -> ApplicationScopeViewModelProvider.getApplicationScopeViewModel(EventBusCore::class.java)
        ?.observeEvent(
            this,
            T::class.java.name,
            minActiveState,
            dispatcher,
            isSticky,
            onReceived
        )
}

//线程监听
inline fun <reified T> eObserveEvent(
    coroutineScope: CoroutineScope,
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
) {
    coroutineScope.launch {
        ApplicationScopeViewModelProvider.getApplicationScopeViewModel(EventBusCore::class.java)
            ?.observeWithoutLifecycle(
                T::class.java.name,
                isSticky,
                onReceived
            )
    }
}

//app  进程跨越
  fun <T> eObserveEvent(context: Context, defValue: T , block: (T ) -> Unit) {
    eBReceiver.eInit.eSetRegisterReceiver(context,
        IntentFilter("eEventBus")
    ) { context: Context, intent: Intent, mReceiver: BroadcastReceiver ->
        val value = when (defValue) {
            is Boolean -> intent.getBooleanExtra("eEventBus", defValue)
            is Float -> intent.getFloatExtra("eEventBus", defValue)
            is Int -> intent.getIntExtra("eEventBus", defValue)
            is Long -> intent.getLongExtra("eEventBus", defValue)
            is Double -> intent.getDoubleExtra("eEventBus", defValue)
            is Byte -> intent.getByteExtra("eEventBus", defValue)
            is Short -> intent.getShortExtra("eEventBus", defValue)
            is Serializable -> intent.getSerializableExtra("eEventBus")
            else -> intent.getStringExtra("eEventBus")
        }
        block(value as T)
    }
}
