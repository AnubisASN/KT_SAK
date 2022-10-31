package com.anubis.module_eventbus

import android.app.Application
import com.alibaba.android.arouter.facade.template.ILogger

object eEventBus {
    lateinit var application: Application
/**初始化
 *@param application: Application,app全局
 * @param logger: ILogger? = null
 * */
    fun eInit(application: Application) {
        eEventBus.application = application
    }
}
