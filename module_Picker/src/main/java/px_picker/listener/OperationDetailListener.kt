package px_picker.listener

import px_picker.model.FuncDetailsMarker

/**
 * ## UI function details result marker
 *
 * Created by lxw
 */
interface OperationDetailListener {

    fun onReceiveDetails(operation: Operation, funcDetailsMarker: FuncDetailsMarker)
}
