package com.anubis.module_Thermography.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

/**
 * Created by jackie.sun
 */
object ImageHelper {

    /**
     * 利用 rgba 来修改图片
     * @param bm            所需修改的图片
     * @param hue           色调值
     * @param saturation    饱和度
     * @param lum           亮度
     * @return              修改完成的图片
     */
    fun getHandleImageForARGB(bm: Bitmap, hue: Float, saturation: Float, lum: Float): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        //色调 0-R 1-G 2-B
        val hueMatrix = ColorMatrix()
        hueMatrix.setRotate(0, hue)
        hueMatrix.setRotate(1, hue)
        hueMatrix.setRotate(2, hue)

        //饱和度
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)

        //亮度
        val lumMatrix = ColorMatrix()
        lumMatrix.setScale(lum, lum, lum, 1f)

        //将色调,饱和度,亮度全糅合要一起
        val imageMatrix = ColorMatrix()
        imageMatrix.postConcat(hueMatrix)
        imageMatrix.postConcat(satMatrix)
        imageMatrix.postConcat(lumMatrix)

        paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
        canvas.drawBitmap(bm, 0f, 0f, paint)
        return bmp
    }

    //底片效果
    fun handleImageNegative(bm: Bitmap): Bitmap {
        val width = bm.width
        val height = bm.height
        var color: Int
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)
        bm.getPixels(oldPx, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            color = oldPx[i]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)

            r1 = getJudgedData(255 - r)
            g1 = getJudgedData(255 - g)
            b1 = getJudgedData(255 - b)
            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    //老照片
    fun handleImagePixelsOldPhoto(bm: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height,
                Bitmap.Config.ARGB_8888)
        val width = bm.width
        val height = bm.height
        var color = 0
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)

        bm.getPixels(oldPx, 0, bm.width, 0, 0, width, height)
        for (i in 0 until width * height) {
            color = oldPx[i]
            a = Color.alpha(color)
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)

            r1 = getJudgedData((0.393 * r + 0.769 * g + 0.189 * b).toInt())
            g1 = getJudgedData((0.349 * r + 0.686 * g + 0.168 * b).toInt())
            b1 = getJudgedData((0.272 * r + 0.534 * g + 0.131 * b).toInt())

            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    //浮雕效果
    fun handleImagePixelsRelief(bm: Bitmap): Bitmap {
        val bmp = Bitmap.createBitmap(bm.width, bm.height,
                Bitmap.Config.ARGB_8888)
        val width = bm.width
        val height = bm.height
        var color = 0
        var colorBefore = 0
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var r1: Int
        var g1: Int
        var b1: Int

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)

        bm.getPixels(oldPx, 0, bm.width, 0, 0, width, height)
        for (i in 1 until width * height) {
            colorBefore = oldPx[i - 1]
            a = Color.alpha(colorBefore)
            r = Color.red(colorBefore)
            g = Color.green(colorBefore)
            b = Color.blue(colorBefore)

            color = oldPx[i]
            r1 = Color.red(color)
            g1 = Color.green(color)
            b1 = Color.blue(color)

            r = getJudgedData(r - r1 + 127)
            g = getJudgedData(g - g1 + 127)
            b = getJudgedData(b - b1 + 127)
            newPx[i] = Color.argb(a, r, g, b)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    private fun getJudgedData(oldData: Int): Int {
        var newData = oldData
        if (newData > 255) {
            newData = 255
        } else if (newData < 0) {
            newData = 0
        }
        return newData
    }
}
