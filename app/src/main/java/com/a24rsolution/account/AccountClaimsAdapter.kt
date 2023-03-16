package com.a24rsolution.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a24rsolution.R

class AccountClaimsAdapter(private val claimsList: ArrayList<Claims>): RecyclerView.Adapter<AccountClaimsAdapter.ClaimsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClaimsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_claim, parent, false)
        return ClaimsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClaimsViewHolder, position: Int) {
        val currentItem = claimsList[position]
        holder.claimAmountText.text = currentItem.claimAmount
        holder.claimTimeText.text = currentItem.claimDate.toString()
        holder.upiText.text = currentItem.upiID
    }

    override fun getItemCount(): Int = claimsList.size

    class ClaimsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val claimAmountText:TextView = itemView.findViewById(R.id.claimsAmountTextView)
        val upiText:TextView = itemView.findViewById(R.id.claimsUpiIDTextView)
        val claimTimeText: TextView= itemView.findViewById(R.id.claimsTimestampTextView)

    }
}