package com.anubis.kt_extends

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.ContentUris
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.preference.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils.indexOf
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.inputMethodManager
import org.json.JSONObject
import java.io.*
import java.lang.Process
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.experimental.and

/**
 * Author  ： AnubisASN   on 2018-07-23 9:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 * Router :  /'Module'/'Function'
 * 说明：Toamst扩展函数
 * Environment.getExternalStorageDirectory() /storage/sdcard0
 * getExternalFilesDir()
 * getExternalCacheDir()
 */


fun Context.eShowTip(str: Any, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str.toString(), i).show()
}

/**
 * Log i e扩展函数------------------------------------------------------------------------------------
 */
var eIsTagD: Boolean = true
var eIsTagI: Boolean = true

fun Any?.eIsBaseType() = this is String || this is Int || this is Int || this is Log || this is Double || this is Float || this is Char || this is Short || this is Boolean || this is Byte


fun <T> T.eLog(hint: Any? = "", TAG: String = "TAGd"): T {
    if (eIsTagD)
        Log.d(TAG, "${if (this == null) "" else (this as Any).javaClass.name}-$hint ${if (this.eIsBaseType()) "：$this" else ""}\n")
    return this
}


fun <T> T.eLogI(hint: Any? = "", TAG: String = "TAGi"): T {
    if (eIsTagI)
        Log.i(TAG, "${if (this == null) "" else (this as Any).javaClass.name}-$hint ${if (this.eIsBaseType()) "：$this" else ""}\n")
    return this
}


fun <T> T?.eLogE(hint: Any? = "", TAG: String = "TAGe"): T? {
    Log.e(TAG, "${if (this == null) "" else (this as Any).javaClass.name}-$hint\n${eErrorOut(this)} ")
    return this
}

fun eLogE(hint: Any? = "", e: Any? = null, TAG: String = "TAGe") {
    e.eLogE(hint, TAG)
}

/**
 * 错误输出
 */
fun eErrorOut(e: Any?): String? {
    e ?: return null
    val os = ByteArrayOutputStream()
    when (e) {
        is Error -> e.printStackTrace(PrintStream(os))
        is Exception -> e.printStackTrace(PrintStream(os))
    }
    return os.toString()
}


fun Context.eLogCat(savePath: String = "/mnt/sdcard/Logs/", fileName: String = "${eTime.eInit.eGetTime("yyyy-MM-dd")}.log", parame: String = "-v long AndroidRuntime:E *:S TAG:E TAG:I *E") = async {
    if (!File(savePath).exists()) {
        File(savePath).mkdirs()
    }
    GlobalScope.launch {
        val psResult = eShell.eInit.eExecShell("ps logcat")
        psResult.split("\n").forEach {
            if (!it.contains("shell"))
                coroutineScope {
                    it.split(" ").forEach {
                        try {
                            val pid = it.replace(" ", "").toInt()
                            eShell.eInit.eExecShell("kill $pid")
                            return@coroutineScope
                        } catch (e: Exception) {
                        }
                    }
                }
        }
        eShell.eInit.eExecShell("logcat $parame -d >> $savePath$fileName")
    }
}

fun Application.eCrash(saveFile: File = File("${Environment.getExternalStorageDirectory()}/errorLogs.log")) {
    //记录崩溃信息
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        //获取崩溃时的UNIX时间戳
        //将时间戳转换成人类能看懂的格式，建立一个String拼接器
        val stringBuilder = StringBuilder(eTime.eInit.eGetTime("yyyy/MM/dd HH:mm:ss"))
        stringBuilder.append(":\n")
        //获取错误信息退票手续费
        stringBuilder.append(throwable.message)
        stringBuilder.append("\n")
        //获取堆栈信息
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        stringBuilder.append(sw.toString())

        //这就是完整的错误信息了，你可以拿来上传服务器，或者做成本地文件保存等等等等
        val errorLog = stringBuilder.toString()
        saveFile.appendText(errorLog)
        //最后如何处理这个崩溃，这里使用默认的处理方式让APP停止运行
        defaultHandler.uncaughtException(thread, throwable)
    }
}

/*String字符标识*/
fun TextView.eSpannableTextView(str: String, startAndEndIndexArray: Array<Pair<Int, Int>>? = arrayOf(Pair(0, 0)), colorArray: Array<Int>? = arrayOf(Color.RED), clickBlockArray: Array<() -> Unit>? = null) {
    if (str.isEmpty())
        return
    text = ""
    var startIndex = 0
    //这个一定要记得设置，不然点击不生效
    movementMethod = LinkMovementMethod.getInstance()
    startAndEndIndexArray?.forEachIndexed { index, pair ->
        var subStr = ""
        try {
            subStr = str.substring(startIndex, if (index + 1 == startAndEndIndexArray.size) str.length else pair.second)
            val ssb = SpannableStringBuilder(subStr)
            ssb.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    clickBlockArray?.let {
                        if (index > it.size - 1) {
                            it.last()()
                        } else {
                            it[index]()
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    colorArray?.let {
                        if (index > it.size - 1) {
                            ds.color = it.last()
                        } else {
                            ds.color = it[index]
                        }
                    }
                }
            }, pair.first - startIndex, pair.second - startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            append(ssb)
            startIndex = pair.second
        } catch (e: IndexOutOfBoundsException) {
            append(subStr)
            return@forEachIndexed
        } catch (e: Exception) {
            e.eLogE("eSpannableTextView")
            return@forEachIndexed
        }
    }
}


/**
 * SharedPreferences数据文件存储扩展-------------------------------------------------------------------
 */
//系统数据文件存储扩展
fun Context.eSetSystemSharedPreferences(key: Any, value: Any, sharedPreferences: SharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)): Boolean {
    val key = key.toString()
    val editor = sharedPreferences.edit()
    when (value) {
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
        else -> editor.putString(key, value.toString())
    }
    return editor.commit()
}

//系统数据文件存储读取扩展
fun <T> Context.eGetSystemSharedPreferences(key: String, value: T, sharedPreferences: SharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)): T = when (value) {
    is Int -> sharedPreferences.getInt(key, value)
    is Long -> sharedPreferences.getLong(key, value)
    is Float -> sharedPreferences.getFloat(key, value)
    is Boolean -> sharedPreferences.getBoolean(key, value)
    is String -> sharedPreferences.getString(key, value)
    is Set<*> -> sharedPreferences.getStringSet(key, value as Set<String>)
    else -> sharedPreferences.getString(key, value.toString())
} as T


//用户文件数据存储扩展
fun Context.eSetUserSharedPreferences(userID: String, key: String, value: Any, sharedPreferences: SharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)): Boolean {
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
        else -> editor.putString(key, value.toString())
    }
    return editor.commit()
}

//用户文件数据读取扩展
fun <T> Context.eGetUserSharedPreferences(userID: String, key: String, value: T, sharedPreferences: SharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)): T = when (value) {
    is Int -> sharedPreferences.getInt(key, value)
    is Long -> sharedPreferences.getLong(key, value)
    is Float -> sharedPreferences.getFloat(key, value)
    is Boolean -> sharedPreferences.getBoolean(key, value)
    is String -> sharedPreferences.getString(key, value)
    is Set<*> -> sharedPreferences.getStringSet(key, value as Set<String>)
    else -> sharedPreferences.getString(key, value.toString())
} as T


//首选项数据文件存储扩展
fun Context.eSetDefaultSharedPreferences(key: String, value: Any, sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)): Boolean {
    val editor = sharedPref.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
        else -> editor.putString(key, value.toString())
    }
    return editor.commit()
}

//首选项数据文件读取扩展
fun <T> Context.eGetDefaultSharedPreferences(key: String, value: T, sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)): T = when (value) {
    is Int -> sharedPref.getInt(key, value)
    is Long -> sharedPref.getLong(key, value)
    is Float -> sharedPref.getFloat(key, value)
    is Boolean -> sharedPref.getBoolean(key, value)
    is String -> sharedPref.getString(key, value)
    is Set<*> -> sharedPref.getStringSet(key, value as Set<String>)
    else -> sharedPref.getString(key, value.toString())
} as T

//Intent Get传递扩展
fun Intent.eGetMessage(Sign: String): String = getStringExtra(Sign)

