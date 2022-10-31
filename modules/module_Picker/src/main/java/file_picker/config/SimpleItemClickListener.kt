package file_picker.config

import android.view.View
import file_picker.adapter.FileListAdapter

open class SimpleItemClickListener : FileItemOnClickListener {
    override fun onItemClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {}

    override fun onItemChildClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {}

    override fun onItemLongClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {}
}
