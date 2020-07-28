package com.anubis.app_textr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import com.anubis.app_textr.R.id.button0
import com.anubis.kt_extends.eBitmap
import com.anubis.module_textrecognition.eTextRecognition
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eTextRecognition.eInit(this@MainActivity)
    }

    fun onClick(v: View) {
        var bitmap:Bitmap?=null
        when (v.id) {
            button0.id -> {
                  bitmap = eBitmap.eDrawableToBitmap(resources.getDrawable(R.drawable.m0))

            }
            button1.id -> {
                bitmap = eBitmap.eDrawableToBitmap(resources.getDrawable(R.drawable.m1))
            }
        }
        val bit=bitmap!!.copy(Bitmap.Config.ARGB_8888,true)
        imageView.setImageBitmap(bit)
        async {
        val text = eTextRecognition.eRecognition(bit)
            textView.post { textView.text=text }
        }
    }

}