fun Bundle.eSetMessage(Sign: String, Message: Any) = when (Message) {
    is String -> putString(Sign, Message)
    is Int -> putInt(Sign, Message)
    is Float -> putFloat(Sign, Message)
    is Boolean -> putBoolean(Sign, Message)
    is Char -> putChar(Sign, Message)
    is Byte -> putByte(Sign, Message)
    is ArrayList<*> -> putStringArrayList(Sign, Message as ArrayList<String>)
    is IntArray -> putIntArray(Sign, Message)
    else -> {
    }
}

//音频播放
var eMediaPlayer: MediaPlayer? = null

@RequiresApi(Build.VERSION_CODES.N)
fun ePlayVoice(
        context: Context,
        music: Any,
        isAsync: Boolean = false,
        isLoop: Boolean = false
): Boolean {
    try {
        eMediaPlayer?.release()
        when (music) {
            is Int -> eMediaPlayer = MediaPlayer.create(context, music)//重新设置要播放的音频}
            is String -> {
                eMediaPlayer = MediaPlayer()
                eMediaPlayer?.setDataSource(music)
                if (isAsync)
                    eMediaPlayer?.prepareAsync()
                else
                    eMediaPlayer?.prepare()
            }
            is AssetFileDescriptor -> {
                eMediaPlayer = MediaPlayer()
                eMediaPlayer?.setDataSource(music)
                if (isAsync)
                    eMediaPlayer?.prepareAsync()
                else
                    eMediaPlayer?.prepare()
            }
        }
        eMediaPlayer?.isLooping = isLoop
        eMediaPlayer?.start()//开始播放
        return true
    } catch (e: Exception) {
        e.eLogE("ePlayVoice 错误")
        return false
    }
}

fun MediaPlayer.eClean() {
    stop()
    release()
    eMediaPlayer = null
}

//PCM播放
fun ePlayPCM(path: String, sampleRateInHz: Int = 16000, channelConfig: Int = AudioFormat.CHANNEL_OUT_MONO, audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT) {
    val bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    var audioTrack: AudioTrack? = AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM)
    var fis: FileInputStream? = null
    var isPlaying = true
    var isStop = false
    try {
        audioTrack!!.play()
        fis = FileInputStream(path)
        val buffer = ByteArray(bufferSize)
        var len = 0
        while ((fis.read(buffer)).apply { len = this } != -1 && !isStop) {
            audioTrack.write(buffer, 0, len)
        }
    } catch (e: Exception) {
        e.eLogE("playPCMRecord")
    } finally {
        isPlaying = false
        isStop = false
        if (audioTrack != null) {
            audioTrack.stop()
            audioTrack = null
        }
        if (fis != null) {
            try {
                fis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

// 跳转相机拍照
val REQUEST_CODE_CAMERA_TAKE = 103
fun Activity.eSysTemCameraTake(fileDir: String = Environment.getExternalStorageDirectory().path + "/Pictures", fileName: String = "IMG_" + System.currentTimeMillis() + ".jpg", requestCode: Int = REQUEST_CODE_CAMERA_TAKE, authority: String = "com.anubis.module_extends", resultBlock: ((Intent, String) -> Unit)? = null) {
    //拍照存放路径
    if (!eFile.eInit.eCheckFile(fileDir))
        return eShowTip("File Create Error")
    val mFilePath = "$fileDir/${if (fileName.contains(".")) fileName else "$fileName.jpg"}"
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val uri: Uri
    uri = if (Build.VERSION.SDK_INT >= 24) {
        FileProvider.getUriForFile(this, authority, File(mFilePath))
    } else {
        Uri.fromFile(File(mFilePath))
    }
    intent.putExtra(EXTRA_OUTPUT, uri)
    resultBlock?.let { it(intent, mFilePath) }
    startActivityForResult(intent, requestCode)
}

// 跳转相机选择
val REQUEST_CODE_PHOTO_SELECT = 104
fun Activity.eSystemSelectImg(requestCode: Int = REQUEST_CODE_PHOTO_SELECT) {
    val intent = Intent()
    intent.action = Intent.ACTION_PICK
    intent.type = "image/*"
    startActivityForResult(intent, requestCode)
}


/**
 *  Json解析扩展--------------------------------------------------------------------------------
 */
class eJson private constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eJson() }
    }

    //实例类解析
    fun <T> eGetJsonFrom(jsonStr: String, clazz: Class<T>) = Gson().fromJson<T>(jsonStr, clazz)

    //实例类生成Json
    fun eGetToJson(any: Any) = GsonBuilder().disableHtmlEscaping().create().toJson(any).replace("\\n", "").replace(" ", "").trim()

    //Object Json解析扩展
    fun <T> eGetJson(json: String, resultKey: String, default: T): T {
        return try {
            when (default) {
                is String -> JSONObject(json).getString(resultKey)
                is Int -> JSONObject(json).getInt(resultKey)
                is Long -> JSONObject(json).getLong(resultKey)
                is Boolean -> JSONObject(json).getBoolean(resultKey)
                is Double -> JSONObject(json).getDouble(resultKey)
                else -> default
            } as T
        } catch (e: Exception) {
            default
        }
    }

    //Array Json解析扩展
    fun eGetJsonArray(json: String, resultKey: String, i: Int) = JSONObject(json).optJSONArray(resultKey).getJSONObject(i).toString()

    fun eGetJsonArray(json: String, resultKey: String) = JSONObject(json).optJSONArray(resultKey)
}

/**
 *广播接收器辅助扩展类--------------------------------------------------------------------------------------
 */
class eBReceiver private constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eBReceiver() }
    }

    //开机启动
    private var isSetAutoBoot = true

    fun eSetPowerBoot(context: Context, intent: Intent, cls: Class<*>): Boolean {
        return if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            eLog("开机自启", "SAK")
            if (isSetAutoBoot) {
                isSetAutoBoot = false
                val startServiceIntent = Intent(context, cls)
                startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(startServiceIntent)
                true
            } else false
        } else false
    }

    //APP更新启动
    fun eSetAPPUpdateBoot(context: Context, intent: Intent, cls: Class<*>, hint: Array<String>? = arrayOf("升级了一个安装包", "安装了一个安装包", "卸载了一个安装包")): Boolean {
        //接收更新广播
        if (intent.action == "android.intent.action.PACKAGE_REPLACED") {
            Toast.makeText(context, hint!![0], Toast.LENGTH_SHORT).show()
            val startServiceIntent = Intent(context, cls)
            startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startServiceIntent)
            return true
        }
        //接收安装广播
        if (intent.action == "android.intent.action.PACKAGE_ADDED") {
            Toast.makeText(context, hint!![1], Toast.LENGTH_SHORT).show()
            val packName = intent.resolveActivityInfo(context.packageManager, 0).toString()
            eLog("packName:$packName")
            val startServiceIntent = Intent(context, cls)
            startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startServiceIntent)
            return true
        }
        //接收卸载广播
        if (intent.action == "android.intent.action.PACKAGE_REMOVED") {
            Toast.makeText(context, hint!![2], Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }
}


/**
 *  Uri扩展--------------------------------------------------------------------------------
 */
