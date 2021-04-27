package com.anubis.app_coroutine


import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_eventbus.observe.eObserveEvent
import kotlinx.coroutines.*


class MyService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        try {
          eObserveEvent(this,"0"){
              it.eLog("MyService-eObserveEvent0 ")
          }
            GlobalScope.launch {
                eLog("DaemonThread: 守护线程启动")
                while (true) {
                    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val tasks = am.getRunningTasks(2)
                    //守护
                    runBlocking {
                        tasks.forEach {
                            if (it.baseActivity.packageName == packageName)
                                return@runBlocking
                        }
                        eLog("守护执行")
                        startActivity(packageManager.getLaunchIntentForPackage(application.packageName))
                    }
                    delay(10000L)
                    eLog("MyService 运行中")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            eLogE("run: 守护线程发生错误", e)
        }
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY        //保活的一种
    }

}



