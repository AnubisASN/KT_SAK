package com.anubis.app_coroutine

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eJson
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
