package com.anubis.kt_extend

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetSystemSharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eLog("扩展函数")
    }
}
