package com.anubis.module_iva

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.*
import android.util.Base64
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
//import com.zhouwei.blurlibrary.EasyBlur
import java.util.ArrayList


/**
 * 作者 ： AnubisASN   on 2017/7/3 16:23.
 * 邮箱 ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  QQ :  773506352   ( 441666482 )
 */

/**
 * 扩展函数库
 */

/**
 * Toamst扩展函数-----------------------------------------------------------------------
 */
fun Context.eShowTip(str: CharSequence, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str, i).show()
}

fun Context.eShowTip(th: Context, str: CharSequence, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(th, str, i).show()
}

/**
 * Log.v扩展函数------------------------------------------------------------------------
 */

fun eLog(str: String, TAG: String = "TAG") {
    Log.i(TAG, "$str\n ")
}

/**
 * 系统数据文件存储扩展----------------------------------------------------------------------
 */
fun Context.eSetSystemSharedPreferences(key: Any, str: Any) {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    val key = key.toString()
    val editor = sharedPreferences.edit()
    if (str is Boolean) {
        editor.putBoolean(key, str)
        return
    }
    if (str is Float) {
        editor.putFloat(key, str)
        return
    }
    if (str is Int) {
        editor.putInt(key, str)
        return
    }
    if (str is Long) {
        editor.putLong(key, str)
        return
    }
    editor.putString(key, str.toString())
    editor.commit()
}

//系统数据文件读取扩展
fun Context.eGetSystemSharedPreferences(key: Any): String {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    return sharedPreferences.getString(key.toString(), "")

}

fun Context.eGetSystemSharedPreferences(key: String, value: String): String {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, value)

}

/**
 * 用户数据文件存储扩展----------------------------------------------------------------------
 */
fun Context.eSetUserPutSharedPreferences(userID: Int, key: String, str: Any) {
    val sharedPreferences = getSharedPreferences(userID.toString(), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, str.toString())
    editor.commit()
}

//数据文件读取扩展
fun Context.eGetUserSharedPreferences(userID: Int, key: String): String {
    val sharedPreferences = getSharedPreferences(userID.toString(), Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, "")

}

fun Context.eGetUserSharedPreferences(userID: Int, key: String, value: String): String {
    val sharedPreferences = getSharedPreferences(userID.toString(), Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, value)

}

//首选项数据文件读取扩展
fun Context.eGetDefaultSharedPreferences(key: Any): String {
    var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    return sharedPref.getString(key.toString(), "")
}


fun Context.eGetDefaultSharedPreferences(key: String, type: Any): Any {
    var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    when (type) {
        is String -> {
            return sharedPref.getString(key, "")
        }
        is Boolean -> {
            return sharedPref.getBoolean(key, true)
        }
        is Int -> {
            return sharedPref.getInt(key, 0)
        }
        else -> {
            return "数据类型错误！"
        }
    }
}


/**
 * String Json解析扩展--------------------------------------------------------------------
 */
fun eGetJsonObject(json: String, resultKey: String) = JSONObject(json).getString(resultKey)

//Array Json解析扩展
fun eGetJsonArray(json: String, resultKey: String, i: Int) = JSONObject(json).getJSONArray(resultKey).getJSONObject(i).toString()

fun eGetJsonArray(json: String, resultKey: String) = JSONObject(json).getJSONArray(resultKey)


/**
 * Intent Set传递扩展-------------------------------------------------------------------------
 */
//Intent Get传递扩展
fun Intent.eGetMessage(Sign: String): String = getStringExtra(Sign)


/**
 * Intent Set 捆绑传递扩展-------------------------------------------------------------------------
 */
fun Bundle.eSetMessage(Sign: String, Message: Any) = when (Message) {
    is String -> putString(Sign, Message)
    is Int -> putInt(Sign, Message)
    is Float -> putFloat(Sign, Message)
    is Boolean -> putBoolean(Sign, Message)
    is Char -> putChar(Sign, Message)
    else -> {
    }
}

/**
 * 高斯模糊轮子扩展---------------------------------------------------------------------------------
 */
//fun Context.eGetGaussianBlur(resource: Int, radius: Int = 5, scale: Int = 3): Bitmap {
//    return EasyBlur.with(this)
//            .bitmap(BitmapFactory.decodeResource(getResources(), resource)) //要模糊的图片
//            .radius(radius)//模糊半径
//            .scale(scale)//指定模糊前缩小的倍数
//            .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
//            .blur()
//}

/**
 * 本地应用遍历
 */
fun Context.eGetLocalApps(appName: String): String? {
    var PackageName: String? = null
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val apps = getPackageManager().queryIntentActivities(intent, 0)
    //for循环遍历ResolveInfo对象获取包名和类名
    for (i in 0..apps.size - 1) {
        val info = apps.get(i)
        val packageName = info.activityInfo.packageName
        val className = info.activityInfo.name
        val appsName = info.activityInfo.loadLabel(getPackageManager())
        if (appName == appsName) {
            PackageName = packageName
            return PackageName
        }
        PackageName = "NO"
    }
    return PackageName
}

/**
 * 数值段获取----------------------------------------------------------
 */
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
    return if (Str.length === 0) "" else Str.substring(Start, End).trim()
}

/**
 * 状态判断-----------------------------------------------------------------------------------
 */
//AppLiCation判断
fun Context.isAppRunningForeground(context: Context): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
    for (processInfo in runningAppProcessInfoList) {
        if (processInfo.processName == context.packageName && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true
        }
    }
    return false
}

//Activity判断
fun Context.eActivityWhetherWorked(className: String): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    if (list != null && list.size > 0) {
        val cpn = list[0].topActivity
        if (className == cpn.className) {
            return true
        }
    }
    return false
}


//服务判断
fun Context.eServiceWhetherWorked(className: String): Boolean {
    val myManager = this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningService = myManager.getRunningServices(30) as ArrayList<ActivityManager.RunningServiceInfo>
    for (i in runningService.indices) {
        if (runningService[i].service.className.toString() == className) {
            return true
        }
    }
    return false
}

/**
 * 正则表达式------------------------------------------------------------------------------------
 */
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
fun eIsPhoneNumber(number: Any): Boolean {
    val regExp: String = "^((13[0-9])|(15[^4,\\D])|(18[0,1,5-9])|(17[6，7,8]))\\d{8}$"
    val p: Pattern = Pattern.compile(regExp)
    val m: Matcher = p?.matcher(number.toString())
    return m.find()
}

//邮编格式判断
fun eIsZipNO(zipString: String): Boolean {
    val str = "^[1-9][0-9]{5}$"
    return Pattern.compile(str).matcher(zipString).matches()
}

//邮箱格式判断
fun eIsEmail(email: String?): Boolean {
    if (null == email || "" == email) return false
    val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")//复杂匹配
    val m = p.matcher(email)
    return m.matches()
}

/**
 * 首选Summary项动态改变--------------------------------------------------
 */
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


/**
 * 图片文件工具类-----------------------------------------------
 */
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
fun eBitmapToBase64(bitmap: Bitmap?): String? {

    // 要返回的字符串
    var reslut: String? = null

    var baos: ByteArrayOutputStream? = null

    try {

        if (bitmap != null) {

            baos = ByteArrayOutputStream()
            /**
             * 压缩只对保存有效果bitmap还是原来的大小
             */
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

//Summary动态刷新
//override fun onPreferenceChange(p: Preference, newValue: Any): Boolean {
//    if(p is EditTextPreference) p.summary =newValue.toString()
//    return true
//}
