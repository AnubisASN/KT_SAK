package px_core.listener;


import java.util.List;

import px_core.model.MediaEntity;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2017/7/26 下午5:52
 */
public interface OnPickerListener {

    void onPickSuccess(List<MediaEntity> pickList);

    void onPickFailed(String errorMessage);
}
