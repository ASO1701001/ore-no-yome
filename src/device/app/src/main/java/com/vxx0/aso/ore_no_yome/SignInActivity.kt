package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import com.vxx0.aso.ore_no_yome.apis.ApiPostTask
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")
        title = resources.getString(R.string.sign_in_string_title)
    }

    override fun onResume() {
        super.onResume()

        button_sign_in.setOnClickListener { view ->
            signIn(view)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun validationUserId(): Boolean {
        val userId = text_input_user_id.editText?.text.toString().trim()

        return when {
            userId.isEmpty() -> {
                text_input_user_id.error = "ユーザーIDが入力されていません"
                false
            }
            else -> {
                text_input_user_id.error = null
                true
            }
        }
    }

    private fun validationPassword(): Boolean {
        val password = text_input_password.editText?.text.toString().trim()

        return when {
            password.isEmpty() -> {
                text_input_password.error = "パスワードが入力されていません"
                false
            }
            else -> {
                text_input_password.error = null
                true
            }
        }
    }

    private fun signIn(view: View) {
        var check = true
        if (!validationUserId()) check = false
        if (!validationPassword()) check = false

        if (!check) return

        ApiPostTask {
            if (it == null) {
                Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val token = it.getJSONObject("data").getString("token")
                        val editor = getSharedPreferences("data", MODE_PRIVATE).edit()
                        editor.putString("token", token).apply()
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
                                "UNKNOWN_USER" -> Snackbar.make(view, "不明なユーザーです", Snackbar.LENGTH_SHORT).show()
                                else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
        }.execute(
            ApiParams(
                "account/sign-in",
                hashMapOf(
                    "user_id" to text_input_user_id.editText?.text.toString(),
                    "password" to text_input_password.editText?.text.toString()
                )
            )
        )
    }

}
