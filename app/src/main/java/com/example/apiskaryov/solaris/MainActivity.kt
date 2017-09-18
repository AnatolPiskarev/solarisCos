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
    private lateinit var mainBtn : Button
    private lateinit var grayBtn : Button
    private lateinit var solBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
         mainBtn = findViewById(R.id.imgBtn) as Button
         grayBtn = findViewById(R.id.grayBtn) as Button
         solBtn = findViewById(R.id.SolarBtn) as Button
        grayBtn.isEnabled = false
        solBtn.isEnabled = false
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
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                imageView.setImageBitmap(bitmap)
                solBtn.isEnabled = true
                grayBtn.isEnabled = true
                grayBtn.text = "Gray"
                grayBtn.setOnClickListener {
                    val grayBitMap = imgService.toShadesOfGray(bitmap)
                    imageView.setImageBitmap(grayBitMap)
                    grayBtn.text = "Show Statistic"
                    grayBtn.setOnClickListener {
                        showStats(grayBitMap)
                    }
                }
                solBtn.setOnClickListener {
                    val solBitMap = imgService.solarize(bitmap)
                    imageView.setImageBitmap(solBitMap)
                }
            }
        } catch (e: Exception) {
           Log.e("error", e.message)
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun showStats(bitmap: Bitmap) {
        val stats = imgService.getStatistic(bitmap)
        val intent = Intent(baseContext, ChartActivity::class.java)
        intent.putExtra("data", stats)
        startActivity(intent)
    }
}
