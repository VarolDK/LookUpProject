package com.crypto.lookup.ui.dashboard

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.crypto.lookup.R
import com.crypto.lookup.data.User
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onSaveDataListener
import kotlinx.android.synthetic.main.dashboard_coin_recycler_row.view.*
import java.util.*

class SubscribedCoinAdapter(val user: User) : RecyclerView.Adapter<SubscribedCoinAdapter.SubscribedCoinsWH>(),
    Filterable {

    var coinList = ArrayList<Coin>()
    var coinFilterList = java.util.ArrayList<Coin>()
    var userService: UserService = UserService(UserFirebaseDaoImpl())

    class SubscribedCoinsWH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    fun addCoin(coin: Coin) {
        this.coinList.add(coin)
        coinFilterList = coinList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribedCoinsWH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_coin_recycler_row, parent, false)
        return SubscribedCoinsWH(itemView)
    }

    override fun onBindViewHolder(holder: SubscribedCoinsWH, position: Int) {
        val coin = coinFilterList.get(position)
        holder.itemView.coinTextView.text = coin.name.subSequence(0, coin.name.length - 4).toString() + " / USDT"
        holder.itemView.priceTextView.text = coin.price.toString() + "$"
        val dashboardButton: Button = holder.itemView.dashboardButton
        val icon = holder.itemView.context.resources.getDrawable(R.drawable.unsubscribe_heart)
        dashboardButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        holder.itemView.dashboardButton.setOnClickListener {
            userService.unsubscribeCoin(user.email, coin.name, object : onSaveDataListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onSuccess() {
                    coinFilterList.removeAt(position)
                    notifyDataSetChanged()
                }

                override fun onFailed(exception: Exception) {
                    throw exception
                }

            })
        }

        holder.itemView.coinTextView.setOnClickListener{
            val bundle = bundleOf("symbol" to coin.name)
            it.findNavController().navigate(R.id.action_navigation_dashboard_to_candleChartFragment,bundle)
        }

    }

    override fun getItemCount(): Int {
        return coinFilterList.size
    }

    fun setDataList(data: ArrayList<Coin>) {
        this.coinList = data
        coinFilterList = coinList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    coinFilterList = coinList
                } else {
                    val resultList = java.util.ArrayList<Coin>()
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
                coinFilterList = results?.values as java.util.ArrayList<Coin>
                notifyDataSetChanged()
            }

        }
    }
}