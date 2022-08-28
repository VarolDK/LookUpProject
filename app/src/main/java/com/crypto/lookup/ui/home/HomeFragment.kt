package com.crypto.lookup.ui.home

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.lookup.R
import com.crypto.lookup.data.listeners.onGetDataListListener
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.crypto.lookup.databinding.FragmentHomeBinding
import com.crypto.lookup.ui.login.UserViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: UserViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var signalCoinService: SignalCoinService
    private var dataUpdate: Job? = null
    private var updatedSignals = 0L
    private var signalSize = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        signalCoinService = SignalCoinService(SignalCoinFirebaseDaoImpl())
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeProgressBar.visibility = View.VISIBLE
        binding.signalScrollView.visibility = View.INVISIBLE
        binding.homeInfoLayout.visibility = View.INVISIBLE


        val layoutManagerSignalCoin = LinearLayoutManager(context)
        binding.signalRecylerView.layoutManager = layoutManagerSignalCoin
        binding.signalRecylerView.adapter = homeViewModel.signalCoinAdapter
        initData()
        homeViewModel.initFearIndex()
        homeViewModel.initDailyTweetSentCount()


        homeViewModel.tweetsBTCDaily.observe(viewLifecycleOwner) {
            if (homeViewModel.tweetsBTC.value!!.size > 0) {
                val average = homeViewModel.tweetsBTC.value!!.stream().mapToInt { it.tweet_count }
                    .sum() / homeViewModel.tweetsBTC.value!!.size
                val perc = (it.tweet_count - average) / average.toFloat() * 100
                val df = DecimalFormat("#.##")
                val positive = homeViewModel.tweetSentBTCDaily.value!!.positive
                val negative = homeViewModel.tweetSentBTCDaily.value!!.negative
                val neutral = homeViewModel.tweetSentBTCDaily.value!!.neutral
                var tvText = "Bitcoin reaction is "

                if (perc < 0) tvText += "decrease %" + df.format(perc * -1)
                else tvText += "increase %" + df.format(perc)

                var isRed = false
                if (positive > negative && positive > neutral) {
                    tvText += " and positive"
                } else if (negative >= positive && negative >= neutral) {
                    tvText += " and negative"
                    isRed = true
                } else {
                    tvText += " and neutral"
                }

                binding.tweetCountBTC.text = tvText
                binding.tweetCountBTC.setTextColor(if (!isRed) Color.GREEN else Color.RED)
            }
        }
        homeViewModel.tweetsETHDaily.observe(viewLifecycleOwner) {
            if (homeViewModel.tweetsETH.value!!.size > 0) {
                val average = homeViewModel.tweetsETH.value!!.stream().mapToInt { it.tweet_count }
                    .sum() / homeViewModel.tweetsETH.value!!.size
                val perc = (homeViewModel.tweetsETHDaily.value!!.tweet_count - average) / average.toFloat() * 100
                val df = DecimalFormat("#.##")

                val positive = homeViewModel.tweetSentETHDaily.value!!.positive
                val negative = homeViewModel.tweetSentETHDaily.value!!.negative
                val neutral = homeViewModel.tweetSentETHDaily.value!!.neutral
                var tvText = "Ethereum reaction is "

                if (perc < 0) tvText += "decrease %" + df.format(perc * -1)
                else tvText += "increase %" + df.format(perc)

                var isRed = false
                if (positive > negative && positive > neutral) {
                    tvText += " and positive"
                } else if (negative >= positive && negative >= neutral) {
                    tvText += " and negative"
                    isRed = true
                } else {
                    tvText += " and neutral"
                }

                binding.tweetCountETH.text = tvText
                binding.tweetCountETH.setTextColor(if (!isRed) Color.GREEN else Color.RED)
            }
        }

        homeViewModel.fearIndex.observe(viewLifecycleOwner) {
            var text = it.index.toString() + " " + it.classification
            text += if (it.index < 40) " - " + getText(R.string.buyOpportunity) else " - " + getText(R.string.sellOpportunity)
            binding.fearIndex.text = text
        }

        homeViewModel.signalCoinListData.observe(viewLifecycleOwner) { data ->
            homeViewModel.setAdapterData(data)
            val df = DecimalFormat("#.##")
            var currentSum: Float = 0F
            var closedSum: Float = 0F
            data.signalCoins.forEach {
                if (it.isOpen) {
                    currentSum += (it.currentPrice - it.openPrice) / it.openPrice * 100
                } else {
                    closedSum += (it.closePrice!! - it.openPrice) / it.openPrice * 100
                }
            }
            binding.totalCurrentProfit.text = "%" + df.format(currentSum)
            binding.totalCurrentProfit.setTextColor(if (currentSum >= 0) Color.GREEN else Color.RED)
            binding.totalProfit.text = "%" + df.format(closedSum)
            binding.totalProfit.setTextColor(if (closedSum > 0) Color.GREEN else Color.RED)


            if (data.signalCoins.size > 0) {
                binding.signalScrollView.visibility = View.VISIBLE
                binding.homeInfoLayout.visibility = View.VISIBLE
                binding.signalNoCoin.visibility = View.GONE
            } else {
                binding.signalNoCoin.visibility = View.VISIBLE
            }
            binding.homeProgressBar.visibility = View.GONE

            binding.totalCurrentProfit.visibility = View.GONE
            binding.liveDataPB.visibility = View.VISIBLE
            if (signalSize != 0L) binding.liveDataPB.setProgress(((updatedSignals * 100) / signalSize).toInt(), true)
            if (updatedSignals >= signalSize) {
                binding.liveDataPB.visibility = View.GONE
                binding.totalCurrentProfit.visibility = View.VISIBLE
                updatedSignals = 0L
                signalSize = 0L
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    fun initData() {
        signalCoinService.retrieve(sharedViewModel.getCurrentUser().subscribedCoins, object : onGetDataListListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSuccess(data: List<DocumentSnapshot>) {
                val coinData = ArrayList<SignalCoin>()
                data.forEach {
                    if (data.isNotEmpty()) coinData.add(it.toObject(SignalCoin::class.java)!!)
                }
                signalSize = coinData.stream().filter { it.isOpen }.count()


                binding.totalOpenPosition.text = coinData.stream().filter { it.isOpen == true }.count().toString()
                binding.totalClosedPosition.text = coinData.stream().filter { it.isOpen == false }.count().toString()



                dataUpdate = homeViewModel.dataUpdate(5000, coinData, object : onGetNoDataListener {
                    override fun onSuccess() {
                        updatedSignals++
                    }

                    override fun onFailed(e: Exception) {
                        TODO("Not yet implemented")
                    }

                })


            }

            override fun onFailed(e: Exception) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dataUpdate?.cancel()
    }
}