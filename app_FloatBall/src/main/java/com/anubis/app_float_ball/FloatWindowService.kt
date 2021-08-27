package com.anubis.app_float_ball

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eBReceiver.Companion.eIBReceiver
import com.anubis.kt_extends.eFile.Companion.eIFile
import com.anubis.kt_extends.eJson.Companion.eIJson
import com.anubis.kt_extends.eTime.Companion.eITime
import com.anubis.module_dialog.eFloatWindow
import com.anubis.module_dialog.eForegroundService
import com.anubis.module_extends.eRvAdapter
import com.bumptech.glide.Glide
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.layout_float_window.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.onClick
import org.jetbrains.anko.runOnUiThread
import java.io.*
import java.net.*


/**
 * Created by zimo on 15/12/15.
 */
@RequiresApi(Build.VERSION_CODES.N)
class FloatWindowService : eForegroundService() {
    private val imgPath = "$eGetExternalStorageDirectory/exp/"
    private var mAdapter: eRvAdapter<resultDataItem>? = null
    companion object{
    }

    override fun onCreate() {
        super.onCreate()
        eIFile.eCheckFile(imgPath)
        eFloatWindow.eShowView(application, R.layout.layout_float_window, R.id.ivHome) { view: View, params: WindowManager.LayoutParams ->
            with(view) {
                ivHome.onClick {
                    if (cl.visibility == View.GONE) {
                        cl.visibility = View.VISIBLE
                        eFloatWindow.eSetFlaView(true)
                    } else {
                        OkGo.getInstance().cancelAll()
                        etInput.setText("")
                        mAdapter?.eSetData(null)
                        rv.visibility=View.GONE
                        cl.visibility = View.GONE
                        eFloatWindow.eSetFlaView(false)
                    }
                }
                mAdapter = eRvAdapter(application, rv, R.layout.item, null, { view: View, resultData: resultDataItem, i: Int ->
                    Glide.with(this).load("https://image.dbbqb.com/${resultData.path}").into(view.iv)
                }, layoutManagerBlock = {
                    GridLayoutManager(it, 3)
                }) { view: View, resultDataItem: resultDataItem, i: Int ->
                    OkGo.get<File>("https://image.dbbqb.com/${resultDataItem.path}")
                            .execute(object : FileCallback(imgPath, eITime.eGetCuoTime() + ".jpg") {
                                override fun onSuccess(response: Response<File>?) {
                                    response?.let {
                                        eIBReceiver.eSendPhotoBroad(context,it.body())
                                        eShowTip("下载完成")
                                        etInput.setText("")
                                        cl.visibility = View.GONE
                                        eFloatWindow.eSetFlaView(false)
                                    }
                                }

                                override fun onError(response: Response<File>?) {
                                    super.onError(response)
                                    eShowTip("网络异常，稍后再试")
                                }
                            })
                }
                ivSearch.onClick {
                    rv.visibility=View.VISIBLE
                    GlobalScope.launch {
                        val dataBean = eIJson.eGetJsonFrom(eGetUrl(etInput.text.toString()), resultData::class.java)
                        runOnUiThread {
                            mAdapter?.eSetData(dataBean)
                        }
                    }
                }
            }
        }
    }


    private fun eGetUrl(str: String): String {
        var strResult = ""
        try {
            val url = URL("https://www.dbbqb.com/api/search/json?start=0&w=${URLEncoder.encode(str, "UTF-8")}")
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.useCaches = false
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            val `in` = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
            val buffer = StringBuffer()
            var line: String? = ""
            while (`in`.readLine().also({ line = it }) != null) {
                buffer.append(line)
            }
            strResult = buffer.toString()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return strResult
    }

    override fun onDestroy() {
        super.onDestroy()
        eFloatWindow.eRemoveView()
    }

}
