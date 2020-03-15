/*
Copyright 2011-2013 Pieter Pareit
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.anubis.module_ftp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.WifiLock
import android.os.*
import android.util.Log
import com.anubis.kt_extends.*

import com.anubis.module_ftp.server.SessionThread
import com.anubis.module_ftp.server.TcpListener

import java.io.IOException
import java.lang.reflect.Method
import java.net.*
import java.util.ArrayList
import java.util.Collections
import java.util.Enumeration


class FsService : Service(), Runnable {
    protected var shouldExit = false
    protected var listenSocket: ServerSocket? = null
    private var wifiListener: TcpListener? = null
    private val sessionThreads = ArrayList<SessionThread>()

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiLock? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mInit=this@FsService
        shouldExit = false
        var attempts = 10
        // The previous server thread may still be cleaning up, wait for it to finish.
        while (serverThread != null) {
            Log.w(TAG, "Won't start, server thread exists")
            if (attempts > 0) {
                attempts--
                Util.sleepIgnoreInterupt(1000)
            } else {
                Log.w(TAG, "Server thread already exists")
                return Service.START_STICKY
            }
        }
        Log.d(TAG, "Creating server thread")
        serverThread = Thread(this)
        serverThread!!.start()
        return Service.START_STICKY
    }


    override fun onDestroy() {
        Log.i(TAG, "onDestroy() Stopping server")
        shouldExit = true
        if (serverThread == null) {
            Log.w(TAG, "Stopping with null serverThread")
            return
        }
        serverThread!!.interrupt()
        try {
            serverThread!!.join(10000) // wait 10 sec for server thread to finish
        } catch (e: InterruptedException) {
        }

        if (serverThread!!.isAlive) {
            Log.w(TAG, "Server thread failed to exit")
            // it may still exit eventually if we just leave the shouldExit flag set
        } else {
            Log.d(TAG, "serverThread join()ed ok")
            serverThread = null
        }
        try {
            if (listenSocket != null) {
                Log.i(TAG, "Closing listenSocket")
                listenSocket!!.close()
            }
        } catch (e: IOException) {
        }

        if (wifiLock != null) {
            Log.d(TAG, "onDestroy: Releasing wifi lock")
            wifiLock!!.release()
            wifiLock = null
        }
        if (wakeLock != null) {
            Log.d(TAG, "onDestroy: Releasing wake lock")
            wakeLock!!.release()
            wakeLock = null
        }
        Log.d(TAG, "FTPServerService.onDestroy() finished")
    }

    // This opens a listening socket on all interfaces.
    @Throws(IOException::class)
    internal fun setupListener() {
        listenSocket = ServerSocket()
        listenSocket!!.reuseAddress = true
        listenSocket!!.bind(InetSocketAddress(FsSettings.getPortNumber()))
    }

    override fun run() {
        Log.d(TAG, "Server thread running")
        eLog("网络是否在线：" + eNetWork.eIsNetworkOnline()+"-----"+getIP(this))
        if (getIP(this)==null) {
            Log.w(TAG, "run: There is no local network, bailing out")
            stopSelf()
            sendBroadcast(Intent(ACTION_FAILEDTOSTART))
            return
        }

        try {
            setupListener()
        } catch (e: IOException) {
            Looper.prepare()
            Handler().post { eDataFTP.mAPP!!.eShowTip(resources.getString(R.string.portOccupancy)) }
            Looper.loop()
            e.eLogE("无法打开端口--")
            stopSelf()
            sendBroadcast(Intent(ACTION_FAILEDTOSTART))
            return
        }

        // @TODO: when using ethernet, is it needed to take wifi lock?
        takeWifiLock()
        takeWakeLock()

        // A socket is open now, so the FTP server is started, notify rest of world
        Log.i(TAG, "Ftp Server up and running, broadcasting ACTION_STARTED")
        sendBroadcast(Intent(ACTION_STARTED))

        while (!shouldExit) {
            if (wifiListener != null) {
                if (!wifiListener!!.isAlive) {
                    Log.d(TAG, "Joining crashed wifiListener thread")
                    try {
                        wifiListener!!.join()
                    } catch (e: InterruptedException) {
                    }

                    wifiListener = null
                }
            }
            if (wifiListener == null) {
                // Either our wifi listener hasn't been created yet, or has crashed,
                // so spawn it
                wifiListener = TcpListener(listenSocket, this)
                wifiListener!!.start()
            }
            try {
                // TODO: think about using ServerSocket, and just closing
                // the main socket to send an exit signal
                Thread.sleep(WAKE_INTERVAL_MS.toLong())
            } catch (e: InterruptedException) {
                Log.d(TAG, "Thread interrupted")
            }

        }

        terminateAllSessions()

        if (wifiListener != null) {
            wifiListener!!.quit()
            wifiListener = null
        }
        shouldExit = false // we handled the exit flag, so reset it to acknowledge
        Log.d(TAG, "Exiting cleanly, returning from run()")

        stopSelf()
        sendBroadcast(Intent(ACTION_STOPPED))
    }

    private fun terminateAllSessions() {
        Log.i(TAG, "Terminating " + sessionThreads.size + " session thread(s)")
        synchronized(this) {
            for (sessionThread in sessionThreads) {
                if (sessionThread != null) {
                    sessionThread.closeDataSocket()
                    sessionThread.closeSocket()
                }
            }
        }
    }

    /**
     * Takes the wake lock
     *
     * Many devices seem to not properly honor a PARTIAL_WAKE_LOCK, which should prevent
     * CPU throttling. For these devices, we have a option to force the phone into a full
     * wake lock.
     */
    private fun takeWakeLock() {
        if (wakeLock == null) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (FsSettings.shouldTakeFullWakeLock()) {
                Log.d(TAG, "takeWakeLock: Taking full wake lock")
                wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG)
            } else {
                Log.d(TAG, "maybeTakeWakeLock: Taking parial wake lock")
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            }
            wakeLock!!.setReferenceCounted(false)
        }
        wakeLock!!.acquire()
    }

    private fun takeWifiLock() {
        Log.d(TAG, "takeWifiLock: Taking wifi lock")
        if (wifiLock == null) {
            val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiLock = manager.createWifiLock(TAG)
            wifiLock!!.setReferenceCounted(false)
        }
        wifiLock!!.acquire()
    }

    /**
     * The FTPServerService must know about all running session threads so they can be
     * terminated on exit. Called when a new session is created.
     */
    fun registerSessionThread(newSession: SessionThread) {
        // Before adding the new session thread, clean up any finished session
        // threads that are present in the list.

        // Since we're not allowed to modify the list while iterating over
        // it, we construct a list in toBeRemoved of threads to remove
        // later from the sessionThreads list.
        synchronized(this) {
            val toBeRemoved = ArrayList<SessionThread>()
            for (sessionThread in sessionThreads) {
                if (!sessionThread.isAlive) {
                    Log.d(TAG, "Cleaning up finished session...")
                    try {
                        sessionThread.join()
                        Log.d(TAG, "Thread joined")
                        toBeRemoved.add(sessionThread)
                        sessionThread.closeSocket() // make sure socket closed
                    } catch (e: InterruptedException) {
                        Log.d(TAG, "Interrupted while joining")
                        // We will try again in the next loop iteration
                    }

                }
            }
            for (removeThread in toBeRemoved) {
                sessionThreads.remove(removeThread)
            }

            // Cleanup is complete. Now actually add the new thread to the list.
            sessionThreads.add(newSession)
        }
        Log.d(TAG, "Registered session thread")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "user has removed my activity, we got killed! restarting...")
        val restartService = Intent(applicationContext, this.javaClass)
        restartService.setPackage(packageName)
        val restartServicePI = PendingIntent.getService(
                applicationContext, 1, restartService, PendingIntent.FLAG_ONE_SHOT)
        val alarmService = applicationContext
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 2000, restartServicePI)
    }

    companion object {
        private val TAG = FsService::class.java.simpleName
        private var mInit: FsService? = null
        val mFsService: FsService get() = mInit!!

        // Service will (global) broadcast when server start/stop
        val ACTION_STARTED = "be.ppareit.swiftp.FTPSERVER_STARTED"
        val ACTION_STOPPED = "be.ppareit.swiftp.FTPSERVER_STOPPED"
        val ACTION_FAILEDTOSTART = "be.ppareit.swiftp.FTPSERVER_FAILEDTOSTART"

        // RequestStartStopReceiver listens for these actions to start/stop this server
        val ACTION_START_FTPSERVER = "be.ppareit.swiftp.ACTION_START_FTPSERVER"
        val ACTION_STOP_FTPSERVER = "be.ppareit.swiftp.ACTION_STOP_FTPSERVER"

        protected var serverThread: Thread? = null

        // The server thread will check this often to look for incoming
        // connections. We are forced to use non-blocking accept() and polling
        // because we cannot wait forever in accept() if we want to be able
        // to receive an exit signal and cleanly exit.
        val WAKE_INTERVAL_MS = 1000 // milliseconds

        // return true if and only if a server Thread is running
        val isRunning: Boolean
            get() {
                if (serverThread == null) {
                    Log.d(TAG, "Server is not running (null serverThread)")
                    return false
                }
                if (!serverThread!!.isAlive) {
                    Log.d(TAG, "serverThread non-null but !isAlive()")
                } else {
                    Log.d(TAG, "Server is alive")
                }
                return true
            }

        /**
         * Gets the local ip address
         *
         * @return local ip adress or null if not found
         */
        // TODO: next if block could probably be removed
        // This next part should be able to get the local ip address, but in some case
        // I'm receiving the routable address
        // this is the condition that sometimes gives problems
        val localInetAddress: InetAddress?
            get() {
                if (isConnectedToLocalNetwork == false) {
                    Log.e(TAG, "getLocalInetAddress called and no connection")
                    return null
                }
                if (isConnectedUsingWifi == true) {
                    val context = eDataFTP.mAPP!!
                    val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val ipAddress = wm.connectionInfo.ipAddress
                    return if (ipAddress == 0) null else Util.intToInet(ipAddress)
                }
                try {
                    val netinterfaces = NetworkInterface
                            .getNetworkInterfaces()
                    while (netinterfaces.hasMoreElements()) {
                        val netinterface = netinterfaces.nextElement()
                        val adresses = netinterface.inetAddresses
                        while (adresses.hasMoreElements()) {
                            val address = adresses.nextElement()
                            if (address.isLoopbackAddress == false && address.isLinkLocalAddress == false)
                                return address
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return null
            }

        /**
         * Checks to see if we are connected to a local network, for instance wifi or ethernet
         *
         * @return true if connected to a local network
         */
        val isConnectedToLocalNetwork: Boolean
            get() {
                var connected = false
                val context = eDataFTP.mAPP!!
                val cm = context
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val ni = cm.activeNetworkInfo
                connected = (ni != null
                        && ni.isConnected == true
                        && ni.type and (ConnectivityManager.TYPE_WIFI or ConnectivityManager.TYPE_ETHERNET) != 0)
                if (connected == false) {
                    Log.d(TAG, "isConnectedToLocalNetwork: see if it is an WIFI AP")
                    val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    try {
                        val method = wm.javaClass.getDeclaredMethod("isWifiApEnabled")
                        connected = method.invoke(wm) as Boolean
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                if (connected == false) {
                    Log.d(TAG, "isConnectedToLocalNetwork: see if it is an USB AP")
                    try {
                        for (netInterface in Collections.list(NetworkInterface
                                .getNetworkInterfaces())) {
                            if (netInterface.displayName.startsWith("rndis") == true) {
                                connected = true
                            }
                        }
                    } catch (e: SocketException) {
                        e.printStackTrace()
                    }

                }
                return connected
            }

        /**
         * Checks to see if we are connected using wifi
         *
         * @return true if connected using wifi
         */
        val isConnectedUsingWifi: Boolean
            get() {
                val context = eDataFTP.mAPP!!
                val cm = context
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val ni = cm.activeNetworkInfo
                return (ni != null && ni.isConnected == true
                        && ni.type == ConnectivityManager.TYPE_WIFI)
            }

        /**
         * All messages server<->client are also send to this call
         *
         * @param incoming
         * @param s
         */
        fun writeMonitor(incoming: Boolean, s: String) {}
    }

    fun getIP(context: Context): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }

        return null
    }
}
