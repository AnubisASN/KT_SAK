package com.anubis.module_webRTC.demo.setting

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.anubis.module_webRTC.demo.service.FloatWindowsService
import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.demo.audiolive.AudioLiveListActivity
import com.anubis.module_webRTC.demo.p2p.VoipP2PDemoActivity
import com.anubis.module_webRTC.demo.test.LoopTestActivity
import com.anubis.module_webRTC.demo.thirdstream.RtspTestListActivity
import com.anubis.module_webRTC.utils.AEvent
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.api.XHCustomConfig
import com.starrtc.starrtcsdk.api.XHConstants

class SettingActivity : BaseActivity(), View.OnClickListener {
    lateinit var customConfig: XHCustomConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        findViewById<View>(R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(R.id.title_left_btn).setOnClickListener { finish() }
        (findViewById<View>(R.id.title_text) as TextView).text = "设置"

        findViewById<View>(R.id.btn_server_set).setOnClickListener(this)
        findViewById<View>(R.id.btn_test_loop).setOnClickListener(this)
        findViewById<View>(R.id.btn_test_rtsp).setOnClickListener(this)
        findViewById<View>(R.id.btn_test_p2p).setOnClickListener(this)
        findViewById<View>(R.id.no_audio_switch).setOnClickListener(this)
        findViewById<View>(R.id.no_video_switch).setOnClickListener(this)
        findViewById<View>(R.id.btn_video_size).setOnClickListener(this)
        findViewById<View>(R.id.btn_video_config_big).setOnClickListener(this)
        findViewById<View>(R.id.btn_audio_bitrate).setOnClickListener(this)
        findViewById<View>(R.id.btn_video_config_small).setOnClickListener(this)
        findViewById<View>(R.id.btn_video_codec_type).setOnClickListener(this)
        findViewById<View>(R.id.btn_audio_codec_type).setOnClickListener(this)
        findViewById<View>(R.id.btn_audio_source).setOnClickListener(this)
        findViewById<View>(R.id.btn_audio_stream_type).setOnClickListener(this)
        findViewById<View>(R.id.opengl_switch).setOnClickListener(this)
        findViewById<View>(R.id.opensl_switch).setOnClickListener(this)
        findViewById<View>(R.id.dy_bt_fp_switch).setOnClickListener(this)
        findViewById<View>(R.id.voip_p2p_switch).setOnClickListener(this)
        findViewById<View>(R.id.rnn_switch).setOnClickListener(this)
        findViewById<View>(R.id.aec_switch).setOnClickListener(this)
        findViewById<View>(R.id.agc_switch).setOnClickListener(this)
        findViewById<View>(R.id.ns_switch).setOnClickListener(this)
        findViewById<View>(R.id.audio_process_qulity_switch).setOnClickListener(this)
        findViewById<View>(R.id.log_switch).setOnClickListener(this)
        findViewById<View>(R.id.hard_encode_switch).setOnClickListener(this)
        findViewById<View>(R.id.btn_about).setOnClickListener(this)
        findViewById<View>(R.id.btn_logout).setOnClickListener(this)
        //        findViewById(R.id.btn_uploadlogs).setOnClickListener(this);
        findViewById<View>(R.id.btn_test_superroom).setOnClickListener(this)
        findViewById<View>(R.id.aecurl_switch).setOnClickListener(this)

        customConfig = XHCustomConfig.getInstance(this)
    }

