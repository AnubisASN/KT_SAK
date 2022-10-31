package px_picker.ui.camera.lifecycle.listener;

import android.view.View;

import androidx.annotation.Nullable;
import px_picker.ui.camera.config.CameraConfig;
import px_picker.ui.camera.listener.OnCameraResultListener;
import px_picker.ui.camera.util.Size;

/**
 * The camera view
 * <p>
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 */
public interface CameraView {

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@CameraConfig.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPictureTaken(byte[] bytes, @Nullable OnCameraResultListener callback);

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop(@Nullable OnCameraResultListener callback);

    void releaseCameraPreview();
}
