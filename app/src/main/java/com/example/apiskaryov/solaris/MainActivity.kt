package com.example.apiskaryov.solaris

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView


class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imgBtn = findViewById(R.id.imgBtn) as Button
        imgBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            if (requestCode == PICK_IMAGE) {
                val uri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                val imageView = findViewById(R.id.imgView) as ImageView
                imageView.setImageBitmap(bitmap)
                val solBtn = findViewById(R.id.imgBtn) as Button
                solBtn.text = "Solarize"
                solBtn.setOnClickListener {
                    solarize(bitmap)
                }
            }
        }catch (e : Exception) {
            print(e.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun solarize(bitMap: Bitmap) {
        val imageView = findViewById(R.id.imgView) as ImageView
        val lenght = bitMap.width * bitMap.height
        val pixels = IntArray(lenght)
        bitMap.getPixels(pixels, 0, 0, 0, 0, bitMap.width, bitMap.height)
        pixels.map { pixel -> {
            val R = Color.red(pixel)
            val B = Color.blue(pixel)
            val G = Color.green(pixel)
            val gray = ((0.3 * R) + (0.59 * G) + (0.11 * B)).toFloat()
            val res = Color.rgb(gray, gray, gray)
        } }
    }


}
