package file_picker.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import file_picker.bean.FileBean

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun getItem(position: Int): FileBean?
    abstract fun getItemView(position: Int): View?
}
