package com.anubis.module_picker

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.anubis.kt_extends.eTime
import com.anubis.module_picker.Utils.DateUtil
import com.anubis.module_picker.Utils.ScreenUtil
import com.anubis.module_picker.Utils.TextUtil
import com.anubis.module_picker.view.PickerView
import me.rosuh.filepicker.FilePickerActivity


import java.util.ArrayList
import java.util.Calendar

/**
 * Created by liuli on 2015/11/27.
 */
class eTimePicker private constructor() {
    private var scrollUnits = SCROLLTYPE.HOUR.value + SCROLLTYPE.MINUTE.value
    private val FORMAT_STR = "yyyy-MM-dd HH:mm"
    private var seletorDialog: Dialog? = null
    private var year_pv: PickerView? = null
    private var month_pv: PickerView? = null
    private var day_pv: PickerView? = null
    private var hour_pv: PickerView? = null
    private var minute_pv: PickerView? = null
    private val MAXMINUTE = 59
    private var MAXHOUR = 23
    private val MINMINUTE = 0
    private var MINHOUR = 0
    private val MAXMONTH = 12
    private var year: ArrayList<String>? = null
    private var month: ArrayList<String>? = null
    private var day: ArrayList<String>? = null
    private var hour: ArrayList<String>? = null
    private var minute: ArrayList<String>? = null
    private var startYear: Int = 0
    private var startMonth: Int = 0
    private var startDay: Int = 0
    private var startHour: Int = 0
    private var startMininute: Int = 0
    private var endYear: Int = 0
    private var endMonth: Int = 0
    private var endDay: Int = 0
    private var endHour: Int = 0
    private var endMininute: Int = 0
    private var minute_workStart: Int = 0
    private var minute_workEnd: Int = 0
    private var hour_workStart: Int = 0
    private var hour_workEnd: Int = 0
    private var spanYear: Boolean = false
    private var spanMon: Boolean = false
    private var spanDay: Boolean = false
    private var spanHour: Boolean = false
    private var spanMin: Boolean = false
    private val selectedCalender = Calendar.getInstance()
    private val ANIMATORDELAY = 200L
    private val CHANGEDELAY = 90L
    private var workStart_str: String? = null
    private var workEnd_str: String? = null
    private val startCalendar: Calendar
    private val endCalendar: Calendar
    private var tv_cancle: TextView? = null
    private var tv_select: TextView? = null
    private var tv_title: TextView? = null
    private var hour_text: TextView? = null
    private var minute_text: TextView? = null

