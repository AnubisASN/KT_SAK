/*******************************************************************************
 * Copyright (c) 2012-2013 Pieter Pareit.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Contributors:
 * Pieter Pareit - initial API and implementation
 ******************************************************************************/

package com.anubis.module_ftp.GUI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anubis.module_ftp.FsApp;
import com.anubis.module_ftp.FsService;
import com.anubis.module_ftp.FsSettings;
import com.anubis.module_ftp.R;

import java.io.File;
import java.net.InetAddress;
import java.util.List;


/**
 * This is the main activity for swiftp, it enables the user to start the server service
 * and allows the users to change the settings.
 */
public class eFTPUI extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    private static String TAG = eFTPUI.class.getSimpleName();
    private static String lcb = "lcb";

    private EditTextPreference mPassWordPref;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "created");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Resources resources = getResources();

        TwoStatePreference runningPref = findPref("running_switch");
        /*APP开启默认打开服务*/
        runningPref.setChecked(true);
        startServer();

        updateRunningState();
        runningPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    startServer();
                } else {
                    stopServer();
                }
                return true;
            }
        });

        PreferenceScreen prefScreen = findPref("preference_screen");
        Preference marketVersionPref = findPref("market_version");
        marketVersionPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                在我们的应用程序开始市场
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=be.ppareit.swiftp"));
                try {
                    //如果没有安装市场，这可能会失败
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(lcb, TAG + " 106 未能推出市场。 \n" + e.toString());
                    e.printStackTrace();
                }
                return false;
            }
        });
        if (FsApp.Companion.getInit().isFreeVersion() == false) {
            prefScreen.removePreference(marketVersionPref);
        }
        updateLoginInfo();
        EditTextPreference usernamePref = findPref("username");
        usernamePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newUsername = (String) newValue;
                if (preference.getSummary().equals(newUsername))
                    return false;
                if (!newUsername.matches("[a-zA-Z0-9]+")) {
                    Toast.makeText(eFTPUI.this,
                            R.string.username_validation_error, Toast.LENGTH_LONG).show();
                    return false;
                }
                stopServer();
                return true;
            }
        });

        mPassWordPref = findPref("password");
        mPassWordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                stopServer();
                return true;
            }
        });

        EditTextPreference portnum_pref = findPref("portNum");
        portnum_pref.setSummary(sp.getString("portNum",
                resources.getString(R.string.portnumber_default)));
        portnum_pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newPortnumString = (String) newValue;
                if (preference.getSummary().equals(newPortnumString))
                    return false;
                int portnum = 0;
                try {
                    portnum = Integer.parseInt(newPortnumString);
                } catch (Exception e) {
                }
                if (portnum <= 0 || 65535 < portnum) {
                    Toast.makeText(eFTPUI.this,
                            R.string.port_validation_error, Toast.LENGTH_LONG).show();
                    return false;
                }
                preference.setSummary(newPortnumString);
                stopServer();
                return true;
            }
        });

        EditTextPreference chroot_pref = findPref("chrootDir");
        // TODO:chrootDir应该由FsSetting给出，它应该测试。
        chroot_pref.setSummary(FsSettings.getChrootDir().getAbsolutePath());
        chroot_pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newChroot = (String) newValue;
                if (preference.getSummary().equals(newChroot))
                    return false;
                //现在测试新的chroot目录
                File chrootTest = new File(newChroot);
                if (!chrootTest.isDirectory() || !chrootTest.canRead())
                    return false;
                preference.setSummary(newChroot);
                stopServer();
                return true;
            }
        });

        final CheckBoxPreference wakelock_pref = findPref("stayAwake");
        wakelock_pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                stopServer();
                return true;
            }
        });

        Preference help = findPref("help");
        help.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Log.v(TAG, "帮助点击");
                Context context = eFTPUI.this;
                AlertDialog ad = new AlertDialog.Builder(context)
                        .setTitle(R.string.help_dlg_title)
                        .setMessage(R.string.help_dlg_message)
                        .setPositiveButton(R.string.ok, null).create();
                ad.show();
                Linkify.addLinks((TextView) ad.findViewById(android.R.id.message),
                        Linkify.ALL);

                return true;
            }
        });

        Preference about = findPref("about");
        about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                FsApp.getInstance().onTerminate();

                startAPP("com.smdt.androidapi", eFTPUI.this);

