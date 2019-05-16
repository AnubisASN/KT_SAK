package com.anubis.module_ftp

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShell
import com.anubis.kt_extends.eShowTip
import com.anubis.kt_extends.eString
import net.iharder.Base64
import java.io.File
import java.util.*


/**
 * This media rescanner runs in the background. The rescan might
 * not happen immediately.
 */
enum class MediaUpdater {
    INSTANCE;

    private class ScanCompletedListener : MediaScannerConnection.OnScanCompletedListener {
        override fun onScanCompleted(path: String, uri: Uri) {
            val msg=Message()
            msg.obj=path
            eDataFTP.mHndler?.sendMessage(msg)
//            Log.i(TAG, "Scan completed: $path : $uri")
//            if (path.indexOf("register") != -1) {
//                moduleData.type = "register"
//            }
//            if (path.indexOf("delete") != -1) {
//                moduleData.type = "delete"
//            }
//            if (path.indexOf("info") != -1) {
//                moduleData.type = "update"
//            }
//            if (path.indexOf("restart") != -1) {
//                File(path).delete()
//                mAPP?.eShowTip("设备即将重启")
//                Handler().postDelayed({
//                    eShell.eExecShell("reboot")
//                }, 2000)
//            }
            val str = path.split("/").last()
            if (str.length >=50 ||  str.subSequence(str.length-1,str.length)=="=") {
                val str1 = str.replace("_", "/")
                val name = String(Base64.decode(str1.toByteArray()))
                File(path).renameTo(File(path.replace(str, name)))
            }
        }

    }

    companion object {

        private val TAG = MediaUpdater::class.java.simpleName

        // the systembroadcast to remount the media is only done after a little while (5s)
        private var sTimer = Timer()

        private val mRunable = Runnable { }

        fun notifyFileCreated(path: String) {
            if (Defaults.do_mediascanner_notify) {
                Log.d(TAG, "Notifying others about new file: $path")
                val context = eDataFTP.mAPP
                MediaScannerConnection.scanFile(context, arrayOf(path), null,
                        ScanCompletedListener())
            }
        }

        fun notifyFileDeleted(path: String) {
            if (Defaults.do_mediascanner_notify) {
                Log.d(TAG, "Notifying others about deleted file: $path")
                if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
                    // on older devices, fake a remount of the media
                    // The media mounted broadcast is very taxing on the system, so
                    // we only do this if for 5 seconds there was no same request,
                    // otherwise we wait again.
                    // the broadcast might have been requested already, cancel if so
                    sTimer.cancel()
                    // that timer is of no value any more, create a new one
                    sTimer = Timer()
                    // and in 5s let it send the broadcast, might never hapen if
                    // before
                    // that time it gets canceled by this code path
                    sTimer.schedule(object : TimerTask() {
                        override fun run() {
                            Log.d(TAG, "Sending ACTION_MEDIA_MOUNTED broadcast")
                            val context = eDataFTP.mAPP
                            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory())
                            val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, uri)
                            context!!.sendBroadcast(intent)
                        }
                    }, 5000)
                } else {
                    // on newer devices, we hope that this works correctly:
                    val context = eDataFTP.mAPP
                    MediaScannerConnection.scanFile(context, arrayOf(path), null,
                            ScanCompletedListener())
                    Log.i(TAG, "notifyFileDeleted: ")
                }
            }
        }
    }

}
