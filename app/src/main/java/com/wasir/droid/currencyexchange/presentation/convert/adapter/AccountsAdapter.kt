package com.wasir.droid.currencyexchange.presentation.convert.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.databinding.AccountRvItemBinding
import com.wasir.droid.currencyexchange.common.FormatUtils

class AccountsAdapter(private val mList: ArrayList<Account>, private val formatUtils: FormatUtils) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: AccountRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(account: Account, formatUtils: FormatUtils) {
            binding.myBalanceAmountTv.text =
                formatUtils.formatAmountWithOutSign(account.balance)
            binding.currencyTv.text = account.currencyCode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AccountRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(account = mList[position], formatUtils = formatUtils)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<Account>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

}