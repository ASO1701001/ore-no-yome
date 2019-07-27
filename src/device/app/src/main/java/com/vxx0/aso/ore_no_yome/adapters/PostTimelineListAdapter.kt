package com.vxx0.aso.ore_no_yome.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.R

class PostTimelineListAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater: LayoutInflater =
        context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var postTimelineListItem: ArrayList<PostTimelineListItem>

    override fun getCount(): Int {
        return try {
            postTimelineListItem.size
        } catch (e: Exception) {
            0
        }
    }

    fun setPostTimelineList(postTimelineListItem: ArrayList<PostTimelineListItem>) {
        this.postTimelineListItem = postTimelineListItem
    }

    override fun getItem(position: Int): Any {
        return postTimelineListItem[position]
    }

    override fun getItemId(position: Int): Long {
        return postTimelineListItem[position].id
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.layout_post_timeline, parent, false)

        view.findViewById<TextView>(R.id.user_id).text = postTimelineListItem[position].userId
        view.findViewById<TextView>(R.id.user_name).text = postTimelineListItem[position].userName
        view.findViewById<TextView>(R.id.content).text = postTimelineListItem[position].content

        if (!postTimelineListItem[position].icon.isNullOrEmpty()) {
            Picasso.get().load("https://aso.vxx0.com/ore-no-yome/upload/icon/" + postTimelineListItem[position].icon)
                .into(view.findViewById<ImageView>(R.id.user_icon))
        }

        val image01 = view.findViewById<ImageView>(R.id.image01)
        val image02 = view.findViewById<ImageView>(R.id.image02)

        image01.visibility = View.GONE
        image02.visibility = View.GONE

        if (!postTimelineListItem[position].image01.isNullOrEmpty()) {
            image01.visibility = View.VISIBLE

            val filePath = "https://aso.vxx0.com/ore-no-yome/upload/post/"

            Picasso.get().load(filePath + postTimelineListItem[position].image01).into(image01)

            if (!postTimelineListItem[position].image02.isNullOrEmpty()) {
                val image01RightMargin = image01.layoutParams as MarginLayoutParams
                image01RightMargin.rightMargin = 5
                image01.layoutParams = image01RightMargin

                val image02LeftMargin = image02.layoutParams as MarginLayoutParams
                image02LeftMargin.leftMargin = 5
                image02.layoutParams = image02LeftMargin

                image02.visibility = View.VISIBLE

                Picasso.get().load(filePath + postTimelineListItem[position].image02).into(image02)
            }
        }

        return view
    }
}