    public override fun onResume() {
        super.onResume()
        if (MLOC.hasLogout) {
            finish()
            MLOC.hasLogout = true
            return
        }
        findViewById<View>(R.id.opengl_switch).isSelected = customConfig.openGLESEnable
        findViewById<View>(R.id.log_switch).isSelected = FloatWindowsService.runing
        findViewById<View>(R.id.hard_encode_switch).isSelected = customConfig.hardwareEnable
        (findViewById<View>(R.id.video_size_text) as TextView).text = "(" + customConfig.videoSizeName + ")"
        findViewById<View>(R.id.opensl_switch).isSelected = customConfig.openSLESEnable
        findViewById<View>(R.id.dy_bt_fp_switch).isSelected = customConfig.dynamicBitrateAndFpsEnable
        findViewById<View>(R.id.voip_p2p_switch).isSelected = customConfig.voipP2PEnable
        findViewById<View>(R.id.rnn_switch).isSelected = customConfig.rnnEnable
        findViewById<View>(R.id.aec_switch).isSelected = customConfig.audioProcessAECEnable
        findViewById<View>(R.id.agc_switch).isSelected = customConfig.audioProcessAGCEnable
        findViewById<View>(R.id.ns_switch).isSelected = customConfig.audioProcessNSEnable
        findViewById<View>(R.id.audio_process_qulity_switch).isSelected = if (customConfig.aecConfigQulity == XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY)
            true
        else
            false
        (findViewById<View>(R.id.video_config_big_text) as TextView).text = "(" + customConfig.bigVideoFPS + "/" + customConfig.bigVideoBitrate + ")"
        (findViewById<View>(R.id.video_config_small_text) as TextView).text = "(" + customConfig.smallVideoFPS + "/" + customConfig.smallVideoBitrate + ")"
        findViewById<View>(R.id.no_audio_switch).isSelected = !customConfig.audioEnable
        findViewById<View>(R.id.no_video_switch).isSelected = !customConfig.videoEnable
        (findViewById<View>(R.id.video_codec_type_text) as TextView).text = customConfig.videoCodecTypeName
        (findViewById<View>(R.id.audio_codec_type_text) as TextView).text = customConfig.audioCodecTypeName
        (findViewById<View>(R.id.audio_source) as TextView).text = customConfig.audioSourceName
        (findViewById<View>(R.id.audio_stream_type) as TextView).text = customConfig.audioStreamTypeName
        (findViewById<View>(R.id.audio_bitrate_text) as TextView).text = customConfig.audioBitrate.toString() + "kbps"
        findViewById<View>(R.id.aecurl_switch).isSelected = MLOC.AEventCenterEnable!!


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_WINDOW_GRANT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this@SettingActivity)) {
                    Toast.makeText(this@SettingActivity, "没有打开悬浮权限~，", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_server_set -> startActivity(Intent(this, SetupServerHostActivity::class.java))
            R.id.btn_test_loop -> startActivity(Intent(this, LoopTestActivity::class.java))
            R.id.btn_test_rtsp -> startActivity(Intent(this, RtspTestListActivity::class.java))
            R.id.btn_test_superroom -> startActivity(Intent(this, AudioLiveListActivity::class.java))
            R.id.btn_test_p2p -> startActivity(Intent(this, VoipP2PDemoActivity::class.java))
            R.id.no_audio_switch -> {
                customConfig.setDefConfigAudioEnable(if (customConfig.audioEnable) false else true)
                findViewById<View>(R.id.no_audio_switch).isSelected = !customConfig.audioEnable
            }
            R.id.no_video_switch -> {
                customConfig.setDefConfigVideoEnable(if (customConfig.videoEnable) false else true)
                findViewById<View>(R.id.no_video_switch).isSelected = !customConfig.videoEnable
            }
            R.id.btn_video_config_big -> showAddDialog(true)
            R.id.btn_video_config_small -> showAddDialog(false)
            R.id.btn_audio_bitrate -> showAudioDialog()
            R.id.btn_video_codec_type -> {
                val builder = AlertDialog.Builder(this)
                builder.setItems(XHConstants.XHVideoCodecConfigEnumName) { dialogInterface, i ->
                    var selected = customConfig.videoCodecType
                    for (e in XHConstants.XHVideoCodecConfigEnum.values()) {
                        if (i == e.ordinal) {
                            selected = e
                        }
                    }
                    customConfig.setDefConfigVideoCodecType(selected)
                    onResume()
                }
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
            R.id.btn_audio_codec_type -> {
                val builder = AlertDialog.Builder(this)
                builder.setItems(XHConstants.XHAudioCodecConfigEnumName) { dialogInterface, i ->
                    var selected = customConfig.audioCodecType
                    for (e in XHConstants.XHAudioCodecConfigEnum.values()) {
                        if (i == e.ordinal) {
                            selected = e
                        }
                    }
                    customConfig.setDefConfigAudioCodecType(selected)
                    onResume()
                }
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
            R.id.btn_audio_source -> {
                val builder = AlertDialog.Builder(this)
                builder.setItems(XHConstants.XHAudioSourceEnumName, DialogInterface.OnClickListener { dialogInterface, i ->
                    for (e in XHConstants.XHAudioSourceEnum.values()) {
                        if (i == e.ordinal) {
                            customConfig.setDefConfigAudioSource(e)
                            onResume()
                            return@OnClickListener
                        }
                    }
                })
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
            R.id.btn_audio_stream_type -> {
                val builder = AlertDialog.Builder(this)
                builder.setItems(XHConstants.XHAudioStreamTypeEnumName, DialogInterface.OnClickListener { dialogInterface, i ->
                    for (e in XHConstants.XHAudioStreamTypeEnum.values()) {
                        if (i == e.ordinal) {
                            customConfig.setDefConfigAudioStreamType(e)
                            onResume()
                            return@OnClickListener
                        }
                    }
                })
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
            R.id.btn_video_size -> {
                val builder = AlertDialog.Builder(this)
                builder.setItems(XHConstants.XHCropTypeEnumName) { dialogInterface, i ->
                    var selected = customConfig.videoSize
                    for (e in XHConstants.XHCropTypeEnum.values()) {
                        if (i == e.ordinal) {
                            selected = e
                        }
                    }
                    if (customConfig.setDefConfigVideoSize(selected)!!) {
                        MLOC.d("Setting", "Setting selected " + selected.toString())
                        (findViewById<View>(R.id.video_size_text) as TextView).text = "(" + customConfig.videoSizeName + ")"
                    } else {
                        MLOC.showMsg(this@SettingActivity, "设备无法支持所选配置")
                    }
                }
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }

            R.id.opengl_switch -> {
                customConfig.setDefConfigOpenGLESEnable(if (customConfig.openGLESEnable) false else true)
                findViewById<View>(R.id.opengl_switch).isSelected = customConfig.openGLESEnable
            }
            R.id.log_switch -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 动态申请悬浮窗权限
                    if (!Settings.canDrawOverlays(this@SettingActivity)) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName"))
                        startActivityForResult(intent, REQUEST_WINDOW_GRANT)
                        return
                    }
                }

                if (FloatWindowsService.runing) {
                    findViewById<View>(R.id.log_switch).isSelected = false
                    stopService(Intent(this@SettingActivity, FloatWindowsService::class.java))
                } else {
                    findViewById<View>(R.id.log_switch).isSelected = true
                    startService(Intent(this@SettingActivity, FloatWindowsService::class.java))
                }
            }
            R.id.hard_encode_switch -> if (customConfig.setHardwareEnable(if (customConfig.hardwareEnable) false else true)) {
                findViewById<View>(R.id.hard_encode_switch).isSelected = customConfig.hardwareEnable
            } else {
                MLOC.showMsg(this@SettingActivity, "设置失败")
            }
            R.id.btn_about -> startActivity(Intent(this, AboutActivity::class.java))
            //            case R.id.btn_uploadlogs:
            //                customConfig.uploadLogs();
            //                MLOC.showMsg(this,"日志已上传");
            //                break;
            R.id.btn_logout -> {
                XHClient.getInstance().loginManager.logout()
                AEvent.notifyListener(AEvent.AEVENT_LOGOUT, true, null)
                stopService(Intent(this@SettingActivity, FloatWindowsService::class.java))
                MLOC.hasLogout = true
                finish()
            }
            R.id.opensl_switch -> {
                customConfig.setDefConfigOpenSLESEnable(if (customConfig.openSLESEnable) false else true)
                findViewById<View>(R.id.opensl_switch).isSelected = customConfig.openSLESEnable
            }
            R.id.dy_bt_fp_switch -> {
                customConfig.setDefConfigDynamicBitrateAndFpsEnable(if (customConfig.dynamicBitrateAndFpsEnable) false else true)
                findViewById<View>(R.id.dy_bt_fp_switch).isSelected = customConfig.dynamicBitrateAndFpsEnable
            }
            R.id.voip_p2p_switch -> {
                customConfig.setDefConfigVoipP2PEnable(if (customConfig.voipP2PEnable) false else true)
                findViewById<View>(R.id.voip_p2p_switch).isSelected = customConfig.voipP2PEnable
            }
            R.id.rnn_switch -> {
                customConfig.setDefConfigRnnEnable(if (customConfig.rnnEnable) false else true)
                if (customConfig.rnnEnable) {
                    findViewById<View>(R.id.rnn_switch).isSelected = true
                    customConfig.setDefConfigAudioProcessNSEnable(false)
                    findViewById<View>(R.id.ns_switch).isSelected = false
                } else {
                    findViewById<View>(R.id.rnn_switch).isSelected = false
                }
            }
            R.id.aec_switch -> {
                customConfig.setDefConfigAudioProcessAECEnable(if (customConfig.audioProcessAECEnable) false else true)
                findViewById<View>(R.id.aec_switch).isSelected = customConfig.audioProcessAECEnable
            }
            R.id.agc_switch -> {
                customConfig.setDefConfigAudioProcessAGCEnable(if (customConfig.audioProcessAGCEnable) false else true)
                findViewById<View>(R.id.agc_switch).isSelected = customConfig.audioProcessAGCEnable
            }
            R.id.ns_switch -> {
                customConfig.setDefConfigAudioProcessNSEnable(if (customConfig.audioProcessNSEnable) false else true)
                if (customConfig.audioProcessNSEnable) {
                    findViewById<View>(R.id.ns_switch).isSelected = true
                    customConfig.setDefConfigRnnEnable(false)
                    findViewById<View>(R.id.rnn_switch).isSelected = false
                } else {
                    findViewById<View>(R.id.ns_switch).isSelected = false
                }
            }
            R.id.audio_process_qulity_switch -> {
                customConfig.setDefConfigAECConfigQulity(
                        if (customConfig.aecConfigQulity == XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_HIGH_QULITY)
                            XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY
                        else
                            XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_HIGH_QULITY)
                findViewById<View>(R.id.audio_process_qulity_switch).isSelected = if (customConfig.aecConfigQulity == XHConstants.XHAudioAECQulityEnum.AUDIO_AEC_LOW_QULITY)
                    true
                else
                    false
            }
            R.id.aecurl_switch -> if (MLOC.AEventCenterEnable!!) {
                MLOC.AEventCenterEnable = false
                findViewById<View>(R.id.aecurl_switch).isSelected = false
                MLOC.saveSharedData(this@SettingActivity, "AEC_ENABLE", "0")
            } else {
                MLOC.AEventCenterEnable = true
                findViewById<View>(R.id.aecurl_switch).isSelected = true
                MLOC.saveSharedData(this@SettingActivity, "AEC_ENABLE", "1")
            }
        }
    }


    private fun showAddDialog(isbig: Boolean) {
        val dialog = Dialog(this, R.style.dialog_popup)
        dialog.setContentView(R.layout.dialog_video_config_setting)
        val win = dialog.window
        win!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        win.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)

        val fpsTxt = dialog.findViewById<View>(R.id.fps_txt) as TextView
        val fpsSeekBar = dialog.findViewById<View>(R.id.fps_seekbar) as SeekBar
        val bitrateTxt = dialog.findViewById<View>(R.id.bitrate_txt) as TextView
        val bitrateSeekBar = dialog.findViewById<View>(R.id.bitrate_seekbar) as SeekBar

        if (isbig) {
            fpsSeekBar.max = 30
            fpsSeekBar.progress = customConfig.bigVideoFPS
            fpsTxt.text = "帧率:" + customConfig.bigVideoFPS
            bitrateSeekBar.max = 2000
            bitrateSeekBar.progress = customConfig.bigVideoBitrate
            bitrateTxt.text = "码率:" + customConfig.bigVideoBitrate
        } else {
            fpsSeekBar.max = 30
            fpsSeekBar.progress = customConfig.smallVideoFPS
            fpsTxt.text = "帧率:" + customConfig.smallVideoFPS
            bitrateSeekBar.max = 200
            bitrateSeekBar.progress = customConfig.smallVideoBitrate
            bitrateTxt.text = "码率:" + customConfig.smallVideoBitrate
        }

        fpsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                fpsTxt.text = "帧率:$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        bitrateSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                bitrateTxt.text = "码率:$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        dialog.findViewById<View>(R.id.btn_yes).setOnClickListener {
            if (isbig) {
                customConfig.setDefConfigBigVideoConfig(fpsSeekBar.progress, bitrateSeekBar.progress)
                (findViewById<View>(R.id.video_config_big_text) as TextView).text = "(" + customConfig.bigVideoFPS + "fps/" + customConfig.bigVideoBitrate + "kbps)"
            } else {
                customConfig.setDefConfigSmallVideoConfig(fpsSeekBar.progress, bitrateSeekBar.progress)
                (findViewById<View>(R.id.video_config_small_text) as TextView).text = "(" + customConfig.smallVideoFPS + "fps/" + customConfig.smallVideoBitrate + "kbps)"
            }
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.btn_no).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showAudioDialog() {
        val dialog = Dialog(this, R.style.dialog_popup)
        dialog.setContentView(R.layout.dialog_video_config_setting)
        val win = dialog.window
        win!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        win.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)

        val fpsTxt = dialog.findViewById<View>(R.id.fps_txt) as TextView
        val fpsSeekBar = dialog.findViewById<View>(R.id.fps_seekbar) as SeekBar
        val bitrateTxt = dialog.findViewById<View>(R.id.bitrate_txt) as TextView
        val bitrateSeekBar = dialog.findViewById<View>(R.id.bitrate_seekbar) as SeekBar

        fpsTxt.visibility = View.GONE
        fpsSeekBar.visibility = View.GONE

        bitrateSeekBar.max = 128
        bitrateSeekBar.progress = customConfig.audioBitrate
        bitrateTxt.text = "音频码率:" + customConfig.audioBitrate


        bitrateSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                bitrateTxt.text = "音频码率:$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        dialog.findViewById<View>(R.id.btn_yes).setOnClickListener {
            customConfig.setDefConfigAudioBitRate(bitrateSeekBar.progress)
            (findViewById<View>(R.id.audio_bitrate_text) as TextView).text = customConfig.audioBitrate.toString() + "kbps"
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.btn_no).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    companion object {
        /***
         * 请求悬浮窗权限
         */
        val REQUEST_WINDOW_GRANT = 201
    }

}
