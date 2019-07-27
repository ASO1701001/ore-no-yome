package com.vxx0.aso.ore_no_yome.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.R

class NoticeListAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater =
        context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var noticeListItem:ArrayList<NoticeListItem>

    override fun getCount(): Int {
        return try {
            noticeListItem.size
        } catch (e: Exception) {
            0
        }
    }

    fun setNoticeList(noticeListItem: ArrayList<NoticeListItem>) {
        this.noticeListItem = noticeListItem
    }

    override fun getItem(position: Int): Any {
        return noticeListItem[position]
    }

    override fun getItemId(position: Int): Long {
        return noticeListItem[position].id
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.layout_notice_item_list, parent, false)
        view.findViewById<TextView>(R.id.user_name).text = String.format("%sさんがお気に入り登録しました", noticeListItem[position].userName)
        view.findViewById<TextView>(R.id.content).text = noticeListItem[position].content
        if (!noticeListItem[position].icon.isNullOrEmpty()) {
            Picasso.get().load("https://aso.vxx0.com/ore-no-yome/upload/icon/" + noticeListItem[position].icon)
                .into(view.findViewById<ImageView>(R.id.user_icon))
        }

        return view
    }
}