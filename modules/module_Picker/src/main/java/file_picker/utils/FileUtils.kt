package file_picker.utils

import android.content.Context
import android.os.Environment
import com.anubis.module_picker.R
import file_picker.bean.BeanSubscriber
import file_picker.bean.FileItemBeanImpl
import file_picker.bean.FileNavBeanImpl
import file_picker.config.FilePickerConfig.Companion.STORAGE_CUSTOM_ROOT_PATH
import file_picker.config.FilePickerConfig.Companion.STORAGE_EXTERNAL_STORAGE
import file_picker.config.FilePickerManager
import java.io.File

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
class FileUtils {

    companion object {

        /**
         * 根据配置参数获取根目录文件
         * @return File
         */
        fun getRootFile(): File {
            return when (FilePickerManager.config.mediaStorageType) {
                STORAGE_EXTERNAL_STORAGE -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
                STORAGE_CUSTOM_ROOT_PATH -> {
                    if (FilePickerManager.config.customRootPath.isEmpty()) {
                        File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                    } else {
                        File(FilePickerManager.config.customRootPath)
                    }
                }
                else -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
            }
        }

        /**
         * 获取给定文件对象[rootFile]下的所有文件，生成列表项对象
         */
        fun produceListDataSource(
            rootFile: File,
            beanSubscriber: BeanSubscriber
        ): ArrayList<FileItemBeanImpl> {
            val listData: ArrayList<FileItemBeanImpl> = ArrayList()
            for (file in rootFile.listFiles()) {
                //以符号 . 开头的视为隐藏文件或隐藏文件夹，后面进行过滤
                val isHiddenFile = file.name.startsWith(".")
                if (!FilePickerManager.config.isShowHiddenFiles && isHiddenFile) {
                    // skip hidden files
                    continue
                }
                if (file.isDirectory) {
                    listData.add(
                        FileItemBeanImpl(
                            file.name,
                            file.path,
                            false,
                            null,
                            true,
                            isHiddenFile,
                            beanSubscriber
                        )
                    )
                    continue
                }
                val itemBean = FileItemBeanImpl(
                    file.name,
                    file.path,
                    false,
                    null,
                    false,
                    isHiddenFile,
                    beanSubscriber
                )
                // 如果调用者没有实现文件类型甄别器，则使用的默认甄别器
                FilePickerManager.config.customDetector?.fillFileType(itemBean)
                    ?: FilePickerManager.config.defaultFileDetector.fillFileType(itemBean)
                listData.add(itemBean)
            }
            // 默认字典排序
            // Default sort by alphabet
            listData.sortWith(compareBy({ !it.isDir }, { it.fileName.toUpperCase() }))
            // 将当前列表数据暴露，以供调用者自己处理数据
            // expose data list  to outside caller
            return FilePickerManager.config.selfFilter?.doFilter(listData) ?: listData
        }

        /**
         * 为导航栏添加数据，也就是每进入一个文件夹，导航栏的列表就添加一个对象
         * 如果是退回到上层文件夹，则删除后续子目录元素
         */
        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBeanImpl>,
            nextPath: String,
            context: Context
        ): ArrayList<FileNavBeanImpl> {

            if (currentDataSource.isEmpty()) {
                // 优先级：目标设备名称 --> 自定义路径 --> 默认 SD 卡
                currentDataSource.add(
                    FileNavBeanImpl(
                        if (!FilePickerManager.config.mediaStorageName.isNullOrEmpty()) {
                            FilePickerManager.config.mediaStorageName
                        } else if (!FilePickerManager.config.customRootPath.isEmpty()) {
                            FilePickerManager.config.customRootPath
                        } else {
                            context.getString(R.string.file_picker_tv_sd_card)
                        },
                        nextPath
                    )
                )
                return currentDataSource
            }

            for (data in currentDataSource) {
                // 如果是回到根目录
                if (nextPath == currentDataSource.first().dirPath) {
                    return ArrayList(currentDataSource.subList(0, 1))
                }
                // 如果是回到当前目录（不包含根目录情况）
                // 直接返回
                val isCurrent = nextPath == currentDataSource[currentDataSource.size - 1].dirPath
                if (isCurrent) {
                    return currentDataSource
                }

                // 如果是回到上层的某一目录(即，当前列表中有该路径)
                // 将列表截取至目标路径元素
                val isBackToAbove = nextPath == data.dirPath
                if (isBackToAbove) {
                    return ArrayList(
                        currentDataSource.subList(
                            0,
                            currentDataSource.indexOf(data) + 1
                        )
                    )
                }
            }
            // 循环到此，意味着将是进入子目录
            currentDataSource.add(
                FileNavBeanImpl(
                    nextPath.substring(nextPath.lastIndexOf("/") + 1),
                    nextPath
                )
            )
            return currentDataSource
        }
    }
}
