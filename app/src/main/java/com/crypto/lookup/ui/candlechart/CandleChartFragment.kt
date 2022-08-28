package com.crypto.lookup.ui.candlechart

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crypto.lookup.databinding.FragmentCandlechartBinding
import com.crypto.lookup.ui.login.UserViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet


class CandleChartFragment : Fragment() {

    private lateinit var candleChartViewModel: CandleChartViewModel
    private lateinit var sharedViewModel: UserViewModel
    private var _binding: FragmentCandlechartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCandlechartBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        candleChartViewModel = ViewModelProvider(this).get(CandleChartViewModel::class.java)

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val symbol = arguments?.getString("symbol")
        candleChartViewModel.loadPresentData(symbol)
        val candleStickChart: CandleStickChart = binding.candleStickChart
        candleStickChart.cameraDistance = 100F
        candleStickChart.axisRight.isEnabled = false
        candleStickChart.setBackgroundColor(Color.parseColor("#05053D"))
        candleStickChart.description.isEnabled = false
        candleStickChart.legend.textColor = Color.WHITE
        candleStickChart.xAxis.textColor = Color.WHITE
        candleStickChart.axisLeft.textColor = Color.WHITE
        var zoomIn = true
        candleChartViewModel.getData().observe(this, Observer {
            val candleDataSet = CandleDataSet(it, symbol)
            val candleData = CandleData(candleDataSet)
            candleStickChart.data = candleData;
            candleDataSet.axisDependency = YAxis.AxisDependency.LEFT
            candleDataSet.shadowColor = Color.GRAY
            candleDataSet.shadowWidth = 0.5f
            candleDataSet.decreasingColor = Color.RED
            candleDataSet.increasingColor = Color.GREEN
            candleDataSet.neutralColor = Color.BLUE
            candleDataSet.increasingPaintStyle = Paint.Style.FILL
            candleDataSet.decreasingPaintStyle = Paint.Style.FILL
            candleDataSet.valueTextColor = Color.WHITE
            candleDataSet.valueTextSize = 9F
            candleStickChart.invalidate()
            binding.homePB.isVisible = false
            binding.candleStickChart.isVisible = true
            if (zoomIn) candleStickChart.moveViewToX(candleStickChart.xChartMax)
            zoomIn = false
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}