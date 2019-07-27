package com.vxx0.aso.ore_no_yome

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.adapters.PostTimelineListAdapter
import com.vxx0.aso.ore_no_yome.adapters.PostTimelineListItem
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.fragment_main_profile.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainProfileFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()

        setProfile(root_layout)

        button_profile_edit.setOnClickListener {
            val intent = Intent(activity, MainProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_profile, container, false)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener)
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)

        return view
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        setProfile(root_layout)
    }

    override fun onRefresh() {
        setProfile(root_layout)
    }

    private fun setProfile(view: View) {
        val pref = activity!!.getSharedPreferences("data", MODE_PRIVATE)
        val userToken = pref.getString("token", "") as String

        ApiGetTask {
            if (it == null) {
                Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val profileData = it.getJSONObject("data").getJSONObject("profile")
                        if (!profileData.getString("icon").isNullOrEmpty()) {
                            Picasso.get().load("https://aso.vxx0.com/ore-no-yome/upload/icon/" + profileData.getString("icon"))
                                .into(user_icon)
                        }
                        user_name.text = profileData.getString("user_name")
                        user_id.text = profileData.getString("user_id")
                        profile.text = profileData.getString("profile")

                        val timelineData = it.getJSONObject("data").getJSONArray("post")
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
                        val postTimelineListAdapter = PostTimelineListAdapter(activity!!)
                        postTimelineListAdapter.setPostTimelineList(list)
                        postTimelineListAdapter.notifyDataSetChanged()
                        listView.adapter = postTimelineListAdapter
                        listView.setOnItemClickListener { adapterView, _, i, _ ->
                            val item = adapterView.getItemAtPosition(i) as PostTimelineListItem
                            val intent = Intent(activity, MainPostDetailActivity::class.java).apply {
                                putExtra("post_id", item.postId)
                            }
                            startActivity(intent)
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
                                "UNKNOWN_TOKEN" -> Snackbar.make(
                                    view,
                                    "ログイントークンが見つかりませんでした",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                else -> Snackbar.make(
                                    view,
                                    "不明なエラーが発生しました",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
            mSwipeRefreshLayout.isRefreshing = false
        }.execute(ApiParams("user/get-my-profile", hashMapOf("token" to userToken)))
    }
}
