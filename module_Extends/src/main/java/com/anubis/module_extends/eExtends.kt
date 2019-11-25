package com.anubis.kt_extends

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.preference.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils.indexOf
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.anubis.kt_extends.eString.eInterception
import com.tencent.bugly.proguard.s
import com.tencent.bugly.proguard.t
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.custom.async
import org.json.JSONObject
import java.io.*
import java.lang.Error
import java.lang.Process
import java.net.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
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
 */


fun Context.eShowTip(str: Any, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str.toString(), i).show()
}

/**
 * Log i e扩展函数------------------------------------------------------------------------------------
 */

fun Activity?.eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "${this?.localClassName ?: "eLog"}-：$str\n ")
}

fun eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "eNLog:$str\n ")
}

fun Activity?.eLogE(str: Any, e: Exception? = null, TAG: String = "TAG") {
    e?.printStackTrace()
    Log.e(TAG, "${this?.localClassName ?: "eLogE"}-：$str\n$e ")
}


fun Activity?.eLogE(str: Any, e: Error, TAG: String = "TAG") {
    e?.printStackTrace()
    Log.e(TAG, "${this?.localClassName ?: "eLogE"}-：$str\n$e ")
}


fun eLogE(str: Any, e: Error? = null, TAG: String = "TAG") {
    e?.printStackTrace()
    Log.e(TAG, "eNLogE:$str\n${eErrorOut(e)}")
}

fun eLogE(str: Any, e: Exception? = null, TAG: String = "TAG") {
    e?.printStackTrace()
    Log.e(TAG, "eNLogE:$str\n${eErrorOut(e)}")
}


fun eErrorOut(e:Exception?):String?{
    e?:return null
    val os = ByteArrayOutputStream()
    e.printStackTrace(PrintStream(os))
    return os.toString()
}
fun eErrorOut(e:Error?):String?{
    e?:return null
    val os = ByteArrayOutputStream()
    e.printStackTrace(PrintStream(os))
    return os.toString()
}


fun Context.eLogCat(savePath: String = "/mnt/sdcard/Logs/", fileName: String = "${eTime.eGetCurrentTime("yyyy-MM-dd")}.log", parame: String = "TAG:I") = async {
    if (!File(savePath).exists()) {
        File(savePath).mkdirs()
    }
    eShell.eExecShell("logcat -v long AndroidRuntime:E *:S TAG:E $parame *E -d >> $savePath$fileName")
}


/**
 * SharedPreferences数据文件存储扩展-------------------------------------------------------------------
 */
//系统数据文件存储扩展
fun Context.eSetSystemSharedPreferences(key: Any, value: Any, sharedPreferences: SharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)): Boolean {
    val key = key.toString()
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    return editor.commit()
}

//系统数据文件存储读取扩展
fun Context.eGetSystemSharedPreferences(key: String, value: Any? = null, sharedPreferences: SharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)) =
        try {
            sharedPreferences.getString(key, value as String? ?: "")
        } catch (e: Exception) {
            try {
                sharedPreferences.getBoolean(key, value as Boolean? ?: true)
            } catch (e: Exception) {
                try {
                    sharedPreferences.getInt(key, value as Int? ?: 0)
                } catch (e: Exception) {
                    try {
                        sharedPreferences.getFloat(key, value as Float? ?: 0f)
                    } catch (e: Exception) {
                        try {
                            sharedPreferences.getLong(key, value as Long? ?: 0)
                        } catch (e: Exception) {
                            value
                        }
                    }
                }
            }
        }


//用户文件数据存储扩展
fun Context.eSetUserSharedPreferences(userID: String, key: String, value: Any, sharedPreferences: SharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)): Boolean {
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    return editor.commit()
}

