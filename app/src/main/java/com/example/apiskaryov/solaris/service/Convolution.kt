package com.example.apiskaryov.solaris.service

/**
 * Created by a.piskaryov on 19.09.2017.
 */
import android.graphics.Bitmap
import android.graphics.Color

class ConvolutionMatrix {

    var Factor = 1.0
    var Offset = 1.0

    fun computeConvolution3x3(src: Bitmap, kernel: Array<DoubleArray>): Bitmap {
        val width = src.width
        val height = src.height
        val result = Bitmap.createBitmap(width, height, src.config)
        val SIZE = kernel.size

        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var sumR: Int
        var sumG: Int
        var sumB: Int
        val pixels = Array(SIZE) { IntArray(SIZE) }

        for (y in 0 until height - 2) {
            for (x in 0 until width - 2) {
                // get pixel matrix
                for (i in 0 until SIZE) {
                    for (j in 0 until SIZE) {
                        pixels[i][j] = src.getPixel(x + i, y + j)
                    }
                }
                // get alpha of center pixel
                A = Color.alpha(pixels[1][1])

                // init color sum
                sumB = 0
                sumG = sumB
                sumR = sumG

                // get sum of RGB on matrix
                for (i in 0 until SIZE) {
                    for (j in 0 until SIZE) {
                        sumR += (Color.red(pixels[i][j]) * kernel[i][j]).toInt()
                        sumG += (Color.green(pixels[i][j]) * kernel[i][j]).toInt()
                        sumB += (Color.blue(pixels[i][j]) * kernel[i][j]).toInt()
                    }
                }
                // get final Red
                R = (sumR / Factor + Offset).toInt()
                if (R < 0) {
                    R = 0
                } else if (R > 255) {
                    R = 255
                }

                // get final Green
                G = (sumG / Factor + Offset).toInt()
                if (G < 0) {
                    G = 0
                } else if (G > 255) {
                    G = 255
                }

                // get final Blue
                B = (sumB / Factor + Offset).toInt()
                if (B < 0) {
                    B = 0
                } else if (B > 255) {
                    B = 255
                }
                // apply new pixel
                result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B))
            }
        }
        // final image
        return result
    }
}