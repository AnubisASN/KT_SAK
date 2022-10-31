package com.anubis.module_webRTC.demo

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation

import com.anubis.module_webRTC.demo.service.KeepLiveService
import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.eDataRTC
import com.anubis.module_webRTC.utils.AEvent

import java.util.ArrayList

class SplashActivity : androidx.appcompat.app.AppCompatActivity() {
    private var isLogin = false
    private val checkNetState = false

    private var times = 0
    private val REQUEST_PHONE_PERMISSIONS = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(com.anubis.module_webRTC.R.layout.activity_splash)
        eDataRTC.mAPP=this.application
        AEvent.setHandler(Handler())
        checkPermission()
    }

    private fun checkPermission() {
        times++
        val permissionsList = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE)
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.READ_PHONE_STATE)
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.CAMERA)
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.BLUETOOTH)
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) permissionsList.add(Manifest.permission.RECORD_AUDIO)
            if (permissionsList.size != 0) {
                if (times == 1) {
                    requestPermissions(permissionsList.toTypedArray(),
                            REQUEST_PHONE_PERMISSIONS)
                } else {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("提示")
                            .setMessage("获取不到授权，APP将无法正常使用，请允许APP获取权限！")
                            .setPositiveButton("确定") { arg0, arg1 ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toTypedArray(),
                                            REQUEST_PHONE_PERMISSIONS)
                                }
                            }.setNegativeButton("取消") { arg0, arg1 -> finish() }.show()
                }
            } else {
                initSDK()
            }
        } else {
            initSDK()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    private fun initSDK() {
        startService()
        startAnimation()
    }


    private fun startService() {
        val intent = Intent(this@SplashActivity, KeepLiveService::class.java)
        startService(intent)
    }


    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        isLogin = true
        val eye = findViewById<View>(com.anubis.module_webRTC.R.id.eye)
        eye.alpha = 0.2f
        val black = findViewById<View>(com.anubis.module_webRTC.R.id.black_view)
        val white = findViewById<View>(com.anubis.module_webRTC.R.id.white_view)

        val va = ObjectAnimator.ofFloat(eye, "alpha", 0.2f, 1f)
        va.duration = 1000
        va.repeatCount = ValueAnimator.INFINITE
        va.repeatMode = Animation.REVERSE
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {
                if (isLogin) {
                    va.cancel()
                    val va1 = ObjectAnimator.ofFloat(white, "alpha", 0f, 1f)
                    val va2 = ObjectAnimator.ofFloat(black, "alpha", 1f, 0f)

                    val animatorSet = AnimatorSet()
                    animatorSet.duration = 1500
                    animatorSet.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            object : Handler() {
                                override fun handleMessage(msg: Message) {
                                    startActivity(Intent(this@SplashActivity, StarAvDemoActivity::class.java))
                                    finish()
                                }

                            }.sendEmptyMessageDelayed(0, 500)
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    })
                    animatorSet.playTogether(va1, va2)
                    animatorSet.start()
                }
            }
        })
        va.start()
    }
}
