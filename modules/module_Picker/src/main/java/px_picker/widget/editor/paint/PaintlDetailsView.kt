package px_picture.edit.widget.paint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.anubis.module_picker.R
import px_picker.widget.editor.ColorSeekBar
import px_picker.listener.OnRevokeListener
import px_picker.listener.Operation

/**
 * ## UI elements of scrawl view
 *
 * Created by lxw
 */
class PaintlDetailsView(ctx: Context) : FrameLayout(ctx) {
    var onRevokeListener: OnRevokeListener? = null
    var onColorChangeListener: ColorSeekBar.OnColorChangeListener? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.item_edit_paint, this, true)
        findViewById<View>(R.id.ivRevoke).setOnClickListener {
            onRevokeListener?.revoke(Operation.PaintOperation)
        }
        val ckb = findViewById<View>(R.id.colorBarScrawl) as ColorSeekBar
        ckb.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                onColorChangeListener?.onColorChangeListener(colorBarPosition, alphaBarPosition, color)
            }
        })
    }
}
