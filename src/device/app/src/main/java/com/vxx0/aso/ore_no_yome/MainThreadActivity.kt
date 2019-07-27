package com.vxx0.aso.ore_no_yome

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.adapters.ThreadTimelineListAdapter
import com.vxx0.aso.ore_no_yome.adapters.ThreadTimelineListItem
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.activity_main_thread.*
//import kotlinx.android.synthetic.main.content_main.*
//import kotlinx.android.synthetic.main.content_main.root_layout
import kotlinx.android.synthetic.main.content_main_thread.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class MainThreadActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var animeId: Int = 0
    private var charaId: Int = 0
    private lateinit var animeTitle: String
    private lateinit var charaName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_thread)
        setSupportActionBar(toolbar)

        title = "スレッド"

        animeId = intent.getIntExtra("anime_id", 0)
        charaId = intent.getIntExtra("chara_id", 0)
        animeTitle = intent.getStringExtra("anime_title") as String
        charaName = intent.getStringExtra("chara_name") as String

        if (animeId == 0) {
            Toast.makeText(this, "不明なIDです", Toast.LENGTH_LONG).show()
            finish()
        }
        if (animeTitle == "" || (charaId != 0 && charaName == "")) {
            Toast.makeText(this, "取得に失敗しました", Toast.LENGTH_LONG).show()
            finish()
        }

        title = if (charaId == 0) {
            "$animeTitle のスレッド"
        } else {
            "$charaName のスレッド"
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener)
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    }

    override fun onResume() {
        super.onResume()

        button_post_add.setOnClickListener {
            startActivity<MainThreadAddActivity>("anime_id" to animeId, "chara_id" to charaId)
        }

        getThreadSelect()
        /*
        if (charaId == 0) {
            ApiGetTask {
                getThread(root_layout, it)
            }.execute(ApiParams(this, "post/get-thread-anime", hashMapOf("anime_id" to animeId.toString())))
        } else {
            ApiGetTask {
                getThread(root_layout, it)
            }.execute(ApiParams(this, "post/get-thread-chara", hashMapOf("chara_id" to charaId.toString())))
        }
        */
    }

    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        getThreadSelect()
    }

    override fun onRefresh() {
        getThreadSelect()

        Snackbar.make(root_layout, "スレッドを更新しました", Snackbar.LENGTH_SHORT).show()
    }

    private fun getThreadSelect() {
        if (charaId == 0) {
            ApiGetTask {
                getThread(root_layout, it)
            }.execute(ApiParams("post/get-thread-anime", hashMapOf("anime_id" to animeId.toString())))
        } else {
            ApiGetTask {
                getThread(root_layout, it)
            }.execute(ApiParams("post/get-thread-chara", hashMapOf("chara_id" to charaId.toString())))
        }
    }

    private fun getThread(view: View, data: JSONObject?) {
        if (data == null) {
            Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
        } else {
            when (data.getString("status")) {
                "S00" -> {
                    val timelineData = data.getJSONArray("data")
                    val list = ArrayList<ThreadTimelineListItem>()
                    for (i in 0 until timelineData.length()) {
                        val threadTimelineListItem = ThreadTimelineListItem()
                        val item = timelineData.getJSONObject(i)
                        threadTimelineListItem.id = i.toLong()
                        threadTimelineListItem.userId = item.getString("user_id")
                        threadTimelineListItem.userName = item.getString("user_name")
                        threadTimelineListItem.postId = item.getString("post_id")
                        threadTimelineListItem.icon = item.getString("icon")
                        threadTimelineListItem.content = item.getString("content")
                        threadTimelineListItem.wayCP = item.getString("way")
                        threadTimelineListItem.image01 = item.getString("image01")
                        threadTimelineListItem.image02 = item.getString("image02")
                        list.add(threadTimelineListItem)
                    }

                    val listView = timeline
                    val threadTimelineListAdapter = ThreadTimelineListAdapter(this)
                    threadTimelineListAdapter.setThreadTimelineList(list)
                    threadTimelineListAdapter.notifyDataSetChanged()
                    listView.adapter = threadTimelineListAdapter

                    listView.setOnItemClickListener { adapterView, _, i, _ ->
                        val item = adapterView.getItemAtPosition(i) as ThreadTimelineListItem
                        when (item.wayCP) {
                            "p" -> startActivity<MainPostDetailActivity>("post_id" to item.postId)
                            "c" -> Snackbar.make(view, "この投稿はスレッドに対するコメントのため開くことができません", Snackbar.LENGTH_SHORT).show()
                            else -> Snackbar.make(view, "StartActivity did not work.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
            }
        }
        mSwipeRefreshLayout.isRefreshing = false
    }

}