    companion object {
        private lateinit var mContext: Context
        private lateinit var mRHandler: ResultHandler
        fun eInit(context: Context, rhandler: ResultHandler): eTimePicker {
            mContext = context
            mRHandler = rhandler
            return eInit
        }
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTimePicker() }
    }

    interface ResultHandler {
        fun handle(time: String)
    }

    enum class SCROLLTYPE private constructor(var value: Int) {
        HOUR(1),
        MINUTE(2)
    }

    enum class MODE private constructor(var value: Int) {
        YMD(1),
        YMDHM(2)
    }

    init {
        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()
        initDialog()
        initView()
    }

    fun eShowTimeSelect(startDate: String, endDate: String = eTime.eInit.eGetCurrentTime()) {
        startCalendar.time = DateUtil.parse(startDate, FORMAT_STR)
        endCalendar.time = DateUtil.parse(endDate, FORMAT_STR)
        if (startCalendar.time.time >= endCalendar.time.time) {
            Toast.makeText(mContext, "start>end", Toast.LENGTH_LONG).show()
            return
        }
        if (!excuteWorkTime()) return
        initParameter()
        initTimer()
        addListener()
        seletorDialog!!.show()
    }

    private fun initDialog() {
        if (seletorDialog == null) {
            seletorDialog = Dialog(mContext, R.style.time_dialog)
            seletorDialog!!.setCancelable(false)
            seletorDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            seletorDialog!!.setContentView(R.layout.dialog_selector)
            val window = seletorDialog!!.window
            window!!.setGravity(Gravity.BOTTOM)
            val lp = window.attributes
            val width = ScreenUtil.getInstance(mContext).screenWidth
            lp.width = width
            window.attributes = lp
        }
    }

    private fun initView() {
        year_pv = seletorDialog!!.findViewById<View>(R.id.year_pv) as PickerView
        month_pv = seletorDialog!!.findViewById<View>(R.id.month_pv) as PickerView
        day_pv = seletorDialog!!.findViewById<View>(R.id.day_pv) as PickerView
        hour_pv = seletorDialog!!.findViewById<View>(R.id.hour_pv) as PickerView
        minute_pv = seletorDialog!!.findViewById<View>(R.id.minute_pv) as PickerView
        tv_cancle = seletorDialog!!.findViewById<View>(R.id.tv_cancle) as TextView
        tv_select = seletorDialog!!.findViewById<View>(R.id.tv_select) as TextView
        tv_title = seletorDialog!!.findViewById<View>(R.id.tv_title) as TextView
        hour_text = seletorDialog!!.findViewById<View>(R.id.hour_text) as TextView
        minute_text = seletorDialog!!.findViewById<View>(R.id.minute_text) as TextView

        tv_cancle!!.setOnClickListener { seletorDialog!!.dismiss() }
        tv_select!!.setOnClickListener {
                mRHandler.handle(DateUtil.format(selectedCalender.time, FORMAT_STR))
                seletorDialog!!.dismiss()
        }
    }

    private fun initParameter() {
        startYear = startCalendar.get(Calendar.YEAR)
        startMonth = startCalendar.get(Calendar.MONTH) + 1
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH)
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY)
        startMininute = startCalendar.get(Calendar.MINUTE)
        endYear = endCalendar.get(Calendar.YEAR)
        endMonth = endCalendar.get(Calendar.MONTH) + 1
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH)
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY)
        endMininute = endCalendar.get(Calendar.MINUTE)
        spanYear = startYear != endYear
        spanMon = !spanYear && startMonth != endMonth
        spanDay = !spanMon && startDay != endDay
        spanHour = !spanDay && startHour != endHour
        spanMin = !spanHour && startMininute != endMininute
        selectedCalender.time = startCalendar.time
    }

    private fun initTimer() {
        initArrayList()
        if (spanYear) {
            for (i in startYear..endYear) {
                year!!.add(i.toString())
            }
            for (i in startMonth..MAXMONTH) {
                month!!.add(fomatTimeUnit(i))
            }
            for (i in startDay..startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(fomatTimeUnit(i))
            }
            if (scrollUnits and SCROLLTYPE.HOUR.value != SCROLLTYPE.HOUR.value) {
                hour!!.add(fomatTimeUnit(startHour))
            } else {
                for (i in startHour..MAXHOUR) {
                    hour!!.add(fomatTimeUnit(i))
                }
            }
            if (scrollUnits and SCROLLTYPE.MINUTE.value != SCROLLTYPE.MINUTE.value) {
                minute!!.add(fomatTimeUnit(startMininute))
            } else {
                for (i in startMininute..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }
        } else if (spanMon) {
            year!!.add(startYear.toString())
            for (i in startMonth..endMonth) {
                month!!.add(fomatTimeUnit(i))
            }
            for (i in startDay..startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(fomatTimeUnit(i))
            }
            if (scrollUnits and SCROLLTYPE.HOUR.value != SCROLLTYPE.HOUR.value) {
                hour!!.add(fomatTimeUnit(startHour))
            } else {
                for (i in startHour..MAXHOUR) {
                    hour!!.add(fomatTimeUnit(i))
                }
            }

            if (scrollUnits and SCROLLTYPE.MINUTE.value != SCROLLTYPE.MINUTE.value) {
                minute!!.add(fomatTimeUnit(startMininute))
            } else {
                for (i in startMininute..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }
        } else if (spanDay) {
            year!!.add(startYear.toString())
            month!!.add(fomatTimeUnit(startMonth))
            for (i in startDay..endDay) {
                day!!.add(fomatTimeUnit(i))
            }
            if (scrollUnits and SCROLLTYPE.HOUR.value != SCROLLTYPE.HOUR.value) {
                hour!!.add(fomatTimeUnit(startHour))
            } else {
                for (i in startHour..MAXHOUR) {
                    hour!!.add(fomatTimeUnit(i))
                }
            }

            if (scrollUnits and SCROLLTYPE.MINUTE.value != SCROLLTYPE.MINUTE.value) {
                minute!!.add(fomatTimeUnit(startMininute))
            } else {
                for (i in startMininute..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }

        } else if (spanHour) {
            year!!.add(startYear.toString())
            month!!.add(fomatTimeUnit(startMonth))
            day!!.add(fomatTimeUnit(startDay))

            if (scrollUnits and SCROLLTYPE.HOUR.value != SCROLLTYPE.HOUR.value) {
                hour!!.add(fomatTimeUnit(startHour))
            } else {
                for (i in startHour..endHour) {
                    hour!!.add(fomatTimeUnit(i))
                }

            }

            if (scrollUnits and SCROLLTYPE.MINUTE.value != SCROLLTYPE.MINUTE.value) {
                minute!!.add(fomatTimeUnit(startMininute))
            } else {
                for (i in startMininute..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }


        } else if (spanMin) {
            year!!.add(startYear.toString())
            month!!.add(fomatTimeUnit(startMonth))
            day!!.add(fomatTimeUnit(startDay))
            hour!!.add(fomatTimeUnit(startHour))


            if (scrollUnits and SCROLLTYPE.MINUTE.value != SCROLLTYPE.MINUTE.value) {
                minute!!.add(fomatTimeUnit(startMininute))
            } else {
                for (i in startMininute..endMininute) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }
        }
        loadComponent()
    }
    private fun excuteWorkTime(): Boolean {
        val res = true
        if (!TextUtil.isEmpty(workStart_str) && !TextUtil.isEmpty(workEnd_str)) {
            val start = workStart_str?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            val end = workEnd_str?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            hour_workStart = Integer.parseInt(start!![0])
            minute_workStart = Integer.parseInt(start[1])
            hour_workEnd = Integer.parseInt(end!![0])
            minute_workEnd = Integer.parseInt(end[1])
            val workStartCalendar = Calendar.getInstance()
            val workEndCalendar = Calendar.getInstance()
            workStartCalendar.time = startCalendar.time
            workEndCalendar.time = endCalendar.time
            workStartCalendar.set(Calendar.HOUR_OF_DAY, hour_workStart)
            workStartCalendar.set(Calendar.MINUTE, minute_workStart)
            workEndCalendar.set(Calendar.HOUR_OF_DAY, hour_workEnd)
            workEndCalendar.set(Calendar.MINUTE, minute_workEnd)
            val startTime = Calendar.getInstance()
            val endTime = Calendar.getInstance()
            val startWorkTime = Calendar.getInstance()
            val endWorkTime = Calendar.getInstance()
            startTime.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY))
            startTime.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE))
            endTime.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY))
            endTime.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE))
            startWorkTime.set(Calendar.HOUR_OF_DAY, workStartCalendar.get(Calendar.HOUR_OF_DAY))
            startWorkTime.set(Calendar.MINUTE, workStartCalendar.get(Calendar.MINUTE))
            endWorkTime.set(Calendar.HOUR_OF_DAY, workEndCalendar.get(Calendar.HOUR_OF_DAY))
            endWorkTime.set(Calendar.MINUTE, workEndCalendar.get(Calendar.MINUTE))
            if (startTime.time.time == endTime.time.time || startWorkTime.time.time < startTime.time.time && endWorkTime.time.time < startTime.time.time) {
                Toast.makeText(mContext, "Wrong parames!", Toast.LENGTH_LONG).show()
                return false
            }
            startCalendar.time = if (startCalendar.time.time < workStartCalendar.time.time) workStartCalendar.time else startCalendar.time
            endCalendar.time = if (endCalendar.time.time > workEndCalendar.time.time) workEndCalendar.time else endCalendar.time
            MINHOUR = workStartCalendar.get(Calendar.HOUR_OF_DAY)
            MAXHOUR = workEndCalendar.get(Calendar.HOUR_OF_DAY)
        }
        return res
    }

    private fun fomatTimeUnit(unit: Int): String {
        return if (unit < 10) "0$unit" else unit.toString()
    }

    private fun initArrayList() {
        if (year == null) year = ArrayList()
        if (month == null) month = ArrayList()
        if (day == null) day = ArrayList()
        if (hour == null) hour = ArrayList()
        if (minute == null) minute = ArrayList()
        year!!.clear()
        month!!.clear()
        day!!.clear()
        hour!!.clear()
        minute!!.clear()
    }

    private fun addListener() {
        year_pv!!.setOnSelectListener { text ->
            selectedCalender.set(Calendar.YEAR, Integer.parseInt(text))
            monthChange()
        }
        month_pv!!.setOnSelectListener { text ->
            selectedCalender.set(Calendar.DAY_OF_MONTH, 1)
            selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1)
            dayChange()
        }
        day_pv!!.setOnSelectListener { text ->
            selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text))
            hourChange()
        }
        hour_pv!!.setOnSelectListener { text ->
            selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text))
            minuteChange()
        }
        minute_pv!!.setOnSelectListener { text -> selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text)) }

    }

    private fun loadComponent() {
        year_pv!!.setData(year)
        month_pv!!.setData(month)
        day_pv!!.setData(day)
        hour_pv!!.setData(hour)
        minute_pv!!.setData(minute)
        year_pv!!.setSelected(0)
        month_pv!!.setSelected(0)
        day_pv!!.setSelected(0)
        hour_pv!!.setSelected(0)
        minute_pv!!.setSelected(0)
        excuteScroll()
    }

    private fun excuteScroll() {
        year_pv!!.setCanScroll(year!!.size > 1)
        month_pv!!.setCanScroll(month!!.size > 1)
        day_pv!!.setCanScroll(day!!.size > 1)
        hour_pv!!.setCanScroll(hour!!.size > 1 && scrollUnits and SCROLLTYPE.HOUR.value == SCROLLTYPE.HOUR.value)
        minute_pv!!.setCanScroll(minute!!.size > 1 && scrollUnits and SCROLLTYPE.MINUTE.value == SCROLLTYPE.MINUTE.value)
    }

    private fun monthChange() {

        month!!.clear()
        val selectedYear = selectedCalender.get(Calendar.YEAR)
        if (selectedYear == startYear) {
            for (i in startMonth..MAXMONTH) {
                month!!.add(fomatTimeUnit(i))
            }
        } else if (selectedYear == endYear) {
            for (i in 1..endMonth) {
                month!!.add(fomatTimeUnit(i))
            }
        } else {
            for (i in 1..MAXMONTH) {
                month!!.add(fomatTimeUnit(i))
            }
        }
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(month!![0]) - 1)
        month_pv!!.setData(month)
        month_pv!!.setSelected(0)
        excuteAnimator(ANIMATORDELAY, month_pv!!)

        month_pv!!.postDelayed({ dayChange() }, CHANGEDELAY)

    }

    private fun dayChange() {

        day!!.clear()
        val selectedYear = selectedCalender.get(Calendar.YEAR)
        val selectedMonth = selectedCalender.get(Calendar.MONTH) + 1
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (i in startDay..selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(fomatTimeUnit(i))
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (i in 1..endDay) {
                day!!.add(fomatTimeUnit(i))
            }
        } else {
            for (i in 1..selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(fomatTimeUnit(i))
            }
        }
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day!![0]))
        day_pv!!.setData(day)
        day_pv!!.setSelected(0)
        excuteAnimator(ANIMATORDELAY, day_pv!!)

        day_pv!!.postDelayed({ hourChange() }, CHANGEDELAY)
    }

    private fun hourChange() {
        if (scrollUnits and SCROLLTYPE.HOUR.value == SCROLLTYPE.HOUR.value) {
            hour!!.clear()
            val selectedYear = selectedCalender.get(Calendar.YEAR)
            val selectedMonth = selectedCalender.get(Calendar.MONTH) + 1
            val selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH)

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                for (i in startHour..MAXHOUR) {
                    hour!!.add(fomatTimeUnit(i))
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                for (i in MINHOUR..endHour) {
                    hour!!.add(fomatTimeUnit(i))
                }
            } else {

                for (i in MINHOUR..MAXHOUR) {
                    hour!!.add(fomatTimeUnit(i))
                }

            }
            selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour!![0]))
            hour_pv!!.setData(hour)
            hour_pv!!.setSelected(0)
            excuteAnimator(ANIMATORDELAY, hour_pv!!)
        }
        hour_pv!!.postDelayed({ minuteChange() }, CHANGEDELAY)

    }

    private fun minuteChange() {
        if (scrollUnits and SCROLLTYPE.MINUTE.value == SCROLLTYPE.MINUTE.value) {
            minute!!.clear()
            val selectedYear = selectedCalender.get(Calendar.YEAR)
            val selectedMonth = selectedCalender.get(Calendar.MONTH) + 1
            val selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH)
            val selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY)

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                for (i in startMininute..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                for (i in MINMINUTE..endMininute) {
                    minute!!.add(fomatTimeUnit(i))
                }
            } else if (selectedHour == hour_workStart) {
                for (i in minute_workStart..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            } else if (selectedHour == hour_workEnd) {
                for (i in MINMINUTE..minute_workEnd) {
                    minute!!.add(fomatTimeUnit(i))
                }
            } else {
                for (i in MINMINUTE..MAXMINUTE) {
                    minute!!.add(fomatTimeUnit(i))
                }
            }
            selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute!![0]))
            minute_pv!!.setData(minute)
            minute_pv!!.setSelected(0)
            excuteAnimator(ANIMATORDELAY, minute_pv!!)

        }
        excuteScroll()


    }

    private fun excuteAnimator(ANIMATORDELAY: Long, view: View) {
        val pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f)
        val pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.3f, 1f)
        val pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.3f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATORDELAY).start()
    }

    fun setNextBtTip(str: String) {
        tv_select!!.text = str
    }

    fun setTitle(str: String) {
        tv_title!!.text = str
    }

    fun disScrollUnit(vararg scrolltypes: SCROLLTYPE): Int {
        if (scrolltypes == null || scrolltypes.size == 0)
            scrollUnits = SCROLLTYPE.HOUR.value + SCROLLTYPE.MINUTE.value
        for (scrolltype in scrolltypes) {
            scrollUnits = scrollUnits xor scrolltype.value
        }
        return scrollUnits
    }

    fun setMode(mode: MODE) {
        when (mode.value) {
            1 -> {
                disScrollUnit(SCROLLTYPE.HOUR, SCROLLTYPE.MINUTE)
                hour_pv!!.visibility = View.GONE
                minute_pv!!.visibility = View.GONE
                hour_text!!.visibility = View.GONE
                minute_text!!.visibility = View.GONE
            }
            2 -> {
                disScrollUnit()
                hour_pv!!.visibility = View.VISIBLE
                minute_pv!!.visibility = View.VISIBLE
                hour_text!!.visibility = View.VISIBLE
                minute_text!!.visibility = View.VISIBLE
            }
        }
    }

    fun setIsLoop(isLoop: Boolean) {
        this.year_pv!!.setIsLoop(isLoop)
        this.month_pv!!.setIsLoop(isLoop)
        this.day_pv!!.setIsLoop(isLoop)
        this.hour_pv!!.setIsLoop(isLoop)
        this.minute_pv!!.setIsLoop(isLoop)
    }
}
