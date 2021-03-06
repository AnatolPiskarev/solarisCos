package com.example.apiskaryov.solaris.service

import android.graphics.*
import android.util.Log
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


/**
 * Created by a.piskaryov on 18.09.2017.
 */
class ImageService {
     fun getPixels(bitMap: Bitmap): IntArray {
        val length = bitMap.width * bitMap.height
        val pixels = IntArray(length)
        bitMap.getPixels(pixels, 0, bitMap.width, 0, 0, bitMap.width, bitMap.height)
        return pixels
    }

    fun toShadesOfGray(bitmap: Bitmap): Bitmap {
        try {
            val width = bitmap.width
            val height = bitmap.height
            val bmOut = Bitmap.createBitmap(width, height, bitmap.config)
            val canvas = Canvas(bmOut)
            val ma = ColorMatrix()
            ma.setSaturation(0f)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(ma)
            canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), paint)
            return bmOut
        } catch (e: Exception) {
            Log.e("error", e.message)
        }
        return bitmap
    }

    fun getStatistic(bitMap: Bitmap): IntArray {
        val stats = IntArray(256)
        try {
            getPixels(bitMap).forEach { c ->
                val color = Color.red(c)
                stats[color]++
            }

        } catch (e: Exception) {
            Log.e("error", e.message)
        }

        return stats
    }

    fun solarize(bitMap: Bitmap): Bitmap {
        return try {
            val bmOut = Bitmap.createBitmap(bitMap.width, bitMap.height, bitMap.config)
            val pixels = getPixels(bitMap).mapInPlace { p -> solarize(p) }
            bmOut.setPixels(pixels, 0, bitMap.width, 0, 0, bitMap.width, bitMap.height)
            bmOut
        } catch (e: Exception) {
            Log.e("error", e.message)
            bitMap
        }
    }

    fun stampFilter(bitMap: Bitmap): Bitmap {
        val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(0.0, 1.0, 0.0), doubleArrayOf(1.0, 0.0, -1.0), doubleArrayOf(0.0, -1.0, 0.0))
        return ConvolutionMatrix().computeConvolution3x3(bitMap, kernel)
    }

    fun toBinary2(bitMap: Bitmap): Bitmap {
        val matrix = Mat()
        val bmOut = Bitmap.createBitmap(bitMap.width, bitMap.height, bitMap.config)
        Mat().let {
            Utils.bitmapToMat(bitMap, matrix)
            Imgproc.adaptiveThreshold(matrix, it, 255.0,
                    Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40.0 )
          Utils.matToBitmap(it, bmOut)
        }
        return bmOut

    }

    private fun solarize(pixel: Int): Int {
        val R = solarizePixel(Color.red(pixel))
        val G = solarizePixel(Color.green(pixel))
        val B = solarizePixel(Color.blue(pixel))
        return Color.rgb(R, G, B)
    }

    private fun solarizePixel(pixel: Int) =
            if (pixel <= 127) (255 - pixel) else pixel


    private inline fun IntArray.mapInPlace(mutate: (Int) -> Int): IntArray {
        this.forEachIndexed { idx, value ->
            this[idx] = mutate(value)
        }
        return this
    }

}

