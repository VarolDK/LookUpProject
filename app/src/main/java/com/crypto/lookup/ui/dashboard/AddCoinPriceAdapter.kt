package com.crypto.lookup.ui.dashboard

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.crypto.lookup.R
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.crypto.lookup.ui.login.UserViewModel
import kotlinx.android.synthetic.main.dashboard_coin_recycler_row.view.*
import java.util.*


class AddCoinPriceAdapter(val subscribedCoinAdapter: SubscribedCoinAdapter, val userModeViewModel: UserViewModel) :
    RecyclerView.Adapter<AddCoinPriceAdapter.CoinsWH>(), Filterable {
    var coinList = ArrayList<Coin>()
    var coinFilterList = ArrayList<Coin>()
    private val userService: UserService = UserService(UserFirebaseDaoImpl())


    class CoinsWH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    fun setDataList(data: ArrayList<Coin>) {
        this.coinList = data
        coinFilterList = coinList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsWH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_coin_recycler_row, parent, false)
        return CoinsWH(itemView)
    }

    override fun getItemCount(): Int {
        return coinFilterList.size
    }

    override fun onBindViewHolder(holder: CoinsWH, position: Int) {
        val coin = coinFilterList.get(position)
        holder.itemView.coinTextView.text = coin.name.subSequence(0, coin.name.length - 4).toString() + " / USDT"
        holder.itemView.priceTextView.text = coin.price.toString() + "$"
        val icon = holder.itemView.context.resources.getDrawable(R.drawable.subscribe_heart)
        val dashboardButton = holder.itemView.dashboardButton
        dashboardButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        dashboardButton.setOnClickListener {
            if (userModeViewModel.getCurrentUser().subscribedCoins.size >= 10) {
                Toast.makeText(
                    holder.itemView.context,
                    holder.itemView.context.getText(R.string.cannotSelectCoin),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                userService.subscribeCoin(
                    userModeViewModel.getCurrentUser().email,
                    coin.name,
                    object : onSaveDataListener {
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun onSuccess() {
                            userModeViewModel.addCoin(coin)
                            coinList.remove(coinList.get(position))
                            coinFilterList.remove(coin)
                            subscribedCoinAdapter.addCoin(coin)
                            subscribedCoinAdapter.notifyDataSetChanged()
                            notifyDataSetChanged()
                        }

                        override fun onFailed(exception: Exception) {
                            TODO("Not yet implemented")
                        }

                    })
            }
        }

        holder.itemView.coinTextView.setOnClickListener{
            val bundle = bundleOf("symbol" to coin.name)
            it.findNavController().navigate(R.id.action_navigation_dashboard_to_candleChartFragment,bundle)
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    coinFilterList = coinList
                } else {
                    val resultList = ArrayList<Coin>()
                    for (row in coinList) {
                        if (row.name.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    coinFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coinFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coinFilterList = results?.values as ArrayList<Coin>
                notifyDataSetChanged()
            }

        }
    }

}
