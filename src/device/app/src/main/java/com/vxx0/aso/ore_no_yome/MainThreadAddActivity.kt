package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import com.vxx0.aso.ore_no_yome.apis.ApiPostTask
import kotlinx.android.synthetic.main.activity_main_thread_add.*
import kotlinx.android.synthetic.main.content_main_post_add.*

class MainThreadAddActivity : AppCompatActivity() {
    private var animeId: Int = 0
    private var charaId: Int = 0
    private var userToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_thread_add)
        setSupportActionBar(toolbar)

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        userToken = pref.getString("token", "")
        if (userToken.isNullOrEmpty()) {
            startActivity(
                Intent(
                    this, MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        animeId = intent.getIntExtra("anime_id", 0)
        charaId = intent.getIntExtra("chara_id", 0)

        if (animeId == 0) {
            Toast.makeText(this, "不明なIDです", Toast.LENGTH_LONG).show()

            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        button_post_add.setOnClickListener {
            if (!text_input_post_content.text.isNullOrEmpty() && !userToken.isNullOrEmpty()) {
                ApiPostTask {
                    Toast.makeText(this, "投稿しました", Toast.LENGTH_SHORT).show()
                    finish()
                }.execute(
                    ApiParams(
                        "post/add-thread",
                        hashMapOf(
                            "token" to userToken.toString(),
                            "content" to text_input_post_content.text.toString(),
                            "anime_id" to animeId.toString(),
                            "chara_id" to charaId.toString()
                        )
                    )
                )
            }
        }
    }

}
