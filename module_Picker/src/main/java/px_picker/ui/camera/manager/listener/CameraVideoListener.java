package px_picker.ui.camera.manager.listener;

import px_picker.ui.camera.listener.OnCameraResultListener;

import java.io.File;

import px_picker.ui.camera.util.Size;

public interface CameraVideoListener {

    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile, OnCameraResultListener callback);

    void onVideoRecordError();
}
