package px_picker.rx.bus

import px_core.model.MediaEntity
import px_picker.model.MediaFolder

interface ObserverListener {
    fun observerUpFoldersData(folders: List<MediaFolder>)

    fun observerUpSelectsData(selectMediaEntities: List<MediaEntity>)
}
