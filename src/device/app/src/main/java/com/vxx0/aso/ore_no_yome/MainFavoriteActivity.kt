package com.vxx0.aso.ore_no_yome

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.adapters.PostTimelineListAdapter
import com.vxx0.aso.ore_no_yome.adapters.PostTimelineListItem
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.activity_main_favorite.*
import kotlinx.android.synthetic.main.content_main_favorite.*
import org.jetbrains.anko.startActivity

class MainFavoriteActivity : AppCompatActivity(),
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_favorite)
        setSupportActionBar(toolbar)

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener)
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)

        setTimeline(root_layout)
    }

    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        setTimeline(root_layout)
    }

    override fun onRefresh() {
        setTimeline(root_layout)
    }

    private fun setTimeline(view: View) {
        val pref = getSharedPreferences("data", MODE_PRIVATE)
        val userToken = pref.getString("token", "") as String
        ApiGetTask {
            if (it == null) {
                Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val userName = it.getJSONObject("data").getString("user_name")
                        title = userName
                        val timelineData = it.getJSONObject("data").getJSONArray("favorite")
                        val list = ArrayList<PostTimelineListItem>()
                        for (i in 0 until timelineData.length()) {
                            val postTimelineListItem = PostTimelineListItem()
                            val item = timelineData.getJSONObject(i)
                            postTimelineListItem.id = i.toLong()
                            postTimelineListItem.userId = item.getString("user_id")
                            postTimelineListItem.userName = item.getString("user_name")
                            postTimelineListItem.icon = item.getString("icon")
                            postTimelineListItem.postId = item.getString("post_id")
                            postTimelineListItem.content = item.getString("content")
                            postTimelineListItem.image01 = item.getString("image01")
                            postTimelineListItem.image02 = item.getString("image02")
                            list.add(postTimelineListItem)
                        }
                        val listView = timeline
                        val postTimelineListAdapter = PostTimelineListAdapter(this)
                        postTimelineListAdapter.setPostTimelineList(list)
                        postTimelineListAdapter.notifyDataSetChanged()
                        listView.adapter = postTimelineListAdapter
                        listView.setOnItemClickListener { adapterView, _, i, _ ->
                            val item = adapterView.getItemAtPosition(i) as PostTimelineListItem
                            startActivity<MainPostDetailActivity>("post_id" to item.postId)
                        }
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "REQUIRED_PARAM" -> Snackbar.make(
                                    view,
                                    "必要な値が見つかりませんでした",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                "UNKNOWN_USER" -> Snackbar.make(
                                    view,
                                    "ユーザーが見つかりませんでした",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                else -> Snackbar.make(
                                    view,
                                    "必要な値が見つかりませんでした",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
            mSwipeRefreshLayout.isRefreshing = false
        }.execute(ApiParams("user/get-my-favorite", hashMapOf("token" to userToken)))
    }
}
