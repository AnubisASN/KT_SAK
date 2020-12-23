package com.anubis.module_tts

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_ttse.eTTSE
import kotlinx.android.synthetic.main.activity_tts_test.*
import org.jetbrains.anko.onItemSelectedListener

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ttsTest : AppCompatActivity() {
    private lateinit var mTTS: eTTS
    private lateinit var mTTSE: eTTSE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts_test)
        mTTSE = eTTSE.eInit(this, listener = TextToSpeech.OnInitListener {
            it.eLog("OnInitListener")
        })
        testInit()
    }

    /*无缝衔接DEMO*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun testInit() {
        mTTS = eTTS.eInit(application, arrayOf("23260583", "2dPEskLsvn6Gub6GtVna6oGT", "dYcjDn2G6r0UkLQ3sGWQ7xDWop9MZYhn"), Handler(), TTSMode.MIX, VoiceModel.MALE)
        val arrayEngine = eTTSE.eSearchEngine(this)
        arrayEngine.add(0, "APP自带")
        R.layout.activity_tts_test
        tts_spinner.adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayEngine)
        tts_spinner.onItemSelectedListener {
            onItemSelected { adapterView, view, i, l ->
                if (i == 0) {
                    mTTS?.eSetParams(extendEngineBlock = null)
                } else {
                    mTTS?.eSetParams {
                        if (it == null) {
                            mTTSE = eTTSE.eInit(this@ttsTest, arrayEngine[i])
                        } else {
                            mTTSE.eInitEngine(arrayEngine[i])
                        }
                        return@eSetParams mTTSE
                    }
                }
            }
        }
    }
fun onClick(v:View){
    when(v.id){
        button.id->mTTS.eSpeak(editText.text.toString())
        button2.id->eShowTip(mTTSE.eEngines?.joinToString().toString())
        button5.id->mTTSE.eToTtsData(this)
        button6.id->mTTSE.eToTtsSet(this)
        button7.id->mTTSE.eDiaDownload(this)
    }
}

}
