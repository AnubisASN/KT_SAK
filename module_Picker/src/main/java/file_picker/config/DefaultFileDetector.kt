package file_picker.config

import file_picker.bean.FileItemBeanImpl
import file_picker.filetype.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class DefaultFileDetector : AbstractFileDetector() {

    private val allDefaultFileType: ArrayList<FileType> by lazy {
        val fileTypes = ArrayList<FileType>()
        fileTypes.add(AudioFileType())
        fileTypes.add(RasterImageFileType())
        fileTypes.add(CompressedFileType())
        fileTypes.add(DataBaseFileType())
        fileTypes.add(ExecutableFileType())
        fileTypes.add(FontFileType())
        fileTypes.add(PageLayoutFileType())
        fileTypes.add(TextFileType())
        fileTypes.add(VideoFileType())
        fileTypes.add(WebFileType())
        fileTypes
    }

    override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
        for (type in allDefaultFileType) {
            if (type.verify(itemBeanImpl.fileName)) {
                itemBeanImpl.fileType = type
                break
            }
        }
        return itemBeanImpl
    }
}