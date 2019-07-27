package com.vxx0.aso.ore_no_yome.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vxx0.aso.ore_no_yome.R

class CharaListAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater =
        context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var charaListItem:ArrayList<CharaListItem>

    override fun getCount(): Int {
        return try {
            charaListItem.size
        } catch (e: Exception) {
            0
        }
    }

    fun setCharaList(charaListItem: ArrayList<CharaListItem>) {
        this.charaListItem = charaListItem
    }

    override fun getItem(position: Int): Any {
        return charaListItem[position]
    }

    override fun getItemId(position: Int): Long {
        return charaListItem[position].charaId
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.layout_chara_item_list, parent, false)
        view.findViewById<TextView>(R.id.chara_title).text = charaListItem[position].charaTitle
        view.findViewById<TextView>(R.id.anime_title).text = charaListItem[position].animeTitle

        return view
    }
}