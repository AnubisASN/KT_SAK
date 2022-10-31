package px_picker.ui.camera.config.model;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class MediaAction {

    public final static int ACTION_PHOTO = 0;
    public final static int ACTION_VIDEO = 1;

    @IntDef({ACTION_PHOTO, ACTION_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaActionState {
    }
}
