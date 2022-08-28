package com.crypto.lookup.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binance.api.client.BinanceApiClientFactory
import com.crypto.lookup.data.Fear
import com.crypto.lookup.data.Tweet
import com.crypto.lookup.data.TweetSent
import com.crypto.lookup.data.listeners.onGetDataListListener
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import kotlin.streams.toList

class HomeViewModel : ViewModel() {
    var signalCoinListData: MutableLiveData<SignalCoinList>
    var signalCoinAdapter: SignalCoinAdapter
    private val factory: BinanceApiClientFactory = BinanceApiClientFactory.newInstance(
        "QaTHifDPd0jcU4NlNwcf8DptOykOJISTtpcLqY5AC3UiKDB3yOGNGmxuhlcmmiN9",
        "P1DENk2ufvuvYHyFbv0iu7AvWFWIYmgRkJjN9YwXr3C0WxjCzmF9KOHlnMbi4fTj"
    )
    private val restClient = factory.newRestClient()
    private val signalCoinService = SignalCoinService(SignalCoinFirebaseDaoImpl())
    var fearIndex: MutableLiveData<Fear>
    var tweetsBTC: MutableLiveData<ArrayList<Tweet>>
    var tweetsETH: MutableLiveData<ArrayList<Tweet>>
    var tweetsBTCDaily: MutableLiveData<Tweet>
    var tweetsETHDaily: MutableLiveData<Tweet>

    var tweetSentBTCDaily: MutableLiveData<TweetSent>
    var tweetSentETHDaily: MutableLiveData<TweetSent>

    init {
        signalCoinListData = MutableLiveData()
        signalCoinAdapter = SignalCoinAdapter()
        fearIndex = MutableLiveData()
        tweetsBTC = MutableLiveData()
        tweetsETH = MutableLiveData()
        tweetsBTCDaily = MutableLiveData()
        tweetsETHDaily = MutableLiveData()
        tweetSentBTCDaily = MutableLiveData()
        tweetSentETHDaily = MutableLiveData()
    }

    fun fakeData() {
        signalCoinService.fakeData() // TODO DELETE
    }


    fun setAdapterData(data: SignalCoinList) {
        signalCoinAdapter.signalCoins = data.signalCoins
        signalCoinAdapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun dataUpdate(timeInterval: Long, data: ArrayList<SignalCoin>, listener: onGetNoDataListener): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                data.stream().filter { it.isOpen }.forEach {
                    val coin = restClient.get24HrPriceStatistics(it.symbol)
                    it.currentPrice = coin.lastPrice.toFloat()
                    signalCoinListData.postValue(SignalCoinList(data))
                    listener.onSuccess()
                }
                data.sortByDescending { it.isOpen }
                signalCoinListData.postValue(SignalCoinList(data))
                delay(timeInterval)
            }
        }
    }

    fun initFearIndex(): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            val response = URL("https://api.alternative.me/fng/").readText()
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            var fear = Fear(
                jsonObject.getJSONArray("data").getJSONObject(0).getInt("value"),
                jsonObject.getJSONArray("data").getJSONObject(0).getString("value_classification")
            )
            fearIndex.postValue(fear)

        }
    }

    fun initTweetCount() {
        signalCoinService.retrieveTweet(object : onGetDataListListener {
            override fun onSuccess(data: List<DocumentSnapshot>) {
                val tempBTC = arrayListOf<Tweet>()
                val tempETH = arrayListOf<Tweet>()
                data.forEach {
                    val tweet = it.toObject(Tweet::class.java)
                    if (tweet!!.symbol == "BTCUSDT") tempBTC.add(tweet)
                    if (tweet.symbol == "ETHUSDT") tempETH.add(tweet)
                }
                tweetsBTC.postValue(tempBTC)
                tweetsETH.postValue(tempETH)
                initDailyTweetCount()
            }

            override fun onFailed(e: Exception) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initDailyTweetCount() {
        signalCoinService.retrieveTweetDaily(object : onGetDataListListener {
            override fun onSuccess(data: List<DocumentSnapshot>) {
                var tempBTC = Tweet()
                var tempETH = Tweet()
                data.forEach {
                    val tweet = it.toObject(Tweet::class.java)
                    if (tweet!!.symbol == "BTCUSDT") tempBTC = tweet
                    if (tweet.symbol == "ETHUSDT") tempETH = tweet
                }
                tweetsBTCDaily.postValue(tempBTC)
                tweetsETHDaily.postValue(tempETH)

            }

            override fun onFailed(e: Exception) {
                TODO("Not yet implemented")
            }

        })
    }

    fun initDailyTweetSentCount() {
        signalCoinService.retrieveTweetSentDaily(object : onGetDataListListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSuccess(data: List<DocumentSnapshot>) {
                var tempBTC = TweetSent()
                var tempETH = TweetSent()
                val dataTweetSent =
                    data.stream().map { it.toObject(TweetSent::class.java) }.toList().sortedByDescending { it?.end }
                for (i in 0..1) {
                    if (dataTweetSent.get(i)!!.symbol == "BTCUSDT") tempBTC = dataTweetSent.get(i)!!
                    if (dataTweetSent.get(i)!!.symbol == "ETHUSDT") tempETH = dataTweetSent.get(i)!!
                }
                tweetSentBTCDaily.postValue(tempBTC)
                tweetSentETHDaily.postValue(tempETH)
                initTweetCount()
            }

            override fun onFailed(e: Exception) {
                TODO("Not yet implemented")
            }

        })
    }

}