//用户文件数据读取扩展
fun Context.eGetUserSharedPreferences(userID: String, key: String, value: Any? = null, sharedPreferences: SharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)) = try {
    sharedPreferences.getString(key, value as String? ?: "")
} catch (e: Exception) {
    try {
        sharedPreferences.getBoolean(key, value as Boolean? ?: true)
    } catch (e: Exception) {
        try {
            sharedPreferences.getInt(key, value as Int? ?: 0)
        } catch (e: Exception) {
            try {
                sharedPreferences.getFloat(key, value as Float? ?: 0f)
            } catch (e: Exception) {
                try {
                    sharedPreferences.getLong(key, value as Long? ?: 0)
                } catch (e: Exception) {
                    value
                }
            }
        }
    }
}


//首选项数据文件存储扩展
fun Context.eSetDefaultSharedPreferences(key: String, value: Any, sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)): Boolean {
    val editor = sharedPref.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    return editor.commit()
}

//首选项数据文件读取扩展
fun Context.eGetDefaultSharedPreferences(key: String, value: Any? = null, sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)) = try {
    sharedPref.getString(key, value as String? ?: "")
} catch (e: Exception) {
    try {
        sharedPref.getBoolean(key, value as Boolean? ?: true)
    } catch (e: Exception) {
        try {
            sharedPref.getInt(key, value as Int? ?: 0)
        } catch (e: Exception) {
            try {
                sharedPref.getFloat(key, value as Float? ?: 0f)
            } catch (e: Exception) {
                try {
                    sharedPref.getLong(key, value as Long? ?: 0)
                } catch (e: Exception) {
                    value
                }
            }
        }
    }
}


//Intent Get传递扩展
fun Intent.eGetMessage(Sign: String): String = getStringExtra(Sign)


