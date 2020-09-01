package px_picker.ui.camera.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.anubis.module_picker.R;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import px_picker.ui.camera.util.DeviceUtils;

public class CameraSwitchView extends AppCompatImageButton {

    private Drawable frontCameraDrawable;
    private Drawable rearCameraDrawable;
    private int padding = 5;

    public CameraSwitchView(Context context) {
        this(context, null);
    }

    public CameraSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public CameraSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    @SuppressLint("ResourceType")
    private void initializeView() {
        Context context = getContext();
        frontCameraDrawable = ContextCompat.getDrawable(context, R.drawable.phoenix_camera_alt_white);
        frontCameraDrawable = DrawableCompat.wrap(frontCameraDrawable);
        DrawableCompat.setTintList(frontCameraDrawable.mutate(), ContextCompat.getColorStateList(context, R.drawable.phoenix_selector_switch_camera_mode));

        rearCameraDrawable = ContextCompat.getDrawable(context, R.drawable.phoenix_camera_alt_white);
        rearCameraDrawable = DrawableCompat.wrap(rearCameraDrawable);
        DrawableCompat.setTintList(rearCameraDrawable.mutate(), ContextCompat.getColorStateList(context, R.drawable.phoenix_selector_switch_camera_mode));

        setBackgroundResource(android.R.color.transparent);
        displayBackCamera();

        padding = DeviceUtils.convertDipToPixels(context, padding);
        setPadding(padding, padding, padding, padding);

        displayBackCamera();
    }

    public void displayFrontCamera() {
        setImageDrawable(frontCameraDrawable);
    }

    public void displayBackCamera() {
        setImageDrawable(rearCameraDrawable);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (Build.VERSION.SDK_INT > 10) {
            if (enabled) {
                setAlpha(1f);
            } else {
                setAlpha(0.5f);
            }
        }
    }
}
