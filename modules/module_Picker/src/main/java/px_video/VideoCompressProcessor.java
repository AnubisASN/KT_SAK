package px_video;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import px_core.PhoenixOption;
import px_core.listener.OnProcessorListener;
import px_core.listener.Processor;
import px_core.model.MediaEntity;
import px_video.format.MediaFormatStrategyPresets;
import java.io.File;
import java.io.IOException;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2017/8/4 下午1:44
 */
public class VideoCompressProcessor implements Processor {

    private static final String TAG = "VideoCompressProcessor";

    @Override
    public MediaEntity syncProcess(Context context, MediaEntity mediaEntity, PhoenixOption phoenixOption) {

        if (mediaEntity == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        final MediaEntity result = mediaEntity;

        final File compressFile;
        try {
            File compressCachePath = new File(context.getCacheDir(), "outputs");
            compressCachePath.mkdir();
            compressFile = File.createTempFile("compress", ".mp4", compressCachePath);
        } catch (IOException e) {
            Toast.makeText(context, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return null;
        }

        try {
           String compressPath =  VideoCompressor.with().syncTranscodeVideo(mediaEntity.getLocalPath(), compressFile.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid480pFormatStrategy());
            result.setCompressed(true);
            result.setCompressPath(compressPath);
            return mediaEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void asyncProcess(Context context, final MediaEntity mediaEntity, PhoenixOption phoenixOption, final OnProcessorListener onProcessorListener) {

        if (mediaEntity == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        if (onProcessorListener == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        final MediaEntity result = mediaEntity;

        final File compressFile;
        try {
            File compressCachePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "phoenix");
            compressCachePath.mkdir();
            compressFile = File.createTempFile("compress", ".mp4", compressCachePath);
        } catch (IOException e) {
            Toast.makeText(context, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return;
        }
        VideoCompressor.Listener listener = new VideoCompressor.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                onProcessorListener.onProgress((int) progress);
            }

            @Override
            public void onTranscodeCompleted() {
                result.setCompressed(true);
                result.setCompressPath(compressFile.getAbsolutePath());
                onProcessorListener.onSuccess(result);
            }

            @Override
            public void onTranscodeCanceled() {

            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                onProcessorListener.onFailed(exception.getMessage());
            }
        };
        try {
            VideoCompressor.with().asyncTranscodeVideo(mediaEntity.getLocalPath(), compressFile.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid480pFormatStrategy(), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
