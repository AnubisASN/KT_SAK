package com.anubis.module_usbdevice

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShell
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
 * 说明：
 */

@SuppressLint("StaticFieldLeak")
object eUDevice {
    private var mContext: Context? = null
    private val storageDevices: Array<UsbMassStorageDevice>? = null
    private val usbFiles = ArrayList<UsbFile>()
    var uHandler: Handler? = null
    private var mUsbManager: UsbManager? = null
    fun init(context: Context,handler: Handler) {
        uHandler = Handler()
        uHandler=handler
        mContext = context
        mUsbManager = mContext!!.getSystemService(Context.USB_SERVICE) as UsbManager
    }


    /**
     * 读取当前usb设备的数量
     *
     * @return
     */
    //获取存储设备
    val deviceCount: Int
        get() {
            if (mContext == null) {
                eLog("mContext==null")
                return 0
            }
            val storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext!!)
            return storageDevices.size
        }

    /**
     * 获取usb上所有的存储设备
     *
     * @return
     */
    //获取存储设备
    val usbMassAllDevice: Array<UsbMassStorageDevice>
        get() = UsbMassStorageDevice.getMassStorageDevices(mContext)


    /**
     * 检查usb设备的权限
     *
     * @param device
     * @return
     */
    fun checkPerssion(device: UsbMassStorageDevice): Boolean {
        if (mUsbManager == null) {
            return false
        }
        return mUsbManager!!.hasPermission(device.usbDevice)
    }

    /**
     * 根据position获取usb设备
     *
     * @param position
     * @return
     */
    fun getUsbMassDevice(position: Int): UsbMassStorageDevice? {
        //获取存储设备
        val storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext!!)
        return if (position > storageDevices.size) {
            null
        } else {
            storageDevices[position]
        }
    }

    /**
     * 根据设备获取路径
     *
     * @param device
     * @return
     */
    fun readDevice(device: UsbMassStorageDevice): FileSystem? {
//        try {
        if (!checkPerssion(device)) {  //检查是否有权限
            mContext?.eShowTip("设备无权限")
            return null
        }

        device.init()//使用设备之前需要进行 初始化
        val partition = device.partitions[0] //仅使用设备的第一个分区
        val currentFs = partition.fileSystem
        // currentFs.getCapacity(); //容量大小
        // currentFs.getOccupiedSpace(); //已使用大小
        // currentFs.getFreeSpace();  //未使用的大小
        val root = currentFs.rootDirectory//获取根目录
        val deviceName = currentFs.volumeLabel//获取设备标签
        eLog("root$root---deviceName:$deviceName---size:${currentFs.capacity}")
        return currentFs
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        }

    }

    /**
     * 获取U盘的文件和文件夹路径
     *
     * @param fileSystem
     * @return
     */
    fun getUsbFiles(fileSystem: FileSystem): List<UsbFile> {
        usbFiles.clear()
        try {
            for (file in fileSystem.rootDirectory
                    .listFiles()) {  //将所以文件和文件夹路径添加到usbFiles数组中
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
