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

class ThreadTimelineListAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var threadTimelineListItem: ArrayList<ThreadTimelineListItem>

    override fun getCount(): Int {
        return try {
            threadTimelineListItem.size
        } catch (e: Exception) {
            0
        }
    }

    fun setThreadTimelineList(threadTimelineListItem: ArrayList<ThreadTimelineListItem>) {
        this.threadTimelineListItem = threadTimelineListItem
    }

    override fun getItem(postion: Int): Any {
        return threadTimelineListItem[postion]
    }

    override fun getItemId(postion: Int): Long {
        return threadTimelineListItem[postion].id
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.layout_post_timeline, parent, false)

        view.findViewById<TextView>(R.id.user_id).text = threadTimelineListItem[position].userId
        view.findViewById<TextView>(R.id.user_name).text = threadTimelineListItem[position].userName
        view.findViewById<TextView>(R.id.content).text = threadTimelineListItem[position].content

        if (!threadTimelineListItem[position].icon.isNullOrEmpty()) {
            Picasso.get()
                .load("https://aso.vxx0.com/ore-no-yome/upload/icon/" + threadTimelineListItem[position].icon)
                .into(view.findViewById<ImageView>(R.id.user_icon))
        }

        val image01 = view.findViewById<ImageView>(R.id.image01)
        val image02 = view.findViewById<ImageView>(R.id.image02)

        image01.visibility = View.GONE
        image02.visibility = View.GONE

        if (!threadTimelineListItem[position].image01.isNullOrEmpty()) {
            image01.visibility = View.VISIBLE

            val filePath = "https://aso.vxx0.com/ore-no-yome/upload/post/"

            Picasso.get().load(filePath + threadTimelineListItem[position].image01).into(image01)

            if (!threadTimelineListItem[position].image02.isNullOrEmpty()) {
                val image01RightMargin = image01.layoutParams as ViewGroup.MarginLayoutParams
                image01RightMargin.rightMargin = 5
                image01.layoutParams = image01RightMargin

                val image02LeftMargin = image02.layoutParams as ViewGroup.MarginLayoutParams
                image02LeftMargin.leftMargin = 5
                image02.layoutParams = image02LeftMargin

                image02.visibility = View.VISIBLE

                Picasso.get().load(filePath + threadTimelineListItem[position].image02).into(image02)
            }
        }

        return view
    }
}