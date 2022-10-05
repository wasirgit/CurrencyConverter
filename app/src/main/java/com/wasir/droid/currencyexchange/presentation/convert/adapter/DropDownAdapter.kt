package com.wasir.droid.currencyexchange.presentation.convert.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.data.model.DropdownItem

class DropDownAdapter(private val items: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val v = getDropDownView(position, convertView, parent)
        val tv = v.findViewById<TextView>(R.id.dropdownItemTv)
        val end = tv.paddingEnd
        val top = tv.paddingTop
        tv.setPadding(0, top, end, 0)
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View
        val viewHolder: DropDownViewHolder
        if (convertView == null) {
            v = LayoutInflater.from(parent.context)
                .inflate(R.layout.dropdown_item, parent, false)
            viewHolder =
                DropDownViewHolder(
                    v
                )
            v.tag = viewHolder
        } else {
            v = convertView
            viewHolder =
                v.tag as DropDownViewHolder
        }
        viewHolder.textView.text = items[position]

        val end: Int = viewHolder.textView.paddingEnd
        viewHolder.textView.setPadding(end, end, end, end)
        return v
    }

    private class DropDownViewHolder(view: View) {
        var textView: TextView
        init {
            textView = view.findViewById(R.id.dropdownItemTv)
        }
    }
}