class eUri private constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eUri() }
    }

    fun eIsExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority

    fun eIsDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority

    fun eIsMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority

    fun eIsGooglePhotosUri(uri: Uri) = "com.google.android.apps.photos.content" == uri.authority

    fun eGetDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */
    @TargetApi(19)
    fun eGetImageAbsolutePath(context: Context?, imageUri: Uri?): String? {
        if (context == null || imageUri == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (eIsExternalStorageDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (eIsDownloadsDocument(imageUri)) {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return eGetDataColumn(context, contentUri, null, null)
            } else if (eIsMediaDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return eGetDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)
        else if ("content".equals(imageUri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (eIsGooglePhotosUri(imageUri)) imageUri.lastPathSegment else eGetDataColumn(context, imageUri, null, null)
        } else if ("file".equals(imageUri.scheme, ignoreCase = true)) {
            return imageUri.path
        }
        return null
    }
}

/**
 * App程序扩展类--------------------------------------------------------------------------------------
 */
class eApp private constructor() {
    companion object {
        private var wakeLock: PowerManager.WakeLock? = null
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eApp() }
    }

    // CPU唤醒
    fun eCUPWakeLock(context: Context, isLongWake: Boolean = true, timeOut: Long? = null) {
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager
        if (wakeLock == null)
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag")
        if (isLongWake) {
            timeOut?.let { wakeLock?.acquire(it) } ?: wakeLock?.acquire()
        } else {
            wakeLock?.release()
        }
    }

    //屏幕常亮
    fun eLongScreen(activity: Activity, isLongScreen: Boolean = true) {
        if (isLongScreen) activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    //清除栈重启
    fun eTaskCleanAndRestart(activity: Activity) {
        activity.finish()
        val intent = activity.intent
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    //APK安装
    fun eInstallApkFile(context: Context, filePath: String, authority: String = "com.anubis.module_extends") {
        eInstallApkFile(context, File(filePath), authority)
    }

    fun eInstallApkFile(context: Context, file: File, authority: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = FileProvider.getUriForFile(context, authority, file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.eLogE("installApkFile")
        }
    }

    //窗口全屏
    fun eSetWindowFullScreen(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    //输入法隐藏
    fun Context.eInputHide(editText: EditText) = inputMethodManager.hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

    //通过APK地址获取此APP的包名和版本等信息
    open fun eGetApkInfo(context: Context, apkPath: String): Array<String?> {
        val infos = arrayOfNulls<String>(3)
        val pm = context.packageManager
        val info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)
        if (info != null) {
            val appInfo = info.applicationInfo
            val appName = pm.getApplicationLabel(appInfo).toString()
            infos[0] = appName
            infos[1] = info.versionName //获取版本信息
            infos[2] = appInfo.packageName   //获取安装包名称
        }
        return infos
    }

    //APP国际化
    open fun eInternationalization(activity: Context, locale: Locale = Locale.getDefault()) {
        //设置应用语言类型
        val resources = activity.resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        config.locale = locale
        resources.updateConfiguration(config, dm)
        //更新语言后，destroy当前页面，重新绘制
        if (activity is Activity) {
            activity.finish()
            val it = Intent(activity, activity::class.java)
            //清空任务栈确保当前打开activit为前台任务栈栈顶
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(it)
        }
    }

    //获取软件版本
    fun eGetVersion(context: Context) = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    //获取所有安装的app
    open fun eGetLocalApps(context: Context) = context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN, null), 0)

    //获取APP包名
    open fun eGetLocalAppsPackageName(context: Context, appName: String): String? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = context.packageManager.queryIntentActivities(intent, 0)
        //for循环遍历ResolveInfo对象获取包名和类名
        for (i in 0 until apps.size) {
            val info = apps[i]
            val packageName = info.activityInfo.packageName
            val appsName = info.activityInfo.loadLabel(context.packageManager)
            if (appName == appsName) {
                return packageName
            }
        }
        return null
    }

    //APP重启
    open fun eAppRestart(application: Application, activity: Activity) {
        //启动页
        val intent = Intent(application, activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    //Activity重启
    open fun eActivityRestart(activity: Activity, activityList: ArrayList<Activity>? = null): Boolean {
        try {
            if (activityList != null) {
                for (av in activityList) {
                    av.finish()
                }
            } else {
                activity.finish()
            }
            val LaunchIntent = activity.packageManager.getLaunchIntentForPackage(activity.application.packageName)
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(LaunchIntent)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    //APP包名启动
    open fun eAppStart(activity: Activity, packageName: String? = null): Boolean {
        try {
            val LaunchIntent = activity.packageManager.getLaunchIntentForPackage(packageName
                    ?: activity.packageName)
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(LaunchIntent)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    //App运行判断
    open fun eIsAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcessInfoList) {
            if (processInfo.processName == packageName && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    //Activity运行判断
    open fun eActivityWhetherWorked(context: Context, className: String): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val list = am.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val cpn = list[0].topActivity
            if (className == cpn.className) {
                return true
            }
        }
        return false
    }

    //获取当前显示的Activity
    open fun eGetShowActivity(application: Application) = application.activityManager.getRunningTasks(1)[0].topActivity.className

    //判断Activity是否存在任务栈里面
    open fun eIsExistMainActivity(context: Context, activity: Class<*>): Boolean {
        val intent = Intent(context, activity)
        val cmpName = intent.resolveActivity(context.packageManager)
        var flag = false
        if (cmpName != null) { // 说明系统中存在这个activity
            val am: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskInfoList = am.getRunningTasks(10) //获取从栈顶开始往下查找的10个activity
            for (taskInfo in taskInfoList) {
                if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                    flag = true
                    break //跳出循环，优化效率
                }
            }
        }
        return flag
    }

    //服务运行判断 com.anubis.iva.Service.IVAService
    open fun eIsServiceRunning(context: Context, className: String): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.getRunningServices(0x7FFFFFFF)
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (className == aInfo.service.className) return true
        }
        return false
    }

    //后台服务杀死
    open fun eKillBackgroundProcesses(context: Context, packageName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var info: List<ActivityManager.RunningAppProcessInfo>? = am.runningAppProcesses
        if (info == null || info.isEmpty()) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        info = am.runningAppProcesses
        if (info == null || info.isEmpty()) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }

    //根据包名获取PID
    open fun eGetPackNamePID(context: Context, packName: String): Int? {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mRunningProcess = manager.runningAppProcesses
        for (amProcess in mRunningProcess) {
            if (packName == amProcess.processName)
                return amProcess.pid
        }
        return null
    }

    //软件安装判断
    open fun eIsAppInstall(mContext: Context, packageName: String): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.packageManager.getPackageInfo(
                    packageName, 0)

        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    //首选Summary项动态改变
    //sSummaryDynamicSetting(findPreference("root_screen") as PreferenceScreen)
    //Summary修改
    open fun Context.eSummaryModify(group: PreferenceGroup) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0..group.preferenceCount - 1) {
            val p = group.getPreference(i)
            if (p is PreferenceGroup) {
                eSummaryModify(p)
            } else {
                if ((p is ListPreference) or (p is EditTextPreference)) {
                    val value = sp.all[p.key]
                    p.summary = if (value == null) "" else "$value"
                    p.onPreferenceChangeListener = this as Preference.OnPreferenceChangeListener?
                }
            }
        }
    }
}

/**
 * 正则扩展类-----------------------------------------------------------------------------------------
 */
open class eRegex internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eRegex() }
    }

    //自定义正则
    fun eRegex(str: String, regEx: String = "[^0-9.]"): String {
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return (m.replaceAll("").trim { it <= ' ' })
    }

    //获取数字
    open fun eGetNumber(str: String): Int {
        val regEx = "[^0-9]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return (m.replaceAll("").trim { it <= ' ' }).toInt()
    }

    //IP格式判断
    open fun eIsIP(ip: String): Boolean {
        val regIP: String = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"
        return Pattern.compile(regIP).matcher(ip).matches()
    }

    //电话格式判断
    open fun eIsPhoneNumber(number: String): Boolean {
        val regExp: String = "^((13[0-9])|(15[^[4,5],\\D])|(16[^6,\\D])|(18[0,1,5-9])|(17[6,7,8]))\\d{8}$"
        val p: Pattern = Pattern.compile(regExp)
        val m: Matcher = p.matcher(number)
        return m.find()
    }

    //邮编格式判断
    open fun eIsZipNO(zipCode: String): Boolean {
        val str = "^[1-9][0-9]{5}$"
        return Pattern.compile(str).matcher(zipCode).matches()
    }

    //邮箱格式判断
    open fun eIsEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val regEm = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"
        return Pattern.compile(regEm).matcher(email).matches()//复杂匹配
    }

    //身份证格式判断
    open fun eIsIDCard(idCard: String): Boolean {
        val regID = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"
        return Pattern.compile(regID).matcher(idCard).matches()
    }

    //URL格式判断
    open fun eIsUrl(url: String): Boolean {
        val regUrl = "[a-zA-z]+://[^\\s]*"
        return Pattern.compile(regUrl).matcher(url).matches()
    }

    //中文验证
    open fun eIsZh(zh: String): Boolean {
        val reZh = "^[\\u4e00-\\u9fa5]+$"
        return Pattern.compile(reZh).matcher(zh).matches()
    }

    //车牌验证
    fun eIsLicensePlate(str: String): Boolean {
        val PLATE_NO_REGEX = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]" +
                "{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]" +
                "{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)" +
                "|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}[0-9]{4}[TDSHBXJ0-9]{1})" +
                "|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})"
        return Pattern.compile(PLATE_NO_REGEX).matcher(str).matches()
    }
}

