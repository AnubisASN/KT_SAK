package px_core.util;


import px_core.listener.Processor;
import px_core.listener.Starter;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2017/8/3 下午4:08
 */
public class ReflectUtils {

    /**
     * 图片压缩
     */
    public static final String PictureCompressProcessor = "compress.picture.PictureCompressProcessor";

    /**
     * 图片编辑
     */
    public static final String PictureEditProcessor = "picture.edit.PictureEditProcessor";

    /**
     * 视频压缩
     */
    public static final String VideoCompressProcessor = "compress.video.VideoCompressProcessor";

    /**
     * 图片/视频选择
     */
    public static final String Phoenix = "px_picker.Phoenix";

    public static Processor loadProcessor(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (Processor) clazz.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Starter loadStarter(String className){
        try {
            Class<?> clazz = Class.forName(className);
            return (Starter) clazz.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
