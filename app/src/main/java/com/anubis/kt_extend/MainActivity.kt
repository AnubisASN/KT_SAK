package com.anubis.kt_extend

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anubis.extension_function.eLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eLog("扩展函数")
    }
}