/**
 * KeyDownExit事件监听扩展类--------------------------------------------------------------------------
 */
open class eKeyEvent internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eKeyEvent() }
    }

    private var Time: Long = 0
    open fun eSetKeyDownExit(activity: Activity, keyCode: Int, activityList: ArrayList<Activity>? = null, systemExit: Boolean = true, hint: String = "再按一次退出", exitHint: String = "APP已退出", ClickTime: Long = 2000, isExecute: Boolean = true, block: () -> Unit = {}): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - Time > ClickTime) {
                activity.eShowTip(hint)
                Time = System.currentTimeMillis()
                false
            } else {
                block()
                if (isExecute) {
                    if (activityList != null) {
                        for (activity in activityList) {
                            activity.finish()
                        }
                    }
                    activity.eShowTip(exitHint)
                    activity.finish()
                    if (systemExit) {
                        System.exit(0)
                    }
                }
                true
            }
        } else
            false
    }
}


/**
 * 时间扩展类-----------------------------------------------------------------------------------------
 */
open class eTime internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTime() }
    }

    /**
     * 时间差
     * @param endDate 上次更新时间
     * @param nowDate 当前时间
     * @return 天数
     */
    open fun eGetTimeDifference(nowDate: Date, endDate: Date, type: Int = Calendar.HOUR): Long {
        Calendar.SECOND
        val nd = (1000 * 24 * 60 * 60).toLong()
        val nh = (1000 * 60 * 60).toLong()
        val nm = (1000 * 60).toLong()
        val ns = 1000
        // 获得两个时间的毫秒时间差异
        val diff = nowDate.time - endDate.time
        return when (type) {
            Calendar.DATE -> diff / nd
            Calendar.HOUR -> diff % nd / nh
            Calendar.MINUTE -> diff % nd % nh / nm
            Calendar.SECOND -> diff % nd % nh % nm / ns
            else -> diff % nd / nh
        }
    }

    //获取当前星期
    open fun eGetCurrentWeek() = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
        "1" -> "星期天"
        "2" -> "星期一"
        "3" -> "星期二"
        "4" -> "星期三"
        "5" -> "星期四"
        "6" -> "星期五"
        "7" -> "星期六"
        else -> "发生错误"
    }

    //获取当前时间   (yyyy-MM-dd HH:mm:ss)
    fun eGetTime(format: String = "yyyy-MM-dd HH:mm:ss", calendar: Int = Calendar.DATE, distance: Int = 0, extendBlock: ((Calendar) -> Unit)? = null): String {

        val dft = SimpleDateFormat(format)
        val beginDate = Date()
        val date = Calendar.getInstance()
        date.time = beginDate
        date.add(calendar, distance)
        extendBlock?.let { it(date) }
        var endDate: Date? = null
        try {
            endDate = dft.parse(dft.format(date.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dft.format(endDate)
    }


    //获取时间戳格式转时间
    open fun eGetCuoFormatTime(dateCuo: Long, format: String = "yyyy-MM-dd HH:mm:ss") = SimpleDateFormat(format).format(Date(dateCuo))

    //时间格式字符串转时间
    fun eGetDataStrToDate(dateStr: String, format: String = "yyyy-MM-dd HH:mm:ss") = try {
        val date = SimpleDateFormat(format).parse(dateStr)
        date
    } catch (e: ParseException) {
        e.eLogE("eGetStringtoDate")
        null
    }

    //时间转时间戳
    open fun eGetCuoTime(date: String = eGetTime(), pattern: String = "yyyy-MM-dd HH:mm:ss", type: Int = Calendar.SECOND): String {
        val date = SimpleDateFormat(pattern).parse(date).time.toString()
        return if (type == Calendar.SECOND) date.substring(0, 10) else date
    }

    //将GMT格式的时间转换成yyyy-MM-dd HH:mm:ss格式
    open fun eGetGMTToDateTime(GMTTime: String, pattern: String = "EEE, d MMM yyyy HH:mm:ss 'GMT'", format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sf = SimpleDateFormat(pattern, Locale.ENGLISH)
        val sdf = SimpleDateFormat(format)
        var date: Date? = null
        var dateTime = ""
        try {
            date = sf.parse(GMTTime)
            dateTime = sdf.format(date)
        } catch (e: ParseException) {
            e.eLogE("eGetGMTToDateTime")
        }
        return dateTime
    }
}

/**
 * 网络扩展类-----------------------------------------------------------------------------------------
 */
open class eNetWork internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eNetWork() }
    }

    // 判断是否有网
    open fun eIsNetworkAvailable(context: Context): Boolean {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // 获取NetworkInfo对象
        val networkInfo = connectivityManager.allNetworkInfo
        if (networkInfo != null && networkInfo.isNotEmpty()) {
            for (i in networkInfo.indices) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

    //检测网络是否可用
    open fun eIsNetworkOnline(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("ping -c 1 114.114.114.114")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.eLogE("eIsNetworkOnline")
        } catch (e: InterruptedException) {
            e.eLogE("eIsNetworkOnline")
        }
        return false
    }


    // 获取PING延迟
    open fun eGetNetDelayTime(url: String): String {
        var delay = String()
        var output = ""
        try {
            val p = Runtime.getRuntime().exec("/system/bin/ping -c 4 " + url)
            val buf = BufferedReader(InputStreamReader(p!!.inputStream))
            var str: String? = ""
            while (str != null) {
                str = buf.readLine()
                eLog("str$str")
                output += str
                if (output.contains("avg")) {
                    val i = str.indexOf("/", 20)
                    val j = str.indexOf(".", i)
                    delay = str.substring(i + 1, j)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return delay
    }
}


/**
 * 设备信息扩展类--------------------------------------------------------------------------------------
 */
open class eDevice internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eDevice() }
    }

    // 获取屏幕密度，宽高
    open fun eGetDensityWidthHeight(mContext: Context): Array<Any> {
        val resources = mContext.resources
        val dm = resources.displayMetrics
        val density = dm.density
        val width = dm.widthPixels
        val height = dm.heightPixels
        return arrayOf(density, width, height)
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    open fun eGetDpToPx(context: Context, dpValue: Float) = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()

    //获取主板型号
    open fun eGetMotherboardModel() = Build.MODEL

    //获取系统的SDK版本
    open fun eGetSysSDKVersion() = Build.VERSION.SDK

    //获取系统的无线电固件版本
    open fun eGetRadioVersion() = Build.RADIO

    //获得显示
    open fun eGetDisPlay() = Build.DISPLAY


    //获取本地IP
    open fun eGetHostIP(): String {
        var hostIp: String = "0.0.0.0"
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            var ia: InetAddress? = null
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement() as NetworkInterface
                val ias = ni.inetAddresses
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement()
                    if (ia is Inet6Address) {
                        continue// skip ipv6
                    }
                    val ip = ia!!.hostAddress
                    if ("127.0.0.1" != ip) {
                        hostIp = ia.hostAddress
                        break
                    }
                }
            }
        } catch (e: SocketException) {
            Log.i("yao", "SocketException")
            e.printStackTrace()
        }
        return hostIp
    }

    /**震动器
     * @param patter;patter[0]表示静止的时间，patter[1]代表的是震动的时间 类推
     * @param repeat;0循环 -1不循环
     */
    fun Context.eVibrator(
            isRepeat: Boolean = false,
            patter: LongArray = longArrayOf(0, 1000)
    ): Vibrator {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(patter, if (isRepeat) 0 else -1)
        return vibrator
    }

    fun Vibrator.eClean() {
        cancel()
    }
}

/**
 * 蓝牙扩展类-----------------------------------------------------------------------------------------
 */
