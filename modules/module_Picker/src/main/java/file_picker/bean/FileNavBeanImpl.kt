package file_picker.bean

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavBeanImpl(val dirName: String, val dirPath: String) : FileBean {
    override var fileName: String
        get() = dirName
        set(value) {}
    override var filePath: String
        get() = dirPath
        set(value) {}

    override var beanSubscriber: BeanSubscriber
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}