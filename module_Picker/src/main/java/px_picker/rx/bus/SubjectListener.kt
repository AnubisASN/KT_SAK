package px_picker.rx.bus


interface SubjectListener {

    fun add(observerListener: ObserverListener)
    fun remove(observerListener: ObserverListener)
}