open class eBluetooth internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eBluetooth() }
    }

    // 打开蓝牙
    open fun eOpenBluetooth(bel_name: String, context: Context) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            context.eShowTip("该设备不支持蓝牙")
        } else {
            if (mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.name = bel_name
                eSetDiscoverableTimeout()
            } else {
                mBluetoothAdapter.enable()
                context.eShowTip("已打开蓝牙")
            }
        }
    }

    //关闭蓝牙
    open fun eCloseBluetooth(context: Context) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            context.eShowTip("该设备不支持蓝牙")
        } else {
            if (!mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.disable()
                context.eShowTip("已关闭蓝牙")
            }
        }
    }

    //设置开放检测
    open fun eSetDiscoverableTimeout(timeout: Int = 0) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        try {
            val setDiscoverableTimeout = BluetoothAdapter::class.java.getMethod("setDiscoverableTimeout", Int::class.javaPrimitiveType)
            setDiscoverableTimeout.isAccessible = true
            val setScanMode = BluetoothAdapter::class.java.getMethod("setScanMode", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            setScanMode.isAccessible = true
            setDiscoverableTimeout.invoke(adapter, timeout)
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * eAssets文件扩展类--------------------------------------------------------------------------------------
 */
open class eAssets internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eAssets() }
    }

    //获取attr
    fun eGetAttr(context: Context, id: Int): Int {
        return with(TypedValue()) {
            context.theme.resolveAttribute(id, this, true)
            data
        }
    }

    //assets文件复制 文件夹路径
    open fun eAssetsToFile(context: Context, assetsFilePath: String, copyFilePath: String): Boolean {
        try {
            if (!File(copyFilePath).exists() && eFile.eInit.eCheckFile(copyFilePath)) {
                val inputStream = context.resources.assets.open(assetsFilePath)// assets文件夹下的文件
                var fileOutputStream = FileOutputStream(copyFilePath)// 保存到本地的文件夹下的文件
                val buffer = ByteArray(1024)
                var count = 0
                while (inputStream.read(buffer).apply { count = this } > 0) {
                    fileOutputStream.write(buffer, 0, count)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
            }
            return true
        } catch (e: IOException) {
            e.eLogE("文件复制错误")
            return false
        }
    }

    //获取Assets文件图片
    open fun eGetBitmap(context: Context, filename: String): Bitmap? {
        val bitmap: Bitmap
        val asm = context.assets
        try {
            val `is` = asm.open(filename)
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return bitmap
    }

    //加载Assets文件模型
    open fun eLoadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}

/**
 * 矩阵扩展类--------------------------------------------------------------------------------------
 */
open class eMatrix internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eMatrix() }

        open fun eRectFtoRect(rectF: RectF) = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())

    }
}

/**
 * Bitmap扩展类--------------------------------------------------------------------------------------
 */
