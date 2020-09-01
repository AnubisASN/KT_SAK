package px_picker.ui.camera.manager;

import android.content.Context;


import java.io.File;

import px_picker.ui.camera.config.CameraConfig;
import px_picker.ui.camera.config.CameraConfigProvider;
import px_picker.ui.camera.listener.OnCameraResultListener;
import px_picker.ui.camera.manager.listener.CameraCloseListener;
import px_picker.ui.camera.manager.listener.CameraOpenListener;
import px_picker.ui.camera.manager.listener.CameraPictureListener;
import px_picker.ui.camera.manager.listener.CameraVideoListener;
import px_picker.ui.camera.util.Size;

/**
 * The camera manager for manage camera resource
 * <p>
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(CameraConfigProvider cameraConfigProvider, Context context);

    void releaseCameraManager();

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void takePicture(File photoFile, CameraPictureListener cameraPictureListener, OnCameraResultListener callback);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    void stopVideoRecord(OnCameraResultListener callback);

    boolean isVideoRecording();

    void setCameraId(CameraId cameraId);

    void setFlashMode(@CameraConfig.FlashMode int flashMode);

    CameraId getCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    Size getPictureSizeForQuality(@CameraConfig.MediaQuality int mediaQuality);

    CharSequence[] getPictureQualityOptions();

    CharSequence[] getVideoQualityOptions();
}
