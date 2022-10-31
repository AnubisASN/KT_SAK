package file_picker.config

import file_picker.bean.FileItemBeanImpl

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
@Deprecated(
    "Because the class name was confused.",
    ReplaceWith("AbstractFileDetector", "file_picker.config")
)
abstract class AbstractFileType {
    /**
     * 自定义文件类型识别方法，传入 @param itemBeanImpl 条目数据对象，
     * 由实现者来实现文件类型的甄别，返回填充了 fileType 的方法
     */
    abstract fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl
}