open class eBitmap internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eBitmap() }
    }

    //Bitmap释放
    open fun eGcBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle() // 回收图片所占的内存
            System.gc() // 提醒系统及时回收
        }
    }

    //Bitmap镜像水平翻转
    open fun eBitmapHorizontalFlip(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    //Bitmap矩形绘制
    open fun eBitmapRect(bitmap: Bitmap, rect: Rect, paint: Paint? = null): Bitmap {
        return eBitmapRect(bitmap, arrayListOf(rect), paint)
    }

    open fun eBitmapRect(bitmap: Bitmap, rect: ArrayList<Rect>): Bitmap {
        return eBitmapRect(bitmap, rect, null)
    }

    open fun eBitmapRect(bitmap: Bitmap, rect: ArrayList<Rect>, color: Int = Color.GREEN): Bitmap {
        val mPaint = Paint()
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 5f
        return eBitmapRect(bitmap, rect, mPaint)
    }

    open fun eBitmapRect(bitmap: Bitmap, rect: ArrayList<Rect>, paint: Paint? = null): Bitmap {
        val drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val mPaint = if (paint == null) {
            val mPaint = Paint()
            mPaint.color = Color.GREEN
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = 5f
            mPaint
        } else {
            paint
        }
        val canvas = Canvas(drawBitmap)
        rect.forEach {
            canvas.drawRect(it, mPaint)
        }
        return drawBitmap
    }

    //Bitmap转ByteArray工具
    open fun eBitmapToByteArray(image: Bitmap): ByteArray {
        val bytes = image.byteCount
        val buffer = ByteBuffer.allocate(bytes)
        image.copyPixelsToBuffer(buffer)
        return buffer.array()
    }

    //Bitmap转Base64工具
    open fun eBitmapToBase64(bitmap: Bitmap): String? {
        var baos: ByteArrayOutputStream? = null
        var reslut: String? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                baos.flush()
                baos.close()
                // 转换为字节数组
                val byteArray = baos.toByteArray()
                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                baos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return reslut
    }

    //Base64转Bitmap工具
    open fun eBase64ToBitmap(base64String: String): Bitmap {
        val decode = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decode, 0, decode.size)
    }

    //NV21 Bytes字节转Bitmap
    open fun eByteArrayToBitmp(mImageNV21: ByteArray, width: Int, height: Int, rect: Rect = Rect(0, 0, width, height), rotate: Float = 0f, quality: Int = 80, isFlip: Boolean = false): Bitmap? {
        var mBitmap: Bitmap? = null
        try {
            val image = YuvImage(mImageNV21, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(rect, quality, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            mBitmap = eBitmapRotateFlip(bmp, rotate, isFlip)
            stream.close()
//            eGcBitmap(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mBitmap
    }

    //YUV Bytes字节转文件
    open fun eByteArrayToFile(tmpFile: File, yuvBytes: ByteArray, w: Int, h: Int, imageformat: Int = ImageFormat.NV21, rotate: Int = 0, quality: Int = 80): Boolean {
        // 通过YuvImage得到Bitmap格式的byte[]
        try {
            if (!tmpFile.exists())
                tmpFile.createNewFile()
            val yuvImage = YuvImage(yuvBytes, imageformat, w, h, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), rotate, out)
            val dataBmp = out.toByteArray()
            // 生成Bitmap
            val bitmap = BitmapFactory.decodeByteArray(dataBmp, 0, out.size())

            // 旋转
            val matrix = Matrix()
            matrix.setRotate(rotate.toFloat())
            val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            val fos = FileOutputStream(tmpFile)
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            return true
        } catch (e: Exception) {
            e.eLogE("保存失败")
            return false
        }
    }

    //Image获取字节
    open fun eGetImagetoByteArray(image: Image?): ByteArray? {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            val planes = image!!.planes
            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            val width = image.width
            val height = image.height
            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            val yuvBytes = ByteArray(width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8)
            //目标数组的装填到的位置
            var dstIndex = 0
            //临时存储uv数据的
            val uBytes = ByteArray(width * height / 4)
            val vBytes = ByteArray(width * height / 4)
            var uIndex = 0
            var vIndex = 0

            var pixelsStride: Int
            var rowStride: Int
            for (i in planes.indices) {
                pixelsStride = planes[i].pixelStride
                rowStride = planes[i].rowStride

                val buffer = planes[i].buffer

                //如果pixelsStride==2，一般的Y的buffer长度=800x600，UV的长度=800x600/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)

                var srcIndex = 0
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (j in 0 until height) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width)
                        srcIndex += rowStride
                        dstIndex += width
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (j in 0 until height / 2) {
                        for (k in 0 until width / 2) {
                            uBytes[uIndex++] = bytes[srcIndex]
                            srcIndex += pixelsStride
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (j in 0 until height / 2) {
                        for (k in 0 until width / 2) {
                            vBytes[vIndex++] = bytes[srcIndex]
                            srcIndex += pixelsStride
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2
                        }
                    }
                }
            }
            //根据要求的结果类型进行填充
            for (i in vBytes.indices) {
                yuvBytes[dstIndex++] = uBytes[i]
                yuvBytes[dstIndex++] = vBytes[i]
            }
            return yuvBytes
        } catch (e: Exception) {
            image?.close()
            e.eLogE("")
        }

        return null
    }

    //图片旋转翻转
    open fun eBitmapRotateFlip(bitmap: Bitmap?, rotate: Float = 0f, isFlip: Boolean = false): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.setRotate(rotate)
        if (isFlip)
            matrix.postScale(-1f, 1f)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        if (newBM == bitmap) {
            return newBM
        }
        bitmap.recycle()
        return newBM
    }

    //bitmap 宽高压缩
    open fun eBitmapToZoom(bitmap: Bitmap, zoomFactor: Int = 1, filter: Boolean = true) = eBitmapToZoom(bitmap, bitmap.width / zoomFactor, bitmap.height / zoomFactor, filter)

    open fun eBitmapToZoom(bitmap: Bitmap, width: Int, height: Int, filter: Boolean = true): Bitmap {
        val tBitmap = Bitmap.createScaledBitmap(bitmap, width, height, filter)
        return tBitmap
    }


    //Bitmap压缩 (设置压缩率或者 设置大小)
    open fun eBitmapCompress(bitmap: Bitmap, quality: Int = 100, outSize: Int? = null): Bitmap? {
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, if (outSize == null) quality else 100, baos)
        if (outSize != null) {
            var options = 100
            while ((baos.toByteArray().size / 1024).eLog("size") > outSize / 10 && options != 5) {
                baos.reset()//重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, if (options < 5) {
                    options = 5
                    5
                } else options, baos)
                eLog("options:$options")
                if (options != 5)
                    options -= 5
            }
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())
        return BitmapFactory.decodeStream(isBm, null, null)
    }


    //Bitmap转文件
    open fun eBitmapToFile(bitmap: Bitmap?, absPath: String, quality: Int = 80): Boolean {
        return eBitmapToFile(bitmap, File(absPath), quality)
    }

    //Bitmap转文件
    open fun eBitmapToFile(bitmap: Bitmap?, file: File, quality: Int = 80): Boolean {
        if (bitmap == null) {
            eLogE("eBitmapToFile:bitmap==null")
            return false
        } else {
            var fos: FileOutputStream? = null
            try {
                if (!eFile.eInit.eCheckFile(file))
                    return false.apply { eLogE(file.path + "-不存在") }
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos)
                fos.flush()
                return true
            } catch (var13: Exception) {
                var13.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (var12: IOException) {
                        var12.printStackTrace()
                    }
                }
            }
            return false
        }
    }

    //Drawable转Bitmap
    open fun eDrawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val config = if (drawable.opacity != PixelFormat.OPAQUE)
            Bitmap.Config.ARGB_8888
        else
            Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    //获取本地文件图片
    open fun eGetFileBitmap(path: String) = BitmapFactory.decodeFile(path)

    //归一化图片到[0, 1]
    open fun eNormalizeBitmap(bitmap: Bitmap): Array<Array<FloatArray>> {
        val h = bitmap.height
        val w = bitmap.width
        val floatValues = Array(h) { Array(w) { FloatArray(3) } }
        val imageMean = 0.0f
        val imageStd = 255.0f
        val pixels = IntArray(h * w)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, w, h)
        for (i in 0 until h) { // 注意是先高后宽
            for (j in 0 until w) {
                val `val` = pixels[i * w + j]
                val r = ((`val` shr 16 and 0xFF) - imageMean) / imageStd
                val g = ((`val` shr 8 and 0xFF) - imageMean) / imageStd
                val b = ((`val` and 0xFF) - imageMean) / imageStd
                val arr = floatArrayOf(r, g, b)
                floatValues[i][j] = arr
            }
        }
        return floatValues
    }

    open fun eYUV420SPToARGB8888(input: ByteArray, width: Int, height: Int, output: IntArray) {
        val frameSize = width * height
        var j = 0
        var yp = 0
        while (j < height) {
            var uvp = frameSize + (j shr 1) * width
            var u = 0
            var v = 0

            var i = 0
            while (i < width) {
                val y = 0xff and input[yp].toInt()
                if (i and 1 == 0) {
                    v = 0xff and input[uvp++].toInt()
                    u = 0xff and input[uvp++].toInt()
                }
                output[yp] = eYUV2RGB(y, u, v)
                i++
                yp++
            }
            j++
        }
    }

    open fun eYUV420ToARGB8888(
            yData: ByteArray,
            uData: ByteArray,
            vData: ByteArray,
            width: Int,
            height: Int,
            yRowStride: Int,
            uvRowStride: Int,
            uvPixelStride: Int,
            out: IntArray) {
        var yp = 0
        for (j in 0 until height) {
            val pY = yRowStride * j
            val pUV = uvRowStride * (j shr 1)

            for (i in 0 until width) {
                val uv_offset = pUV + (i shr 1) * uvPixelStride
                out[yp++] = eYUV2RGB(0xff and yData[pY + i].toInt(), 0xff and uData[uv_offset].toInt(), 0xff and vData[uv_offset].toInt())
            }
        }
    }

    open fun eYUV2RGB(y: Int, u: Int, v: Int, kMaxChannelValue: Int = 262143): Int {
        var y = y
        var u = u
        var v = v
        // Adjust and check YUV values
        y = if (y - 16 < 0) 0 else y - 16
        u -= 128
        v -= 128

        // This is the floating point equivalent. We do the conversion in integer
        // because some Android devices do not have floating point in hardware.
        // nR = (int)(1.164 * nY + 2.018 * nU);
        // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
        // nB = (int)(1.164 * nY + 1.596 * nV);
        val y1192 = 1192 * y
        var r = y1192 + 1634 * v
        var g = y1192 - 833 * v - 400 * u
        var b = y1192 + 2066 * u

        // Clipping RGB values to be inside boundaries [ 0 , kMaxChannelValue ]
        r = if (r > kMaxChannelValue) kMaxChannelValue else if (r < 0) 0 else r
        g = if (g > kMaxChannelValue) kMaxChannelValue else if (g < 0) 0 else g
        b = if (b > kMaxChannelValue) kMaxChannelValue else if (b < 0) 0 else b
        return -0x1000000 or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)
    }

    open fun eGetYUVByteSize(width: Int, height: Int): Int {
        // The luminance plane requires 1 byte per pixel.
        val ySize = width * height
        // The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
        // Each 2x2 block takes 2 bytes to encode, one each for U and V.
        val uvSize = (width + 1) / 2 * ((height + 1) / 2) * 2

        return ySize + uvSize
    }


}


/**
 * Imagg图片处理辅助扩展类--------------------------------------------------------------------------------------
 */
open class eImage internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eImage() }
    }

    /**
     * 利用 rgba 来修改图片
     * @param bm            所需修改的图片
     * @param hue           色调值
     * @param saturation    饱和度
     * @param lum           亮度
     * @return              修改完成的图片
     */
    open fun eGetHandleImageForARGB(bm: Bitmap, hue: Float, saturation: Float, lum: Float): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        //色调 0-R 1-G 2-B
        val hueMatrix = ColorMatrix()
        hueMatrix.setRotate(0, hue)
        hueMatrix.setRotate(1, hue)
        hueMatrix.setRotate(2, hue)

        //饱和度
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)

        //亮度
        val lumMatrix = ColorMatrix()
        lumMatrix.setScale(lum, lum, lum, 1f)

        //将色调,饱和度,亮度全糅合要一起
        val imageMatrix = ColorMatrix()
        imageMatrix.postConcat(hueMatrix)
        imageMatrix.postConcat(satMatrix)
        imageMatrix.postConcat(lumMatrix)

        paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
        canvas.drawBitmap(bm, 0f, 0f, paint)
        return bmp
    }

    //底片效果
    open fun eGetHandleImageNegative(bm: Bitmap): Bitmap {
        val width = bm.width
        val height = bm.height
        var color: Int
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)
        bm.getPixels(oldPx, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            color = oldPx[i]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)

            r1 = eGetJudgedData(255 - r)
            g1 = eGetJudgedData(255 - g)
            b1 = eGetJudgedData(255 - b)
            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    //老照片
    open fun eGetHandleImagePixelsOldPhoto(bm: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height,
                Bitmap.Config.ARGB_8888)
        val width = bm.width
        val height = bm.height
        var color = 0
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)

        bm.getPixels(oldPx, 0, bm.width, 0, 0, width, height)
        for (i in 0 until width * height) {
            color = oldPx[i]
            a = Color.alpha(color)
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)

            r1 = eGetJudgedData((0.393 * r + 0.769 * g + 0.189 * b).toInt())
            g1 = eGetJudgedData((0.349 * r + 0.686 * g + 0.168 * b).toInt())
            b1 = eGetJudgedData((0.272 * r + 0.534 * g + 0.131 * b).toInt())

            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    //浮雕效果
    open fun eGetHandleImagePixelsRelief(bm: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height,
                Bitmap.Config.ARGB_8888)
        val width = bm.width
        val height = bm.height
        var color = 0
        var colorBefore = 0
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)

        bm.getPixels(oldPx, 0, bm.width, 0, 0, width, height)
        for (i in 1 until width * height) {
            colorBefore = oldPx[i - 1]
            a = Color.alpha(colorBefore)
            r = Color.red(colorBefore)
            g = Color.green(colorBefore)
            b = Color.blue(colorBefore)

            color = oldPx[i]
            r1 = Color.red(color)
            g1 = Color.green(color)
            b1 = Color.blue(color)

            r = eGetJudgedData(r - r1 + 127)
            g = eGetJudgedData(g - g1 + 127)
            b = eGetJudgedData(b - b1 + 127)
            newPx[i] = Color.argb(a, r, g, b)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    open fun eGetJudgedData(oldData: Int): Int {
        var newData = oldData
        if (newData > 255) {
            newData = 255
        } else if (newData < 0) {
            newData = 0
        }
        return newData
    }
}

