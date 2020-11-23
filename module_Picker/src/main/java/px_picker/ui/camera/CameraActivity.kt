package px_picker.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import com.anubis.kt_extends.*
import com.anubis.module_picker.R
import com.anubis.module_picker.Utils.ImagePickerProvider
import px_core.common.PhoenixConstant
import px_core.common.PhoenixConstant.REQUEST_CODE_PREVIEW
import px_core.common.PhoenixConstant.TYPE_PREIVEW_FROM_CAMERA
import px_core.model.MediaEntity
import px_core.model.MimeType
import px_picker.rx.bus.ImagesObservable.Companion.instance
import px_picker.ui.BaseActivity
import px_picker.ui.camera.config.CameraConfig
import px_picker.ui.camera.config.model.MediaAction
import px_picker.ui.camera.listener.*
import px_picker.ui.camera.widget.CameraSettingsView
import px_picker.ui.camera.widget.CameraSwitchView
import px_picker.ui.camera.widget.FlashSwitchView
import px_picker.ui.camera.widget.RecordButton
import px_picker.ui.camera.widget.RecordButton.OnRecordButtonListener
import px_picker.ui.picker.PreviewActivity
import java.io.File
import java.lang.StringBuilder
import java.util.*

class CameraActivity : BaseActivity(), View.OnClickListener {
    var mCameraLayout: View? = null
    var mRecordButton: RecordButton? = null
    var mRecordDurationText: TextView? = null
    var mRecordSizeText: TextView? = null
    var mSettingsView: CameraSettingsView? = null
    var mFlashSwitchView: FlashSwitchView? = null
    var mCameraSwitchView: CameraSwitchView? = null
    private  var mFilePath=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_camera)
        setupView()
        try {
            setupCameraFragment()
        } catch (e: Exception) {
            //拍照存放路径
            eSysTemCameraTake("/sdcard/IMG", "IMG_${eTime.eInit.eGetCuoTime()}.jpg"){ intent: Intent, path: String  ->
                mFilePath=path
            }
        }
    }

    override fun onClick(v: View) {
        val cameraFragment = cameraFragment ?: return
        if (v.id == R.id.flash_switch_view) {
            cameraFragment.toggleFlashMode()
        } else if (v.id == R.id.front_back_camera_switcher) {
            cameraFragment.switchCameraTypeFrontBack()
        } else if (v.id == R.id.settings_view) {
            cameraFragment.openSettingDialog()
        }
    }

    private fun setupView() {
        mSettingsView = findViewById<View>(R.id.settings_view) as CameraSettingsView
        mFlashSwitchView = findViewById<View>(R.id.flash_switch_view) as FlashSwitchView
        mCameraSwitchView = findViewById<View>(R.id.front_back_camera_switcher) as CameraSwitchView
        mRecordButton = findViewById<View>(R.id.record_button) as RecordButton
        mRecordDurationText = findViewById<View>(R.id.record_duration_text) as TextView
        mRecordSizeText = findViewById<View>(R.id.record_size_mb_text) as TextView
        mCameraLayout = findViewById(R.id.rl_camera_control)
        mSettingsView!!.setOnClickListener(this)
        mFlashSwitchView!!.setOnClickListener(this)
        mCameraSwitchView!!.setOnClickListener(this)
        mCameraLayout!!.setOnClickListener(this)
        mRecordButton!!.setTimeLimit(option.recordVideoTime * 1000)
        mRecordButton!!.setOnRecordButtonListener(object : OnRecordButtonListener {
            override fun onClick() {
                val cameraFragment = cameraFragment
                cameraFragment!!.switchCaptureAction(MediaAction.ACTION_PHOTO)
                cameraFragment.takePicture(DIRECTORY_NAME, "IMG_" + System.currentTimeMillis(),
                        object : OnCameraResultAdapter() {
                            override fun onPhotoTaken(bytes: ByteArray, filePath: String) {
                                try {
                                    MediaScannerConnection.scanFile(this@CameraActivity, arrayOf(filePath), null
                                    ) { path, uri -> }
                                } catch (ignore: Exception) {
                                }
                                val mediaList = ArrayList<MediaEntity>()
                                val mediaEntity = MediaEntity.newBuilder()
                                        .localPath(filePath)
                                        .fileType(MimeType.ofImage())
                                        .mimeType(MimeType.createImageType(filePath))
                                        .build()
                                mediaList.add(mediaEntity)
                                instance.savePreviewMediaList(mediaList)
                                val intent = Intent(this@CameraActivity, PreviewActivity::class.java)
                                intent.putParcelableArrayListExtra(PhoenixConstant.KEY_PICK_LIST, mediaList)
                                intent.putExtra(PhoenixConstant.KEY_PREVIEW_TYPE, PhoenixConstant.TYPE_PREIVEW_FROM_CAMERA)
                                startActivityForResult(intent, PhoenixConstant.REQUEST_CODE_PREVIEW)
                            }
                        }
                )
            }

            override fun onLongClickStart() {
                val cameraFragment = cameraFragment
                cameraFragment!!.switchCaptureAction(MediaAction.ACTION_VIDEO)
                cameraFragment.startRecordingVideo(DIRECTORY_NAME, "VID_" + System.currentTimeMillis())
            }

            override fun onLongClickEnd() {
                val cameraFragment = cameraFragment
                cameraFragment!!.stopRecordingVideo(object : OnCameraResultAdapter() {
                    override fun onVideoRecorded(filePath: String) {
                        try {
                            MediaScannerConnection.scanFile(this@CameraActivity, arrayOf(filePath), null
                            ) { path, uri -> }
                        } catch (ignore: Exception) {
                        }
                        val mediaList = ArrayList<MediaEntity>()
                        val mediaEntity = MediaEntity.newBuilder()
                                .localPath(filePath)
                                .fileType(MimeType.ofVideo())
                                .mimeType(MimeType.createVideoType(filePath))
                                .build()
                        mediaList.add(mediaEntity)
                        instance.savePreviewMediaList(mediaList)
                        val intent = Intent(this@CameraActivity, PreviewActivity::class.java)
                        intent.putParcelableArrayListExtra(PhoenixConstant.KEY_PICK_LIST, mediaList)
                        intent.putExtra(PhoenixConstant.KEY_PREVIEW_TYPE, PhoenixConstant.TYPE_PREIVEW_FROM_CAMERA)
                        startActivityForResult(intent, PhoenixConstant.REQUEST_CODE_PREVIEW)
                    }
                })
                cameraFragment.switchCaptureAction(MediaAction.ACTION_PHOTO)
            }
        })
    }

    private fun setupCameraFragment() {
        if (Build.VERSION.SDK_INT > 15) {
            val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionsToRequest: MutableList<String> = ArrayList()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PhoenixConstant.REQUEST_CODE_CAMERA_PERMISSIONS)
            } else addCameraFragment()
        } else {
            addCameraFragment()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            addCameraFragment()
        }
    }

    fun addCameraFragment() {
        mCameraLayout!!.visibility = View.VISIBLE
        val cameraFragment = CameraFragment.newInstance(CameraConfig.Builder()
                .setCamera(CameraConfig.CAMERA_FACE_REAR).build())
        val bundle = Bundle()
        bundle.putParcelable(PhoenixConstant.PHOENIX_OPTION, option)
        cameraFragment!!.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, cameraFragment, TAG_CAMERA_FRAGMENT)
                .commitAllowingStateLoss()
