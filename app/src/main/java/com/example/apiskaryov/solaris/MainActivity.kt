package com.example.apiskaryov.solaris

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.example.apiskaryov.solaris.service.ImageService


class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1
    private val imgService = ImageService()
    private lateinit var mainBtn: Button
    private lateinit var grayBtn: Button
    private lateinit var solBtn: Button
    private lateinit var stampBtn: Button
    private lateinit var statBtn: Button
    private lateinit var originBtn: Button
    private lateinit var originalImage: Bitmap
    private lateinit var currentImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        mainBtn = findViewById(R.id.imgBtn) as Button
        grayBtn = findViewById(R.id.grayBtn) as Button
        solBtn = findViewById(R.id.SolarBtn) as Button
        stampBtn = findViewById(R.id.StampBtn) as Button
        statBtn = findViewById(R.id.StatBtn) as Button
        originBtn = findViewById(R.id.OrigBtn) as Button
        grayBtn.isEnabled = false
        originBtn.isEnabled = false
        solBtn.isEnabled = false
        stampBtn.isEnabled = false
        statBtn.isEnabled = false
        super.onCreate(savedInstanceState)
        mainBtn.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            if (requestCode == PICK_IMAGE) {
                val imageView = findViewById(R.id.imgView) as ImageView
                val uri = data.data
               originalImage = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                currentImage = originalImage
                imageView.setImageBitmap(originalImage)
                solBtn.isEnabled = true
                grayBtn.isEnabled = true
                originBtn.isEnabled = true
                stampBtn.isEnabled = true
                statBtn.isEnabled = true
                grayBtn.setOnClickListener {
                    currentImage = imgService.toShadesOfGray(originalImage)
                    imageView.setImageBitmap(currentImage)
                    }
                statBtn.setOnClickListener {
                    showStats(originalImage)
                }
                solBtn.setOnClickListener {
                    currentImage = imgService.solarize(originalImage)
                    imageView.setImageBitmap(currentImage)
                }
                stampBtn.setOnClickListener {
                    currentImage = imgService.stampFilter(originalImage)
                    imageView.setImageBitmap(currentImage)
                }
                originBtn.setOnClickListener {
                    currentImage.recycle()
                    imageView.setImageBitmap(originalImage)
                    currentImage = originalImage
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.message)
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showStats(bitmap: Bitmap) {
        val stats = imgService.getStatistic(bitmap)
        val intent = Intent(baseContext, ChartActivity::class.java)
        intent.putExtra("data", stats)
        startActivity(intent)
    }
}