/**
 * 文件转换扩展类--------------------------------------------------------------------------------------
 */
open class eFile internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eFile() }
    }

    /**
     * 复制单个文件
     * @param oldPathName String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPathName String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     *         <code>false</code> otherwise
     */

    open fun eCopyFile(oldFilePath: String, newFilePath: String): Boolean {
        try {
            val oldFile = File(oldFilePath)
            if (!oldFile.exists()) {
                eLogE("eCopyFile: oldFile 不存在")
                return false
            }
            if (!oldFile.isFile) {
                eLogE("eCopyFile: oldFile 不是文件")
                return false
            }
            if (!oldFile.canRead()) {
                eLogE("eCopyFile: oldFile 无法读取")
                return false
            }
            eCheckFile(newFilePath)
            val newFile = if (File(newFilePath).isDirectory) {
                newFilePath + "/" + oldFile.name
            } else {
                File(newFilePath).path
            }
            eCheckFile(newFile)
            val fileInputStream = FileInputStream(oldFilePath)
            val fileOutputStream = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            var byteRead = 0
            while ((fileInputStream.read(buffer).apply { byteRead = this }) != -1) {
                fileOutputStream.write(buffer, 0, byteRead)
            }
            fileInputStream.close()
            fileOutputStream.flush()
            fileOutputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    open fun eCheckFile(path: String, isCreate: Boolean = true): Boolean {
        return eCheckFile(File(path), isCreate)
    }

    open fun eCheckFile(file: File, isCreate: Boolean = true): Boolean {
        return if (isCreate) {
            if (file.exists()) {
                return true
            } else {
                if (file.isDirectory || !file.name.contains(".")) {
                    file.mkdirs()
                } else {
                    File(file.parent).mkdirs()
                    File(file.path).createNewFile()
                }
            }
        } else {
            file.exists()
        }
    }

    //    文件转Base64
    open fun eFileToBase64(path: String): String? {
        var inputFile: FileInputStream? = null
        try {
            val file = File(path)
            inputFile = FileInputStream(file)
            val buffer = ByteArray(inputFile.available())
            val length = inputFile.read(buffer)
            inputFile.close()
            return Base64.encodeToString(buffer, 0, length, Base64.DEFAULT)
        } catch (e: Exception) {
            inputFile?.close()
            eLogE("转换错误")
            return null
        }
    }


    //    base64字符保存文本文件
    open fun eBase64StrToFile(base64Code: String, targetPath: String) {
        val buffer = base64Code.toByteArray()
        val out = FileOutputStream(targetPath)
        out.write(buffer)
        out.close()
    }

    //Base64转文件
    open fun eBase64ToFile(base64: String, file: File): File? {
        var out: FileOutputStream? = null
        try {
            // 解码，然后将字节转换为文件
            if (!file.exists())
                file.createNewFile()
            val bytes = Base64.decode(base64, Base64.DEFAULT)// 将字符串转换为byte数组
            val input = ByteArrayInputStream(bytes)
            val buffer = ByteArray(1024)
            out = FileOutputStream(file)
            var bytesum = 0
            var byteread = 0
            while ((input.read(buffer)).apply { byteread = this } != -1) {
                bytesum += byteread
                out.write(buffer, 0, byteread) // 文件写操作
            }
        } catch (ioe: Exception) {
            out?.close()
            ioe.eLogE("转换异常")
            return null
        }
        return file
    }

    /**
     * 屏幕截图
     * @param activity
     * @return
     */
    open fun eActivityScreenShot(activity: Activity): Bitmap {
        val dView = activity.window.decorView
        dView.setDrawingCacheEnabled(true)
        dView.buildDrawingCache()
        return Bitmap.createBitmap(dView.drawingCache)
    }
}

/**
 * 字符转换扩展类--------------------------------------------------------------------------------------
 */
open class eString internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eString() }
    }

    fun TextView.eSpannableTextView(str: String, startAndEndIndexArray: Array<Pair<Int, Int>>? = arrayOf(Pair(0, 0)), colorArray: Array<Int>? = arrayOf(Color.RED), clickBlockArray: Array<() -> Unit>? = null) {
        if (str.isEmpty())
            return
        text = ""
        var startIndex = 0
        //这个一定要记得设置，不然点击不生效
        movementMethod = LinkMovementMethod.getInstance()
        startAndEndIndexArray?.forEachIndexed { index, pair ->
            var subStr = ""
            try {
                subStr = str.substring(startIndex, if (index + 1 == startAndEndIndexArray.size) str.length else pair.second).eLog("substring")
                val spb = SpannableStringBuilder(subStr)
                spb.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        clickBlockArray?.let {
                            if (index > it.size - 1) {
                                it.last()()
                            } else {
                                it[index]()
                            }
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        colorArray?.let {
                            if (index > it.size - 1) {
                                ds.color = it.last()
                            } else {
                                ds.color = it[index]
                            }
                        }
                    }
                }, pair.first - startIndex, pair.second - startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(spb)
                startIndex = pair.second
            } catch (e: IndexOutOfBoundsException) {
                append(subStr)
                return@forEachIndexed
            } catch (e: Exception) {
                e.eLogE("eSpannableTextView")
                return@forEachIndexed
            }
        }
    }

    //数据转换
    open fun eGetFormatSize(size: Long) = when {
        size < 1 -> "0K"
        size in 1..1023 -> size.toString() + " K"
        1024 < size -> (size / 1024).toString() + " M"
        else -> "0 K"
    }


    //数值段获取
    open fun eGetNumberPeriod(str: String, start: Any, end: Any): String {
        val Str = str.trim()
        val Start: Int = when (start) {
            is Int -> start
            is String -> str.indexOf(start) + 1
            else -> {
                0
            }
        }
        val End: Int = when (end) {
            is Int -> end
            is String -> if (end == "MAX") str.length else str.indexOf(end)
            else -> {
                0
            }
        }
        return if (Str.isEmpty()) "" else Str.substring(Start, End).trim()
    }

    //判断是否为中文
    open fun eIsChinese(c: Char) = c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5// 根据字节码判断

    //判断是否有英文
    open fun eIsHasEglish(chars: CharArray): Boolean {
        for (i in chars.indices) {
            if (chars[i].toInt() >= 97 && chars[i].toInt() <= 122) {
                return true
            }
        }
        return false
    }

    //十六进制字符串转字节
    open fun eGetHexStringToBytes(hexString: String): ByteArray? {
        var hexString = hexString
        if (hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (eGetCharToByte(hexChars[pos]).toInt() shl 4 or eGetCharToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    //字节转十六进字符串
    open fun eGetByteArrToHexStr(byteArr: ByteArray): String {
        val iLen = byteArr.size
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        val sb = StringBuffer(iLen * 2)
        for (i in 0 until iLen) {
            var intTmp = byteArr[i].toInt()
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0")
            }
            sb.append(Integer.toString(intTmp, 16))
        }
        return sb.toString()
    }

    //字符转字节
    open fun eGetCharToByte(c: Char) = indexOf("0123456789ABCDEF", c).toByte()

    //字节转字符串
    open fun eGetBytesToString(src: ByteArray, lenth: Int): String? {
        val stringBuilder = StringBuilder("")
        if (src.isEmpty()) {
            return null
        }
        for (i in 0 until lenth) {
            val v = src[i] and 0xFF.toByte()
            val hv = Integer.toHexString(v.toInt())
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }

    open fun eGetBytesToInt(src: ByteArray, offset: Int) = (src[offset].toInt() and 0x02
            or (src[offset + 1].toInt() and 0x09 shl 8)
            or (src[offset + 2].toInt() and 0x12 shl 16)
            or (src[offset + 3].toInt() and 0x02 shl 24)
            or (src[offset + 4].toInt() and 0x11 shl 32)
            or (src[offset + 5].toInt() and 0x03 shl 40)
            or (src[offset + 6].toInt() and 0x10 shl 48)
            or (src[offset + 7].toInt() and 0x20 shl 56)
            or (src[offset + 8].toInt() and 0x30 shl 53)
            or (src[offset + 9].toInt() and 0xff shl 61)
            or (src[offset + 10].toInt() and 0x03 shl 64))


    //MD5加密
    open fun eGetEncodeMD5(str: String, digits: Int = 32): String? {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest: ByteArray = instance.digest(str.toByteArray())
            val sb: StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i: Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            return if (digits == 32) sb.toString() else sb.toString().substring(8, 24)

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    //base64加密
    open fun eBase64Encode(str: String, charset: Charset = Charsets.UTF_8, flags: Int = Base64.DEFAULT) = eBase64Encode(str.toByteArray(charset), flags)
    open fun eBase64Encode(bytes: ByteArray, flags: Int = Base64.DEFAULT) = Base64.encodeToString(bytes, flags)

    //base64解密
    open fun eBase64Decode(strBase64: String, charset: Charset = Charsets.UTF_8, flags: Int = Base64.DEFAULT) = eBase64Decode(strBase64.toByteArray(charset), flags)
    open fun eBase64Decode(bytes: ByteArray, flags: Int = Base64.DEFAULT) = String(Base64.decode(bytes, flags))

    //字符串截取
    open fun eInterception(str: String, lenght: Int = 1024, symbol: String = ","): String {
        var j = 0
        var s = ""
        var section = if (str.length % lenght == 0) (str.length / lenght) - 1 else str.length / lenght
        if (0 == section) {
            s = str
        }
        val sb = StringBuffer(str)
        for (i in 1..section) {
            s = sb.insert(lenght * i + j, symbol).toString()
            j++
        }
        return s
    }

}

/**
 * 运行权限扩展类--------------------------------------------------------------------------------------
 */
open class ePermissions internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ePermissions() }
    }

    //系统安装权限
    open fun eSetInstallPermissions(context: Context): Boolean {
        var str = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context.packageManager.canRequestPackageInstalls()) {
                str = true
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:${context.packageName}")
                (context as Activity).startActivityForResult(intent, 1)
            }
        } else {
            str = true
        }
        return str
    }

    //系统设置修改权限
    open fun eSetSystemPermissions(context: Context): Boolean {
        var str = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            } else {
                str = Settings.System.canWrite(context)
            }
        } else {
            str = true
        }
        return str
    }


    //授权判断
    open fun eSetPermissions(
            activity: Activity,
            permissionsArray: Array<out String>,
            requestCode: Int = 1
    ): Boolean {
        val permissionsList = ArrayList<String>()
        for (permission in permissionsArray) {
            if (ContextCompat.checkSelfPermission(
                            activity,
                            permission
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
            }
        }
        return if (permissionsList.isEmpty()) {
            eLog("全部授权")
            //未授予的权限为空，表示都授予了
            true
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    permissionsList.toArray(arrayOfNulls<String>(permissionsList.size)),
                    1
            )
            permissionsList.size.eLog("permissionsList.size")
            eLog("未授权")
            false
        }
    }

    //显示授权设置
    open fun eSetOnRequestPermissionsResult(
            activity: Activity,
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray,
            isPermissionsOKHint: String = "授权完成",
            isPermissionsNoHint: String = "授权未完成"
    ) {
        var statusPermissions = Pair<Boolean, ArrayList<String>>(true, arrayListOf())
        when (requestCode) {
            1 -> {
                permissions.forEachIndexed { index, s ->
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        val ps = statusPermissions.second
                        ps.add(s)
                        statusPermissions = statusPermissions.copy(false, ps)
                    }
                }
                if (statusPermissions.first) {
                    if (isPermissionsOKHint.isNotEmpty())
                        activity.eShowTip(isPermissionsOKHint)
                } else {
                    if (isPermissionsNoHint.isNotEmpty())
                        activity.eShowTip("$isPermissionsNoHint:${statusPermissions.second.toArray()!!.contentToString()}")
                }
            }
        }
    }
}