//        if (cameraFragment != null) {
            //cameraFragmentApi.setResultListener(new OnCameraResultListener() {
            //    @Override
            //    public void onVideoRecorded(String filePath) {
            //        Intent intent = CameraPreviewActivity.newIntentVideo(CameraFragmentMainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
            //    }
//
            //    @Override
            //    public void onPictureTaken(byte[] bytes, String filePath) {
            //        Intent intent = CameraPreviewActivity.newIntentPhoto(CameraFragmentMainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
            //    }
            //});
            cameraFragment.setStateListener(object : CameraStateAdapter() {
                override fun onCurrentCameraBack() {
                    mCameraSwitchView!!.displayBackCamera()
                }

                override fun onCurrentCameraFront() {
                    mCameraSwitchView!!.displayFrontCamera()
                }

                override fun onFlashAuto() {
                    mFlashSwitchView!!.displayFlashAuto()
                }

                override fun onFlashOn() {
                    mFlashSwitchView!!.displayFlashOn()
                }

                override fun onFlashOff() {
                    mFlashSwitchView!!.displayFlashOff()
                }

                override fun onCameraSetupForPhoto() {
//                    mFlashSwitchView.setVisibility(View.VISIBLE);
                }

                override fun onCameraSetupForVideo() {
//                    mFlashSwitchView.setVisibility(View.GONE);
                }

                override fun shouldRotateControls(degrees: Int) {
                    ViewCompat.setRotation(mCameraSwitchView, degrees.toFloat())
                    ViewCompat.setRotation(mFlashSwitchView, degrees.toFloat())
                    ViewCompat.setRotation(mRecordDurationText, degrees.toFloat())
                    ViewCompat.setRotation(mRecordSizeText, degrees.toFloat())
                }

                override fun onRecordStateVideoReadyForRecord() {}
                override fun onRecordStateVideoInProgress() {}
                override fun onRecordStatePhoto() {}
                override fun onStopVideoRecord() {
                    mRecordSizeText!!.visibility = View.GONE
                    //                    mSettingsView.setVisibility(View.VISIBLE);
                }

                override fun onStartVideoRecord(outputFile: File) {}
            })
            cameraFragment.setControlsListener(object : CameraControlAdapter() {
                override fun lockControls() {
                    mCameraSwitchView!!.isEnabled = false
                    mRecordButton!!.isEnabled = false
                    mSettingsView!!.isEnabled = false
                    mFlashSwitchView!!.isEnabled = false
                }

                override fun unLockControls() {
                    mCameraSwitchView!!.isEnabled = true
                    mRecordButton!!.isEnabled = true
                    mSettingsView!!.isEnabled = true
                    mFlashSwitchView!!.isEnabled = true
                }

                override fun allowCameraSwitching(allow: Boolean) {
                    mCameraSwitchView!!.visibility = if (allow) View.VISIBLE else View.GONE
                }

                override fun allowRecord(allow: Boolean) {
                    mRecordButton!!.isEnabled = allow
                }

                override fun setMediaActionSwitchVisible(visible: Boolean) {}
            })
            cameraFragment.setTextListener(object : CameraVideoRecordTextAdapter() {
                override fun setRecordSizeText(size: Long, text: String) {
                    mRecordSizeText!!.text = text
                }

                override fun setRecordSizeTextVisible(visible: Boolean) {
                    mRecordSizeText!!.visibility = if (visible) View.VISIBLE else View.GONE
                }

                override fun setRecordDurationText(text: String) {
                    mRecordDurationText!!.text = text
                }

                override fun setRecordDurationTextVisible(visible: Boolean) {
                    mRecordDurationText!!.visibility = if (visible) View.VISIBLE else View.GONE
                }
            })
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (Activity.RESULT_OK != resultCode) return Unit.apply { eLog("RESULT_OK!=resultCode") }
        when (requestCode) {
            REQUEST_CODE_PREVIEW ->{
                intent?:return Unit.apply { eLogE("onActivityResult - intent==null") }
                onResult(intent.getSerializableExtra(PhoenixConstant.KEY_PICK_LIST) as MutableList<MediaEntity>)
            }
            REQUEST_CODE_CAMERA_TAKE -> {
                val mediaList = ArrayList<MediaEntity>()
                val mediaEntity = MediaEntity.newBuilder()
                        .localPath(mFilePath)
                        .fileType(MimeType.ofImage())
                        .mimeType(MimeType.createImageType(mFilePath))
                        .build()
                mediaList.add(mediaEntity)
                onResult(mediaList)
            }
        }
    }

    private val cameraFragment: ICameraFragment?
        private get() = supportFragmentManager.findFragmentByTag(TAG_CAMERA_FRAGMENT) as ICameraFragment?

    companion object {
        private const val TAG_CAMERA_FRAGMENT = "CameraFragment"
        private val DIRECTORY_NAME = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                + "/Camera")
    }
}
