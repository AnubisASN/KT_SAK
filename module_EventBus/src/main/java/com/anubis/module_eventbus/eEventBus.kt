package com.anubis.module_eventbus

import android.app.Application
import com.anubis.module_eventbus.util.ILogger

object eEventBus {
    lateinit var application: Application
    var logger: ILogger? = null

    fun eInit(application: Application, logger: ILogger? = null) {
        eEventBus.application = application
        eEventBus.logger = logger
    }
}