/**
 * linux命令扩展类------------------------------------------------------------------------------------
 */
open class eShell internal constructor() {
    companion object {
        val remount = "mount -o remount,rw rootfs "
        val install = "pm install -r"
        val kill = "am force-stop"
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eShell() }
    }


    //判断是否有Root权限
    open fun eHaveRoot(): Boolean {
        try {
            Runtime.getRuntime().exec("su").toString()
        } catch (e: Exception) {
            return false
        }
        return true
    }


    //执行命令并且输出结果
    open fun eExecShell(shell: String): String {
        var result = ""
        var dos: DataOutputStream? = null
        var dis: DataInputStream? = null
        try {
            val p = Runtime.getRuntime().exec("su")// 经过Root处理的android系统即有su命令
            dos = DataOutputStream(p.outputStream)
            dis = DataInputStream(p.inputStream)
            eLog(shell)
            dos.writeBytes(shell + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String? = ""
            while ((dis.readLine()).apply { line = this } != null) {
                result += line + "\n"
            }
            p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (dis != null) {
                try {
                    dis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    //执行命令但不关注结果输出
    open fun eExecShellSilent(shell: String): Int {
        var result = -1;
        var dos: DataOutputStream? = null
        try {
            var p = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(p.outputStream)
            eLog(shell)
            dos.writeBytes(shell + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            p.waitFor()
            result = p.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    //长时间获取返回  测试
    open fun eTExecShellOut(shell: String) {
        var mReader: BufferedReader? = null
        var mRunning = true
        var cmds: String? = null
        var mPID: String
        var out: FileOutputStream? = null
        var logcatProc: Process? = null
        try {
            eLog(shell)
            logcatProc = Runtime.getRuntime().exec(shell)
            mReader = BufferedReader(InputStreamReader(logcatProc.inputStream), 1024);
            var line = "";
            while (mRunning && (mReader.readLine().apply { line = this }) != null) {
                if (!mRunning) {
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                if (line.contains(android.os.Process.myPid().toString())) {
                    eLog("eShell:$line")
//                    out.write((simpleDateFormat2.format(new Date()) + "  " + line + "\n").getBytes());
                }
            }
        } catch (e: IOException) {
            e.printStackTrace();
        } finally {
            if (logcatProc != null) {
                logcatProc.destroy();
                logcatProc = null;
            }
            if (mReader != null) {
                try {
                    mReader.close();
                    mReader = null;
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (e: IOException) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }

    //Shell APP重启
    open fun eAppReboot(application: Application, clazz: Class<*>) {
        eExecShell("am force-stop ${application.packageName} && am start -n ${application.packageName}/${clazz.name}")
    }
}


/**
 * 反射机制动态加载扩展---------------------------------------------------------------------------------
 */
open class eReflection internal constructor() {
    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eReflection() }
    }


    ////获取 加载类
    open fun eGetClass(className: String) = Class.forName(className)

    ////获取 实例化类
    open fun eGetClassInstance(cls: Class<Any>) = cls.newInstance()

    ////调用方法
    open fun eInvokeMethod(cls: Class<Any>, methodName: String) = {
        cls.getDeclaredMethod(methodName, String::class.java)
    }
}



