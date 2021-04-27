package com.anubis.module_eventbus.post

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anubis.module_eventbus.core.EventBusCore
import com.anubis.module_eventbus.store.ApplicationScopeViewModelProvider


inline fun <reified T> eRemoveStickyEvent(scope: Any?=null, event: Class<T>) =when(scope){
    is ComponentActivity-> ViewModelProvider(scope).get(EventBusCore::class.java)
        .removeStickEvent(event.name)
    is Fragment-> ViewModelProvider(scope).get(EventBusCore::class.java)
        .removeStickEvent(event.name)
    else->ApplicationScopeViewModelProvider.getApplicationScopeViewModel(EventBusCore::class.java)
        ?.removeStickEvent(event.name)
}

