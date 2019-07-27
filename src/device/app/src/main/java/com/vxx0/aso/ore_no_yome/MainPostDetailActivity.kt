package com.vxx0.aso.ore_no_yome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import com.vxx0.aso.ore_no_yome.apis.ApiPostTask
import kotlinx.android.synthetic.main.activity_main_post_detail.*
import kotlinx.android.synthetic.main.content_main_post_detail.*
import org.jetbrains.anko.startActivity

class MainPostDetailActivity : AppCompatActivity() {
    private var animeId = 0
    private var charaId = 0
    private lateinit var animeTitle: String
    private lateinit var charaName: String
    private lateinit var userId: String
    private lateinit var postId: String
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_post_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")

        title = "読み込み中..."

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        userToken = pref.getString("token", "") as String

        postId = intent.getStringExtra("post_id") as String

        main_content.visibility = View.INVISIBLE
        button_favorite_post.visibility = View.INVISIBLE

        ApiGetTask {
            if (it == null) {
                Toast.makeText(this, "APIとの通信に失敗しました", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val data = it.getJSONObject("data")
                        user_id.text = data.getString("user_id")
                        user_name.text = data.getString("user_name")
                        val userIcon = data.getString("icon")
                        if (!userIcon.isNullOrEmpty()) {
                            Picasso.get().load("https://aso.vxx0.com/ore-no-yome/upload/icon/$userIcon")
                                .into(user_icon)
                        }
                        content.text = data.getString("content")
                        timestamp.text = data.getString("timestamp")

                        anime_title.text = String.format("# %s", data.getString("anime_name"))
                        chara_name.text = String.format("# %s", data.getString("chara_name"))

                        animeId = data.getInt("anime_id")
                        charaId = data.getInt("chara_id")
                        animeTitle = data.getString("anime_name")
                        charaName = data.getString("chara_name")
                        userId = data.getString("user_id")

                        if (data.getInt("favorite_flg") == 1) {
                            button_favorite_post.setImageResource(R.drawable.icon_favorite)
                        } else {
                            button_favorite_post.setImageResource(R.drawable.icon_favorite_solid)
                        }

                        image01.visibility = View.GONE
                        image02.visibility = View.GONE

                        val filePath = "https://aso.vxx0.com/ore-no-yome/upload/post/"
                        val image01FileName = data.getString("image01")
                        val image02FileName = data.getString("image02")

                        if (!image01FileName.isNullOrEmpty()) {
                            Picasso.get().load(filePath + image01FileName).into(image01)
                            image01.visibility = View.VISIBLE

                            image01.setOnClickListener {
                                startActivity<MainPostImageActivity>("file_name" to image01FileName)
                            }

                            if (!image02FileName.isNullOrEmpty()) {
                                Picasso.get().load(filePath + image02FileName).into(image02)
                                image02.visibility = View.VISIBLE

                                val image01RightMargin = image01.layoutParams as ViewGroup.MarginLayoutParams
                                image01RightMargin.rightMargin = 5
                                image01.layoutParams = image01RightMargin

                                val image02LeftMargin = image02.layoutParams as ViewGroup.MarginLayoutParams
                                image02LeftMargin.leftMargin = 5
                                image02.layoutParams = image02LeftMargin

                                image02.setOnClickListener {
                                    startActivity<MainPostImageActivity>("file_name" to image02FileName)
                                }
                            }
                        }


                        title = data.getString("user_name") + "さんの投稿"
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "UNKNOWN_USER" -> {
                                    Toast.makeText(this, "ユーザーIDが見つかりませんでした", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                "UNKNOWN_POST" -> {
                                    Toast.makeText(this, "該当する投稿IDが見つかりませんでした", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(this, "不明なエラーが発生しました", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(this, "不明なエラーが発生しました", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

            loading_background.visibility = View.INVISIBLE

            main_content.visibility = View.VISIBLE
            button_favorite_post.visibility = View.VISIBLE
        }.execute(ApiParams("post/get-post", hashMapOf("post_id" to postId, "token" to userToken)))
    }

    override fun onResume() {
        super.onResume()

        button_favorite_post.setOnClickListener { view ->
            regFavorite(view)
        }

        user_id.setOnClickListener {
            startActivity<MainActivity>("user_id" to userId)
        }

        user_name.setOnClickListener {
            startActivity<MainActivity>("user_id" to userId)
        }

        anime_title.setOnClickListener {
            startActivity<MainThreadActivity>(
                "anime_id" to animeId,
                "anime_title" to animeTitle,
                "chara_id" to 0,
                "chara_name" to ""
            )
        }

        chara_name.setOnClickListener {
            startActivity<MainThreadActivity>(
                "anime_id" to animeId,
                "chara_id" to charaId,
                "anime_title" to animeTitle,
                "chara_name" to charaName
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun regFavorite(view: View) {
        ApiPostTask {
            if (it == null) {
                Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        when (it.getString("data")) {
                            "UNREGISTER" -> {
                                button_favorite_post.setImageResource(R.drawable.icon_favorite_solid)
                                Snackbar.make(view, "お気に入りを解除しました", Snackbar.LENGTH_LONG).show()
                            }
                            "REGISTER" -> {
                                button_favorite_post.setImageResource(R.drawable.icon_favorite)
                                Snackbar.make(view, "お気に入りに追加しました", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "REQUIRED_PARAM" -> Snackbar.make(view, "必要な値が見つかりませんでした", Snackbar.LENGTH_SHORT).show()
                                "UNKNOWN_TOKEN" -> Snackbar.make(view, "ユーザーIDが不明です", Snackbar.LENGTH_SHORT).show()
                                "UNKNOWN_POST" -> Snackbar.make(view, "投稿IDが不明です", Snackbar.LENGTH_SHORT).show()
                                else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
        }.execute(ApiParams("post/reg-favorite", hashMapOf("token" to userToken, "post_id" to postId)))
    }

}
