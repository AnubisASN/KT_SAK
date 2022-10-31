package px_picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import com.anubis.module_picker.R;
import px_picker.rx.bus.ImagesObservable;
import px_picker.ui.picker.PickerActivity;
import px_picker.ui.picker.PreviewActivity;
import px_picker.util.DoubleUtils;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;
import px_core.PhoenixConfig;
import px_core.PhoenixOption;
import px_core.common.PhoenixConstant;
import px_core.listener.Starter;
import px_core.model.MediaEntity;
import px_picker.ui.camera.CameraActivity;

public final class Phoenix implements Starter {

    private static volatile PhoenixConfig config;

    public Phoenix() {

    }

    public static PhoenixConfig config() {
        if (config == null) {
            synchronized (Phoenix.class) {
                if (config == null) {
                    config = new PhoenixConfig();
                }
            }
        }
        return config;
    }

    public static PhoenixOption with() {
        return new PhoenixOption();
    }

    public static List<MediaEntity> result(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getParcelableArrayListExtra(PhoenixConstant.PHOENIX_RESULT);
    }

    @Override
    public void start(Fragment fragment, PhoenixOption option, int type, int requestCode) {
        startFromFragment(fragment, option, type, requestCode, null);
    }

    @Override
    public void start(Fragment fragment, PhoenixOption option, int type, String futureAction) {
        startFromFragment(fragment, option, type, 0, futureAction);
    }

    @Override
    public void start(Activity activity, PhoenixOption option, int type, int requestCode) {
        startFromActivity(activity, option, type, requestCode, null);
    }

    @Override
    public void start(Activity activity, PhoenixOption option, int type, String futureAction) {
        startFromActivity(activity, option, type, 0, futureAction);
    }

    private void startFromFragment(Fragment fragment, PhoenixOption option, int type, int requestCode, String futureAction) {
        if (!DoubleUtils.INSTANCE.isFastDoubleClick()) {
            switch (type) {
                //select a media file(contain capture function in the view).
                case PhoenixOption.TYPE_PICK_MEDIA: {
                    Intent intent = new Intent(fragment.getActivity(), PickerActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        fragment.startActivity(intent);
                    } else {
                        fragment.startActivityForResult(intent, requestCode);
                    }
                    fragment.getActivity().overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
                //capture a picture or a video.
                case PhoenixOption.TYPE_TAKE_PICTURE: {
                    Intent intent = new Intent(fragment.getActivity(), CameraActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        fragment.startActivity(intent);
                    } else {
                        fragment.startActivityForResult(intent, requestCode);
                    }
                    fragment.getActivity().overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
                //just browse the media files that imported by the bundle.
                case PhoenixOption.TYPE_BROWSER_PICTURE: {
                    ImagesObservable.Companion.getInstance().savePreviewMediaList(option.getPickedMediaList());
                    Intent intent = new Intent(fragment.getActivity(), PreviewActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);
                    intent.putExtra(PhoenixConstant.KEY_PREVIEW_TYPE, PhoenixConstant.TYPE_PREIVEW_FROM_PREVIEW);
                    intent.putParcelableArrayListExtra(PhoenixConstant.KEY_PICK_LIST, (ArrayList<? extends Parcelable>) option.getPickedMediaList());

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        fragment.startActivity(intent);
                    } else {
                        fragment.startActivityForResult(intent, requestCode);
                    }
                    fragment.getActivity().overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
            }
        }
    }

    private void startFromActivity(Activity activity, PhoenixOption option, int type, int requestCode, String futureAction) {
        if (!DoubleUtils.INSTANCE.isFastDoubleClick()) {
            switch (type) {
                case PhoenixOption.TYPE_PICK_MEDIA: {
                    Intent intent = new Intent(activity, PickerActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        activity.startActivity(intent);
                    } else {
                        activity.startActivityForResult(intent, requestCode);
                    }
                    activity.overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
                case PhoenixOption.TYPE_TAKE_PICTURE: {
                    Intent intent = new Intent(activity, CameraActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        activity.startActivity(intent);
                    } else {
                        activity.startActivityForResult(intent, requestCode);
                    }
                    activity.overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
                case PhoenixOption.TYPE_BROWSER_PICTURE: {
                    ImagesObservable.Companion.getInstance().savePreviewMediaList(option.getPickedMediaList());
                    Intent intent = new Intent(activity, PreviewActivity.class);
                    intent.putExtra(PhoenixConstant.PHOENIX_OPTION, option);
                    intent.putExtra(PhoenixConstant.KEY_PREVIEW_TYPE, PhoenixConstant.TYPE_PREIVEW_FROM_PREVIEW);
                    intent.putParcelableArrayListExtra(PhoenixConstant.KEY_PICK_LIST, (ArrayList<? extends Parcelable>) option.getPickedMediaList());

                    if (!TextUtils.isEmpty(futureAction)) {
                        intent.putExtra(BUNDLE_KEY_FUTURE_ACTION, futureAction);
                        activity.startActivity(intent);
                    } else {
                        activity.startActivityForResult(intent, requestCode);
                    }
                    activity.overridePendingTransition(R.anim.phoenix_activity_in, 0);
                    break;
                }
            }
        }
    }
}
