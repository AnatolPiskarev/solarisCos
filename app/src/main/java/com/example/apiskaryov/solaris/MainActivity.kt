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
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.BaseLoaderCallback
import org.opencv.core.Mat
import org.opencv.android.OpenCVLoader




class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1
    private val imgService = ImageService()
    private lateinit var mainBtn: Button
    private lateinit var grayBtn: Button
    private lateinit var solBtn: Button
    private lateinit var stampBtn: Button
    private lateinit var statBtn: Button
    private lateinit var originBtn: Button
    private lateinit var binaryBtn: Button
    private lateinit var originalImage: Bitmap
    private lateinit var currentImage: Bitmap
    private lateinit var spoonButton: Button
    private lateinit var mLoaderCallback :BaseLoaderCallback

    override fun onCreate(savedInstanceState: Bundle?) {

        mLoaderCallback = object : BaseLoaderCallback(this) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    LoaderCallbackInterface.SUCCESS -> {
                        Mat()
                    }
                    else -> {
                        super.onManagerConnected(status)
                    }
                }
            }
        }

        setContentView(R.layout.activity_main)
        mainBtn = findViewById(R.id.imgBtn) as Button
        grayBtn = findViewById(R.id.grayBtn) as Button
        solBtn = findViewById(R.id.SolarBtn) as Button
        stampBtn = findViewById(R.id.StampBtn) as Button
        statBtn = findViewById(R.id.StatBtn) as Button
        originBtn = findViewById(R.id.OrigBtn) as Button
        binaryBtn = findViewById(R.id.BinaryBtn) as Button
        spoonButton = findViewById(R.id.SpoonBtn) as Button
        grayBtn.isEnabled = false
        originBtn.isEnabled = false
        solBtn.isEnabled = false
        stampBtn.isEnabled = false
        statBtn.isEnabled = false
        binaryBtn.isEnabled = false
        spoonButton.isEnabled = false

        super.onCreate(savedInstanceState)
        mainBtn.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
    }

    public override fun onResume() {
        super.onResume()
        //Вызываем асинхронный загрузчик библиотеки
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback)
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
                binaryBtn.isEnabled = true
                spoonButton.isEnabled = true
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
                binaryBtn.setOnClickListener {
                    currentImage = imgService.toBinary2(originalImage)
                    imageView.setImageBitmap(currentImage)
                }
                originBtn.setOnClickListener {
                    imageView.setImageBitmap(originalImage)
                    currentImage = originalImage
                }
                spoonButton.setOnClickListener {
                    currentImage = imgService.toBinary2(originalImage)
                    imageView.setImageBitmap(currentImage)
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