//                AlertDialog ad = new AlertDialog.Builder(eFTPUI.this)
//                        .setTitle(R.string.about_dlg_title)
//                        .setMessage(R.string.about_dlg_message)
//                        .setPositiveButton(getText(R.string.ok), null).create();
//                ad.show();
//                Linkify.addLinks((TextView) ad.findViewById(android.R.id.message),
//                        Linkify.ALL);

                return true;
            }
        });

    }

    /*通过包名直接打开APP*/
    public void startAPP(String packagename, Context context) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            Toast.makeText(eFTPUI.this, "此程序未安装", Toast.LENGTH_LONG);
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            startAPP(packageName, className, context);
        }
    }

    /*通过包名和类名打开APP*/
    public void startAPP(String packageName, String className, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 设置ComponentName参数1:packagename参数2:MainActivity路径
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        context.startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateRunningState();

        Log.d(TAG, "onResume: Register the preference change listner");
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);

        Log.d(TAG, "onResume: Registering the FTP server actions");
        IntentFilter filter = new IntentFilter();
        filter.addAction(FsService.ACTION_STARTED);
        filter.addAction(FsService.ACTION_STOPPED);
        filter.addAction(FsService.ACTION_FAILEDTOSTART);
        registerReceiver(mFsActionsReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause: Unregistering the FTPServer actions");
        unregisterReceiver(mFsActionsReceiver);

        Log.d(TAG, "onPause: Unregistering the preference change listner");
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        updateLoginInfo();
    }

    private void startServer() {
        sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
    }

    private void stopServer() {
        sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
    }

    private void updateLoginInfo() {

        String username = FsSettings.getUserName();
        String password = FsSettings.getPassWord();

        Log.v(TAG, "Updating login summary");
        PreferenceScreen loginPreference = findPref("login");
        loginPreference.setSummary(username + " : " + transformPassword(password));
        ((BaseAdapter) loginPreference.getRootAdapter()).notifyDataSetChanged();

        EditTextPreference usernamePref = findPref("username");
        usernamePref.setSummary(username);

        EditTextPreference passWordPref = findPref("password");
        passWordPref.setSummary(transformPassword(password));
    }

    private void updateRunningState() {
        Resources res = getResources();
        TwoStatePreference runningPref = findPref("running_switch");
        if (FsService.isRunning() == true) {
            runningPref.setChecked(true);
            // Fill in the FTP server address
            InetAddress address = FsService.getLocalInetAddress();
            if (address == null) {
                Log.v(TAG, "Unable to retreive wifi ip address");
                runningPref.setSummary(R.string.cant_get_url);
                return;
            }
            String iptext = "\n" + "ftp://" + address.getHostAddress() + ":"
                    + FsSettings.getPortNumber() + "/";
            String summary = res.getString(R.string.running_summary_started, iptext);
            runningPref.setSummary(summary);
        } else {
            runningPref.setChecked(false);
            runningPref.setSummary(R.string.running_summary_stopped);
        }
    }

    /**
     * This receiver will check FTPServer.ACTION* messages and will update the button,
     * running_state, if the server is running and will also display at what url the
     * server is running.
     */
    BroadcastReceiver mFsActionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "action received: " + intent.getAction());
            // action will be ACTION_STARTED or ACTION_STOPPED
            updateRunningState();
            // or it might be ACTION_FAILEDTOSTART
            final TwoStatePreference runningPref = findPref("running_switch");
            if (intent.getAction().equals(FsService.ACTION_FAILEDTOSTART)) {
                runningPref.setChecked(false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runningPref.setSummary(R.string.running_summary_failed);
                    }
                }, 100);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runningPref.setSummary(R.string.running_summary_stopped);
                    }
                }, 5000);
            }
        }
    };

    static private String transformPassword(String password) {
        Context context = FsApp.Companion.getInit().get();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        String showPasswordString = res.getString(R.string.show_password_default);
        boolean showPassword = showPasswordString.equals("true");
        showPassword = sp.getBoolean("show_password", showPassword);
        if (showPassword == true)
            return password;
        else {
            StringBuilder sb = new StringBuilder(password.length());
            for (int i = 0; i < password.length(); ++i)
                sb.append('*');
            return sb.toString();
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    protected <T extends Preference> T findPref(CharSequence key) {
        return (T) this.findPreference(key);
    }

}
