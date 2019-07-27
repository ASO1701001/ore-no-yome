package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.activity_title.*
import org.jetbrains.anko.startActivity

class TitleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        button_sign_up.visibility = View.INVISIBLE
        button_sign_in.visibility = View.INVISIBLE
        logo_image.visibility = View.INVISIBLE
        auto_sign_in_dialog.visibility = View.INVISIBLE

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 3000
        alphaAnimation.fillAfter = true
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                val pref = getSharedPreferences("data", MODE_PRIVATE)
                val token = pref.getString("token", "")
                if (!token.isNullOrEmpty()) {
                    val animation = AlphaAnimation(0.0f, 1.0f)
                    animation.duration = 500
                    animation.fillAfter = true
                    animation.setAnimationListener(object : Animation.AnimationListener {

                        override fun onAnimationRepeat(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {}

                        override fun onAnimationStart(p0: Animation?) {
                            verifyToken(root_layout, token)
                        }

                    })
                    auto_sign_in_dialog.startAnimation(animation)
                } else {
                    val animation = AlphaAnimation(0.0f, 1.0f)
                    animation.duration = 500
                    animation.fillAfter = true

                    button_sign_up.startAnimation(animation)
                    button_sign_in.startAnimation(animation)
                }
            }

        })
        logo_image.startAnimation(alphaAnimation)
    }


    override fun onResume() {
        super.onResume()

        button_sign_up.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        button_sign_in.setOnClickListener {
            startActivity<SignInActivity>()
        }
    }

    fun verifyToken(view: View, token: String) {
        ApiGetTask {
            if (it == null) {
                Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        startActivity(
                            Intent(
                                this, MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "REQUIRED_PARAM" -> Snackbar.make(view, "必要な値が見つかりませんでした", Snackbar.LENGTH_SHORT).show()
                                "UNKNOWN_TOKEN" -> Snackbar.make(view, "トークンの有効期限が切れました", Snackbar.LENGTH_SHORT).show()
                                else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        val pref = getSharedPreferences("data", MODE_PRIVATE)
                        pref.edit().clear().apply()
                        auto_sign_in_dialog.visibility = View.GONE
                        button_sign_up.visibility = View.VISIBLE
                        button_sign_in.visibility = View.VISIBLE
                        auto_sign_in_dialog.clearAnimation()
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
        }.execute(ApiParams("token/verify-token", hashMapOf("token" to token)))
    }

}
