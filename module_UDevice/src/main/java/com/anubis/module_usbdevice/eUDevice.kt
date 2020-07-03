package com.anubis.module_usbdevice

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.fs.FileSystem
import com.github.mjdev.libaums.fs.UsbFile
import java.io.IOException
import java.util.*

/**
 * Author  ： AnubisASN   on 19-7-22 下午5:11.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 * Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 * Router :  /'Module'/'Function'
 * 说明： 安卓USB外置设备操作
 */

@SuppressLint("StaticFieldLeak")
open class eUDevice internal constructor() {
    private var mUsbManager: UsbManager

    companion object {
        internal lateinit var mHandler: Handler
        private lateinit var mContext: Context
        fun eInit(context: Context, handler: Handler): eUDevice {
            mHandler = Handler()
            handler.let { mHandler = handler }
            mContext = context
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eUDevice() }
    }

    init {
        mUsbManager = mContext.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    /**
     * 获取usb上所有的存储设备
     */
    open   val eGetUsbDevices = UsbMassStorageDevice.getMassStorageDevices(mContext)


    /**
     * 读取当前usb设备的数量
     */
    open  val eGetUsbDeviceCoun = eGetUsbDevices.size


    /**
     * 检查usb设备的权限
     */
    open  fun eCheckUsbPerssion(device: UsbMassStorageDevice) = mUsbManager.hasPermission(device.usbDevice)

    /**
     * 根据position获取usb设备
     */
    open  fun eGetUsbDevice(position: Int): UsbMassStorageDevice? {
        return if (position in 0 until eGetUsbDeviceCoun)
            eGetUsbDevices[position ]
        else null
    }

    /**
     * 读取设备文件
     *
     * @param device
     * @return
     */
    open  fun eReadUsbDevice(device: UsbMassStorageDevice,partitions:Int=0): FileSystem? {
        try {
            if (!eCheckUsbPerssion(device)) {  //检查是否有权限
                mContext.eShowTip("设备无权限")
                return null
            }
            device.init()//使用设备之前需要进行 初始化
            eLog("partitions:$partitions")
            val partition = device.partitions[partitions] //仅使用设备的第一个分区
            return partition.fileSystem
        } catch (e: Exception) {
            eLogE("eReadDevice", e)
            return null
        }

    }

    /**
     * 获取U盘的文件和文件夹
     *
     * @param fileSystem
     * @return
     */
    open  fun eGetUsbFiles(fileSystem: FileSystem): List<UsbFile> {
        val usbFiles = ArrayList<UsbFile>()
        try {
            for (file in fileSystem.rootDirectory.listFiles()) {  //将所以文件和文件夹路径添加到usbFiles数组中
                usbFiles.add(file)
            }
            usbFiles.sortWith(Comparator { oFile1, oFile2 ->
                //简单排序 文件夹在前 文件在后
                if (oFile1.isDirectory)
                    -1
                else
                    1
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return usbFiles
    }
}
