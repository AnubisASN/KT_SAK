package com.anubis.SwissArmyKnife


import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log


class MyService : Service() {

    private var mRunnable: Runnable? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("TAG", "DaemonThread: 守护线程启动")
        mRunnable = Runnable {
            try {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val cn = am.getRunningTasks(1)[0].topActivity
            Log.i("TAG", "守护线程run: " + cn.packageName + "---" + packageName)
            if (cn.packageName != packageName) {
                val LaunchIntent = packageManager.getLaunchIntentForPackage(application.packageName)
                LaunchIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(LaunchIntent)
            }
            } catch (e: Exception) {
                Log.i("TAG", "run: 守护线程发生错误$e")
            }

            Handler().postDelayed(mRunnable, 5000)
        }
        Handler().postDelayed(mRunnable, 5000)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        return Service.START_NOT_STICKY        //保活的一种
    }


}


