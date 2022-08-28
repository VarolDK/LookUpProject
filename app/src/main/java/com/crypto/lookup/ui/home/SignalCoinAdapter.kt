package com.crypto.lookup.ui.home

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.crypto.lookup.R
import kotlinx.android.synthetic.main.home_signal_recycler_row.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class SignalCoinAdapter : RecyclerView.Adapter<SignalCoinAdapter.SignalWH>() {
    var signalCoins = ArrayList<SignalCoin>()

    class SignalWH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalWH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_signal_recycler_row, parent, false)
        return SignalWH(itemView)
    }

    override fun getItemCount(): Int {
        return signalCoins.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SignalWH, position: Int) {
        val signalCoin = signalCoins.get(position)
        holder.itemView.signalCoinName.text =
            signalCoin.symbol.subSequence(0, signalCoin.symbol.length - 4).toString() + " / USDT"
        val df = DecimalFormat("#.##")

        holder.itemView.signalCoinOpenPrice.text = signalCoin.openPrice.toString() + "$"
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyy hh:mm")
        holder.itemView.signalOpenDate.text = simpleDateFormat.format(signalCoin.openDate)


        var isPositive: Boolean;
        if (signalCoin.isOpen) {
            holder.itemView.signalCoinCurrentPrice.text =
                signalCoin.currentPrice.toString() + "$ %" + df.format(((signalCoin.currentPrice - signalCoin.openPrice) / signalCoin.openPrice) * 100)
            isPositive = signalCoin.currentPrice >= signalCoin.openPrice
            holder.itemView.signalCloseDate.visibility = View.GONE
            holder.itemView.isClosed.visibility = View.GONE
        } else {
            holder.itemView.signalCoinCurrentPrice.text =
                signalCoin.closePrice.toString() + "$ %" + df.format(((signalCoin.closePrice!! - signalCoin.openPrice) / signalCoin.openPrice) * 100)
            isPositive = signalCoin.closePrice!! >= signalCoin.openPrice
            holder.itemView.isClosed.visibility = View.VISIBLE
            holder.itemView.signalCloseDate.text = simpleDateFormat.format(signalCoin.closeDate)
            holder.itemView.signalCloseDate.visibility = View.VISIBLE
        }



        holder.itemView.signalCoinCurrentPrice.setTextColor(if (isPositive) Color.GREEN else Color.RED)
        holder.itemView.isClosed.setTextColor(if (isPositive) Color.GREEN else Color.RED)
        holder.itemView.signalImage.setImageDrawable(
            ResourcesCompat.getDrawable(
                holder.itemView.resources,
                if (isPositive) R.drawable.increase else R.drawable.decrease,
                null
            )
        )
    }

}