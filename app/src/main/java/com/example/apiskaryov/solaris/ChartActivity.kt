package com.example.apiskaryov.solaris

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint


class ChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val data = intent.getIntArrayExtra("data").toList()
                .mapIndexed { index, i ->
                    DataPoint(index.toDouble(), i.toDouble())
                }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val graph = findViewById(R.id.graph) as GraphView
        val series = BarGraphSeries<DataPoint>(data.toTypedArray())
        graph.addSeries(series)
    }
}

