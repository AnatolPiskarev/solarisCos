package com.example.apiskaryov.solaris.service

import android.graphics.*
import android.util.Log
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking


/**
 * Created by a.piskaryov on 18.09.2017.
 */
class ImageService {
    private fun getPixels(bitMap: Bitmap): IntArray {
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

    fun toPseudoBinary(bitMap: Bitmap): Bitmap {
        val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(0.0, 2.0), doubleArrayOf(3.0, 1.0))
        return ConvolutionMatrix().computeConvolution3x3(bitMap, kernel)
    }

    private fun solarize(pixel: Int): Int {
        val R = solarizePixel(Color.red(pixel))
        val G = solarizePixel(Color.green(pixel))
        val B = solarizePixel(Color.blue(pixel))
        return Color.rgb(R, G, B)
    }

    fun toBinary(bitMap: Bitmap): Bitmap {
        return try {
            val bmOut = Bitmap.createBitmap(bitMap.width, bitMap.height, bitMap.config)
            runBlocking {
                val pixels = getPixels(bitMap).mapInPlace { getBinaryValue(it) }
                bmOut.setPixels(pixels, 0, bitMap.width, 0, 0, bitMap.width, bitMap.height)
            }
            bmOut
        } catch (e: Exception) {
            Log.e("error", e.message)
            bitMap
        }
    }


    private fun solarizePixel(pixel: Int) =
            if (pixel <= 127) (255 - pixel) else pixel


    private inline fun IntArray.mapInPlace(mutate: (Int) -> Int): IntArray {
        this.forEachIndexed { idx, value ->
            this[idx] = mutate(value)
        }
        return this
    }

    private val binaryTreshold = 13.0 / 30.0
    suspend private fun getBinaryValue(pixel: Int): Int {
        val R = Color.red(pixel)
        val B = Color.blue(pixel)
        val G = Color.green(pixel)
        val distanceFromWhite = Math.sqrt(Math.pow((0xff - R).toDouble(), 2.0) + Math.pow((0xff - B).toDouble(), 2.0) + Math.pow((0xff - G).toDouble(), 2.0))
        // distance from the black extreme //this should not be computed and might be as well a function of distanceFromWhite and the whole distance
        val distanceFromBlack = Math.sqrt(Math.pow((0x00 - R).toDouble(), 2.0) + Math.pow((0x00 - B).toDouble(), 2.0) + Math.pow((0x00 - G).toDouble(), 2.0))
        // distance between the extremes //this is a constant that should not be computed :p
        val distance = distanceFromBlack + distanceFromWhite
        // distance between the extremes
        return if (distanceFromWhite / distance > binaryTreshold) Color.BLACK else Color.WHITE
    }
}
