package com.anubis.module_Talkback

/**
 * Created by User on 2017/11/28.
 */
interface ITalkbackView {
    fun showMonitorPage(moduleTag: String?, info: String?)
    fun showCountDown(moduleTag: String?, count: Int)
    fun showCallOutPage(moduleTag: String?, info: String?)
    fun showRingPage(moduleTag: String?, info: String)
    fun showTalkPage(moduleTag: String?, info: String)
    fun showHandupPage(moduleTag: String?, info: String)
    fun showUnlockPage(moduleTag: String?, result: String)
    fun showSuspendPage(moduleTag: String?, info: String?)
    fun showCallTransferPage(moduleTag: String?, info: String?)
    fun showCallTurnPage(moduleTag: String?, info: String?)
}