fun Bundle.eSetMessage(Sign: String, Message: Any = "") = when (Message) {
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
var mp:MediaPlayer?=null
fun ePlayVoice(context: Context, music: Any, isLoop: Boolean = false) {
    try {
        mp?.stop()
        mp?.release()
        when (music) {
            is Int -> mp = MediaPlayer.create(context, music)//重新设置要播放的音频}
            is String -> {
                mp = MediaPlayer()
                mp?.setDataSource(music)
                mp?.prepareAsync()
            }
        }
        mp?.isLooping = isLoop
        mp?.start()//开始播放
    } catch (e: Exception) {
      eLogE("ePlayVoice 错误",e)
    }
}

//PCM播放
fun ePlayPCM(path: String) {
    val bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
    var audioTrack: AudioTrack? = AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)
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
        eLogE("playPCMRecord", e)
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

//assets文件复制
fun eAssetsToFile(context: Context, assetsName: String, copyName: String): Boolean {
    try {
        if (!File(copyName).exists()) {
            val inputStream = context.getResources().getAssets().open(assetsName)// assets文件夹下的文件
            val fileOutputStream = FileOutputStream(copyName)// 保存到本地的文件夹下的文件
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
        eLogE("文件复制错误",e)
        return false
    }
}


/**
 * String Json解析扩展--------------------------------------------------------------------------------
 */
object eJson {
    //Object Json解析扩展
    fun eGetJsonObject(json: String, resultKey: String) = JSONObject(json).optString(resultKey)

    //Array Json解析扩展
    fun eGetJsonArray(json: String, resultKey: String, i: Int) = JSONObject(json).optJSONArray(resultKey).getJSONObject(i).toString()

    fun eGetJsonArray(json: String, resultKey: String) = JSONObject(json).optJSONArray(resultKey)

}

/**
 * 广播接收器辅助扩展类--------------------------------------------------------------------------------------
 */
object eBReceiver {
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
 * App程序扩展类--------------------------------------------------------------------------------------
 */
object eApp {

    //获取所有安装的app
    fun eGetLocalApps(context: Context) = context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN, null), 0)


    //获取APP包名
    fun eGetLocalAppsPackageName(context: Context, appName: String): String? {
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
    fun eAppRestart(context: Application, activity: Activity) {
        //启动页
        val intent = Intent(context, activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    //Activity重启
    fun eActivityRestart(activity: Activity, activityList: ArrayList<Activity>? = null): Boolean {
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


    //    APP包名启动
    fun eAppStart(activity: Activity, packageName: String? = null): Boolean {
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
    fun eIsAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcessInfoList) {
            if (processInfo.processName == packageName && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    //Activity运行判断
    fun eActivityWhetherWorked(context: Context, className: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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
    fun eGetShowActivity(context: Context) = context.activityManager.getRunningTasks(1)[0].topActivity

    //判断Activity是否存在任务栈里面
    fun eIsExistMainActivity(context: Context, activity: Class<*>): Boolean {
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
    fun eIsServiceRunning(context: Context, className: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = am.getRunningServices(0x7FFFFFFF)
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (className == aInfo.service.className) return true
        }
        return false
    }

    //后台服务杀死
    fun eKillBackgroundProcesses(context: Context, packageName: String): Boolean {
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
    fun eGetPackNamePID(context: Context, packName: String): Int? {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mRunningProcess = manager.runningAppProcesses
        for (amProcess in mRunningProcess) {
            if (packName == amProcess.processName)
                return amProcess.pid
        }
        return null
    }

    //软件安装判断
    fun eIsAppInstall(packageName: String, mContext: Context): Boolean {
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


    //   首选Summary项动态改变
    //sSummaryDynamicSetting(findPreference("root_screen") as PreferenceScreen)
    //Summary修改
    fun Context.eSummaryModify(group: PreferenceGroup) {
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
object eRegex {
    //获取数字
    fun eGetNumber(str: String): Int {
        val regEx = "[^0-9]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return (m.replaceAll("").trim { it <= ' ' }).toInt()
    }

    //IP格式判断
    fun eIsIP(ip: String): Boolean {
        val regIP: String = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"
        return Pattern.compile(regIP).matcher(ip).matches()
    }

    //电话格式判断
    fun eIsPhoneNumber(number: String): Boolean {
        val regExp: String = "^((13[0-9])|(15[^[4,5],\\D])|(16[^6,\\D])|(18[0,1,5-9])|(17[6,7,8]))\\d{8}$"
        val p: Pattern = Pattern.compile(regExp)
        val m: Matcher = p?.matcher(number.toString())
        return m.find()
    }

    //邮编格式判断
    fun eIsZipNO(zipCode: String): Boolean {
        val str = "^[1-9][0-9]{5}$"
        return Pattern.compile(str).matcher(zipCode).matches()
    }

    //邮箱格式判断
    fun eIsEmail(email: String): Boolean {
        if (null == email || "" == email) return false
        val regEm = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"
        return Pattern.compile(regEm).matcher(email).matches()//复杂匹配
    }

    //身份证格式判断
    fun eIsIDCard(idCard: String): Boolean {
        val regID = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"
        return Pattern.compile(regID).matcher(idCard).matches()
    }

    //URL格式判断
    fun eIsUrl(url: String): Boolean {
        val regUrl = "[a-zA-z]+://[^\\s]*"
        return Pattern.compile(regUrl).matcher(url).matches()
    }

    //中文验证
    fun eIsZh(zh: String): Boolean {
        val reZh = "^[\\u4e00-\\u9fa5]+$"
        return Pattern.compile(reZh).matcher(zh).matches()
    }
}

/**
 * KeyDownExit事件监听扩展类--------------------------------------------------------------------------
 */
object eKeyEvent {
    private var Time: Long = 0
    fun eSetKeyDownExit(activity: Activity, keyCode: Int, activityList: ArrayList<Activity>? = null, systemExit: Boolean = true, hint: String = "再按一次退出", exitHint: String = "APP已退出", ClickTime: Long = 2000, isExecute: Boolean = true): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - Time > ClickTime) {
                activity.eShowTip(hint)
                Time = System.currentTimeMillis()
                false
            } else {
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
object eTime {
    /**
     * 时间差
     * @param endDate 上次更新时间
     * @param nowDate 当前时间
     * @return 天数
     */
    fun eGetTimeDifference(nowDate: Date, endDate: Date, type: String = "hour"): Long {
        val nd = (1000 * 24 * 60 * 60).toLong()
        val nh = (1000 * 60 * 60).toLong()
        val nm = (1000 * 60).toLong()
        val ns = 1000
        // 获得两个时间的毫秒时间差异
        val diff = nowDate.time - endDate.time
        return when (type) {
            "day" -> diff / nd
            "hour" -> diff % nd / nh
            "min" -> diff % nd % nh / nm
            "second" -> diff % nd % nh % nm / ns
            else -> diff % nd / nh
        }
    }

    //获取当前星期
    fun eGetCurrentWeek() = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
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
    fun eGetCurrentTime(format: String = "yyyy-MM-dd HH:mm:ss") = SimpleDateFormat(format).format(Date())

    //获取时间戳格式转时间
    fun eGetCuoFormatTime(dateCuo: Long, format: String = "yyyy-MM-dd HH:mm:ss") = SimpleDateFormat(format).format(Date(dateCuo))

    //时间转时间戳
    fun eGetCuoTime(date: String= eTime.eGetCurrentTime(), type: String = "s"): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).time.toString()
        return if (type == "s") date.substring(0, 10) else date
    }
}

/**
 * 网络扩展类-----------------------------------------------------------------------------------------
 */
object eNetWork {
    // 判断是否有网
    fun eIsNetworkAvailable(context: Context): Boolean {
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
    fun eIsNetworkOnline(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("ping -c 1 114.114.114.114")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }


    // 获取PING延迟
    fun eGetNetDelayTime(url:String): String {
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
object eDevice {
    // 获取屏幕密度，宽高
    fun eGetDensityWidthHeight(mContext: Context): Array<Any> {
        val resources = mContext.resources
        val dm = resources.displayMetrics
        val density = dm.density
        val width = dm.widthPixels
        val height = dm.heightPixels
        return arrayOf(density, width, height)
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    fun eGetDpToPx(context: Context, dpValue: Float) = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()

    //获取系统的名称
    fun eGetSysVersionName() = Build.MODEL

    //获取系统的SDK版本
    fun eGetSysSDKVersion() = Build.VERSION.SDK

    //获取系统的无线电固件版本
    fun eGetRadioVersion() = Build.RADIO

    //获得显示
    fun eGetDisPlay() = Build.DISPLAY


    //获取本地IP
    fun eGetHostIP(): String {
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
}

/**
 * 蓝牙扩展类-----------------------------------------------------------------------------------------
 */
object eBluetooth {
    // 打开蓝牙
    fun eOpenBluetooth(bel_name: String, context: Context) {
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
    fun eCloseBluetooth(context: Context) {
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
    fun eSetDiscoverableTimeout(timeout: Int = 0) {
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
 * Bitmap扩展类--------------------------------------------------------------------------------------
 */
object eBitmap {
    //Bitmap释放
    fun eGcBitmap(bitmap: Bitmap?) {
        var bitmap = bitmap
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle() // 回收图片所占的内存
            bitmap = null
            System.gc() // 提醒系统及时回收
        }
    }

    //Bitmap转Base64工具
    fun eBitmapToBase64(bitmap: Bitmap): String? {
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
    fun eBase64ToBitmap(base64String: String): Bitmap {
        val decode = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decode, 0, decode.size)
    }

    //NV21 Bytes字节转文件
    fun eByteArrayToBitmp(mImageNV21: ByteArray, width: Int, height: Int, rect: Rect = Rect(0, 0, width, height), rotate: Float = 0f, quality: Int = 80): Bitmap? {
        var mBitmap: Bitmap? = null
        try {
            val image = YuvImage(mImageNV21, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(rect, quality, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            mBitmap = eRotateBitmap(bmp, rotate)
            stream.close()
//            eGcBitmap(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mBitmap
    }

    //YUV Bytes字节转文件
    fun eByteArrayToFile(tmpFile: File, yuvBytes: ByteArray, w: Int, h: Int, imageformat: Int = ImageFormat.NV21, rotate: Int = 0, quality: Int = 80): Boolean {
        // 通过YuvImage得到Bitmap格式的byte[]
        try {
            if (!tmpFile.exists())
                tmpFile.createNewFile()
            val yuvImage = YuvImage(yuvBytes, imageformat, w, h, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), rotate, out)
            val dataBmp = out.toByteArray()
            // 生成Bitmap
            val bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())

            // 旋转
            val matrix = Matrix()
            matrix.setRotate(rotate.toFloat())
            val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            val fos = FileOutputStream(tmpFile)
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            com.anubis.kt_extends.eLogE("保存失败", e)
            return false
        }
    }

    //图片旋转
    fun eRotateBitmap(bitmap: Bitmap?, rotate: Float = 0f): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.setRotate(rotate)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        if (newBM == bitmap) {
            return newBM
        }
        bitmap.recycle()
        return newBM
    }

    //Bitmap转文件
    fun eBitmapToFile(bitmap: Bitmap, absPath: String, quality: Int = 80): Boolean {
        return eBitmapToFile(bitmap, File(absPath), quality)
    }

    //Bitmap转文件
    fun eBitmapToFile(bitmap: Bitmap?, file: File, quality: Int = 80): Boolean {
        if (bitmap == null) {
            return false
        } else {
            var fos: FileOutputStream? = null
            try {
                if (!file.exists())
                    file.createNewFile()
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
    fun eDrawableToBitmap(drawable: Drawable): Bitmap {
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

    fun eGetBitmap(path:String)=BitmapFactory.decodeFile(path)
}

/**
 * 文件转换扩展类--------------------------------------------------------------------------------------
 */
object eFile {

/**
 * 复制单个文件
 * @param oldPathName String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
 * @param newPathName String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
 * @return <code>true</code> if and only if the file was copied;
 *         <code>false</code> otherwise
 */

fun   eCopyFile(  oldPathName:String,newPathName:String):Boolean {
        try {
            val oldFile =   File(oldPathName)
            if (!oldFile.exists()) {
                eLog("eCopyFile: oldFile 不存在")
                return false
            }
            if (!oldFile.isFile) {
                eLog("eCopyFile: oldFile 不是文件")
                return false
            }
            if (!oldFile.canRead()) {
                eLog("eCopyFile: oldFile 无法读取")
                return false
            }
            val newFile=File(if (newPathName.contains("."))newPathName.substring(0,newPathName.lastIndexOf("/"))else newPathName)
            if (!newFile.exists()){
                newFile.mkdirs()
            }
            val fileInputStream =   FileInputStream(oldPathName)
            val fileOutputStream =   FileOutputStream(newPathName)
            val buffer = ByteArray(1024)
            var byteRead:Int
            while ( ( fileInputStream.read(buffer).apply { byteRead =this })!=-1) {
                fileOutputStream.write(buffer, 0, byteRead)
            }
            fileInputStream.close()
            fileOutputStream.flush()
            fileOutputStream.close()
            return true
        } catch (e:Exception ) {
            e.printStackTrace()
            return false
        }
    }



    //    文件转Base64
    fun eFileToBase64(path: String): String? {
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
            eLogE("转换错误", e)
            return null
        }
    }


    //    base64字符保存文本文件
    fun eBase64StrToFile(base64Code: String, targetPath: String) {
        val buffer = base64Code.toByteArray()
        val out = FileOutputStream(targetPath)
        out.write(buffer)
        out.close()
    }

    //Base64转文件
    fun eBase64ToFile(base64: String, file: File): File {
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
            eLogE("转换异常", ioe)
        }
        return file
    }

}

/**
 * 字符转换扩展类--------------------------------------------------------------------------------------
 */
object eString {
    //数据转换
    fun eGetFormatSize(size: Long) = when {
        size < 1 -> "0K"
        size in 1..1023 -> size.toString() + " K"
        1024 < size -> (size / 1024).toString() + " M"
        else -> "0 K"
    }


    //数值段获取
    fun eGetNumberPeriod(str: String, start: Any, end: Any): String {
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
    fun eIsChinese(c: Char) = c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5// 根据字节码判断

    //判断是否有英文
    fun eIsHasEglish(chars: CharArray): Boolean {
        for (i in chars.indices) {
            if (chars[i].toInt() >= 97 && chars[i].toInt() <= 122) {
                return true
            }
        }
        return false
    }

    //十六进制字符串转字节
    fun eGetHexStringToBytes(hexString: String): ByteArray? {
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
    fun eGetToHexString(byteArray: ByteArray?): String {
        if (byteArray == null || byteArray.isEmpty()) {
            eLog("eGetToHexString:传入参数为空")
            return ""
        }
        val hexString = StringBuilder()
        for (i in byteArray.indices) {
            if (byteArray[i] and 0xff.toByte() < 0x10)
            //0~F前面不零
                hexString.append("0")
            hexString.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
        }
        return hexString.toString().toLowerCase()
    }

    //字符转字节
    fun eGetCharToByte(c: Char) = indexOf("0123456789ABCDEF", c).toByte()

    //字节转字符串
    fun eGetBytesToHexString(src: ByteArray, lenth: Int): String? {
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

    //MD5加密
    fun eGetEncodeMD5(str: String, digits: Int = 32): String? {
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

    //字符串截取
    fun eInterception(str: String, lenght: Int = 1024, symbol: String = ","): String {
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
object ePermissions {
    //系统设置修改权限
    fun eSetSystemPermissions(context: Context): Boolean {
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
    fun eSetPermissions(activity: Activity, permissionsArray: Array<out String>, requestCode: Int = 1): Boolean {
        val permissionsList = ArrayList<String>()
        for (permission in permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
            }
        }
        return if (permissionsList.isEmpty()) {
            //未授予的权限为空，表示都授予了
            true
        } else {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(arrayOfNulls<String>(permissionsList.size)), 1)
            false
        }

    }

    //显示授权设置
    fun eSetOnRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray, isPermissionsOKHint: String = "", isPermissionsNoHint: String = "") {
        when (requestCode) {
            1 -> for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (isPermissionsOKHint.isEmpty()) {
                        activity.eShowTip(isPermissionsOKHint)
                    }
                } else {
                    if (isPermissionsNoHint.isEmpty()) {
                        activity.eShowTip(isPermissionsNoHint)
                    }
                }
            }
        }
    }
}


/**
 * linux命令扩展类------------------------------------------------------------------------------------
 */
object eShell {
    val remount = "mount -o remount,rw rootfs "
    val install = "pm install -r"
    val kill = "am force-stop"
    //判断是否有Root权限
    fun eHaveRoot(): Boolean {
        try {
            Runtime.getRuntime().exec("su").toString()
        } catch (e: Exception) {
            return false
        }
        return true
    }


    //执行命令并且输出结果
    fun eExecShell(shell: String): String {
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
    fun eExecShellSilent(shell: String): Int {
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
    fun eTExecShellOut(shell: String) {
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
}


/**
 * 反射机制动态加载扩展---------------------------------------------------------------------------------
 */
object eReflection {
    ////获取 加载类
    fun eGetClass(className: String) = Class.forName(className)

    ////获取 实例化类
    fun eGetClassInstance(cls: Class<Any>) = cls.newInstance()

    ////调用方法
    fun eInvokeMethod(cls: Class<Any>, methodName: String) = {
        cls.getDeclaredMethod(methodName, String::class.java)
    }
}



