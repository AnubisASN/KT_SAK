/*******************************************************************************
 * Copyright (c) 2012-2013 Pieter Pareit.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 *
 *
 * Contributors:
 * Pieter Pareit - initial API and implementation
 */

package com.anubis.module_ftp.GUI

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import android.preference.TwoStatePreference
import androidx.annotation.RequiresApi
import android.text.util.Linkify
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.anubis.kt_extends.eLog
import com.anubis.module_ftp.*

import java.io.File


/**
 * This is the main activity for swiftp, it enables the user to start the server service
 * and allows the users to change the settings.
 */
class eFTPUIs : PreferenceActivity(), OnSharedPreferenceChangeListener {
    private var mPassWordPref: EditTextPreference? = null
    private val mHandler = Handler()

    /**
     * This receiver will check FTPServer.ACTION* messages and will update the button,
     * running_state, if the server is running and will also display at what url the
     * server is running.
     */

    internal var mFsActionsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            eLog("action received: " + intent.action!!)
            // action will be ACTION_STARTED or ACTION_STOPPED
            updateRunningState()
            // or it might be ACTION_FAILEDTOSTART
            val runningPref = findPref<TwoStatePreference>("running_switch")
            if (intent.action == FsService.ACTION_FAILEDTOSTART) {
                runningPref.isChecked = false
                mHandler.postDelayed({ runningPref.setSummary(R.string.running_summary_failed) }, 100)
                mHandler.postDelayed({ runningPref.setSummary(R.string.running_summary_stopped) }, 5000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val xml = resources.getXml(R.xml.preferences)
//        var event = xml.eventType
//        while (event != XmlPullParser.END_DOCUMENT) {
//            when (event) {
//                XmlPullParser.START_TAG -> {
//                    if (xml.name == "EditTextPreference") {
//                        eLog("xml:${xml.getAttributeValue(0)}--${xml.getAttributeValue(1)}")
//                    }
//                }
//            }
//            event = xml.next()
//        }

        addPreferencesFromResource(R.xml.preferences)

        eDataFTP.mFTPUI = this@eFTPUIs

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val resources = resources

        val runningPref = findPref<TwoStatePreference>("running_switch")
        /*APP开启默认打开服务*/
        runningPref.isChecked = true
        val spe = sp.edit()
        if (sp.getBoolean("isExecute",true)) {
            spe.putString("username",eDataFTP.UserName)
            spe.putString("password",eDataFTP.PassWord)
            spe.putString("portNum",eDataFTP.Port.toString())
            spe.putBoolean("show_password",eDataFTP.ShowPassword)
            spe.putBoolean("stayAwake",eDataFTP.StayAwake)
            spe.putBoolean("allow_anonymous",eDataFTP.AllowAnonymous)
            spe.putBoolean("isExecute",false)
            spe.apply()
        }
        startServer()

        updateRunningState()
        runningPref.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                startServer()
            } else {
                stopServer()
            }
            true
        }


//        if (mAPP.isFreeVersion()) {
//        }
        updateLoginInfo()
        val usernamePref = findPref<EditTextPreference>("username")
        usernamePref.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val newUsername = newValue as String
            if (preference.summary == newUsername)
                return@OnPreferenceChangeListener false
            if (!newUsername.matches("[a-zA-Z0-9]+".toRegex())) {
                Toast.makeText(this@eFTPUIs,
                        R.string.username_validation_error, Toast.LENGTH_LONG).show()
                return@OnPreferenceChangeListener false
            }
            stopServer()
            true
        }

        mPassWordPref = findPref("password")
        mPassWordPref!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            stopServer()
            true
        }

        val portnum_pref = findPref<EditTextPreference>("portNum")
        portnum_pref.summary = sp.getString("portNum",
                eDataFTP.Port.toString())
        portnum_pref.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val newPortnumString = newValue as String
            if (preference.summary == newPortnumString)
                return@OnPreferenceChangeListener false
            var portnum = 0
            try {
                portnum = Integer.parseInt(newPortnumString)
            } catch (e: Exception) {
            }

            if (portnum <= 0 || 65535 < portnum) {
                Toast.makeText(this@eFTPUIs,
                        R.string.port_validation_error, Toast.LENGTH_LONG).show()
                return@OnPreferenceChangeListener false
            }
            preference.summary = newPortnumString
            stopServer()
            true
        }

        val chroot_pref = findPref<EditTextPreference>("chrootDir")
        // TODO:chrootDir应该由FsSetting给出，它应该测试。
        chroot_pref.summary = FsSettings.getChrootDir()!!.absolutePath
        chroot_pref.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val newChroot = newValue as String
            if (preference.summary == newChroot)
                return@OnPreferenceChangeListener false
            //现在测试新的chroot目录
            val chrootTest = File(newChroot)
            if (!chrootTest.isDirectory || !chrootTest.canRead())
                return@OnPreferenceChangeListener false
            preference.summary = newChroot
            stopServer()
            true
        }

        val wakelock_pref = findPref<CheckBoxPreference>("stayAwake")
        wakelock_pref.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            stopServer()
            true
        }

        val help = findPref<Preference>("help")
        help.onPreferenceClickListener = OnPreferenceClickListener {
            //                Log.v(TAG, "帮助点击");
            val context = this@eFTPUIs
            val ad = AlertDialog.Builder(context)
                    .setTitle(R.string.help_dlg_title)
                    .setMessage(R.string.help_dlg_message)
                    .setPositiveButton(R.string.ok, null).create()
            ad.show()
            Linkify.addLinks(ad.findViewById<View>(android.R.id.message) as TextView,
                    Linkify.ALL)

            true
        }

        val about = findPref<Preference>("about")
        about.onPreferenceClickListener = OnPreferenceClickListener {
            //                app.getInstance().onTerminate();

            this@eFTPUIs.finish()
//            val cls = moduleData.mBulkRegister
//            try {
//                val con = cls!!.getDeclaredConstructor(Context::class.java)
//                eLog("isisis:" + FsService.mFsService)
//                val obj = con.newInstance(moduleData.mFace)
//                val method = cls.getDeclaredMethod("initTest")
//                method.invoke(obj)
//            } catch (e: NoSuchMethodException) {
//                e.printStackTrace()
//            } catch (e: IllegalAccessException) {
//                e.printStackTrace()
//            } catch (e: InvocationTargetException) {
//                e.printStackTrace()
//            } catch (e: InstantiationException) {
//                e.printStackTrace()
//            }


            //                AlertDialog ad = new AlertDialog.Builder(eFTPUIs.this)
            //                        .setTitle(R.string.about_dlg_title)
            //                        .setMessage(R.string.about_dlg_message)
            //                        .setPositiveButton(getText(R.string.ok), null).create();
            //                ad.show();
            //                Linkify.addLinks((TextView) ad.findViewById(android.R.id.message),
            //                        Linkify.ALL);

            true
        }
    }


    override fun onResume() {
        super.onResume()

        updateRunningState()

        val sp = preferenceScreen.sharedPreferences
        sp.registerOnSharedPreferenceChangeListener(this)

        val filter = IntentFilter()
        filter.addAction(FsService.ACTION_STARTED)
        filter.addAction(FsService.ACTION_STOPPED)
        filter.addAction(FsService.ACTION_FAILEDTOSTART)
        registerReceiver(mFsActionsReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(mFsActionsReceiver)

        val sp = preferenceScreen.sharedPreferences
        sp.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        updateLoginInfo()
    }

    private fun startServer() {
        sendBroadcast(Intent(FsService.ACTION_START_FTPSERVER))
    }

    private fun stopServer() {
        sendBroadcast(Intent(FsService.ACTION_STOP_FTPSERVER))
    }

    private fun updateLoginInfo() {

        val username = FsSettings.getUserName()
        val password = FsSettings.getPassWord()
        val loginPreference = findPref<PreferenceScreen>("login")
        loginPreference.summary = username + " : " + transformPassword(password)
        (loginPreference.rootAdapter as BaseAdapter).notifyDataSetChanged()

        val usernamePref = findPref<EditTextPreference>("username")
        usernamePref.summary = username

        val passWordPref = findPref<EditTextPreference>("password")
        passWordPref.summary = transformPassword(password)
    }

    private fun updateRunningState() {
        val res = resources
        val runningPref = findPref<TwoStatePreference>("running_switch")
        if (FsService.isRunning == true) {
            runningPref.isChecked = true
            // Fill in the FTP server address
            val address = FsService.localInetAddress
            if (address == null) {
                runningPref.setSummary(R.string.cant_get_url)
                return
            }
            val iptext = ("\n" + "ftp://" + address.hostAddress + ":"
                    + FsSettings.getPortNumber() + "/")
            val summary = res.getString(R.string.running_summary_started, iptext)
            eDataFTP.hint = summary
            runningPref.summary = summary
        } else {
            runningPref.isChecked = false
            runningPref.setSummary(R.string.running_summary_stopped)
        }
    }

    fun <T : Preference> findPref(key: CharSequence): T {
        return this.findPreference(key) as T
    }


    private fun transformPassword(password: String): String {
        val context = eDataFTP.mAPP
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context!!.resources
        var showPassword = eDataFTP.ShowPassword.toString() == "true"
        showPassword = sp.getBoolean("show_password", showPassword)
        if (showPassword == true)
            return password
        else {
            val sb = StringBuilder(password.length)
            for (i in 0 until password.length)
                sb.append('*')
            return sb.toString()
        }
    }

}
