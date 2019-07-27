package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import com.vxx0.aso.ore_no_yome.apis.ApiPostTask
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")
        title = resources.getString(R.string.sign_up_string_title)
    }

    override fun onResume() {
        super.onResume()

        button_sign_up.setOnClickListener { view ->
            signUp(view)
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
            !Pattern.compile("^[a-zA-Z0-9-_]{4,30}\$").matcher(userId).find() -> {
                text_input_user_id.error = "4文字以上30文字以下の半角英数字で入力してください"
                false
            }
            else -> {
                text_input_user_id.error = null
                true
            }
        }
    }

    private fun validationUserName(): Boolean {
        val userName = text_input_user_name.editText?.text.toString().trim()

        return when {
            userName.isEmpty() -> {
                text_input_user_name.error = "ユーザー名が入力されていません"
                false
            }
            !Pattern.compile("^.{1,30}\$").matcher(userName).find() -> {
                text_input_user_name.error = "最大文字数をオーバーしています"
                false
            }
            else -> {
                text_input_user_name.error = null
                true
            }
        }
    }

    private fun validationEmail(): Boolean {
        val email = text_input_email.editText?.text.toString().trim()

        return when {
            email.isEmpty() -> {
                text_input_email.error = "メールアドレスが入力されていません"
                false
            }
            else -> {
                text_input_email.error = null
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

    private fun signUp(view: View) {
        var check = true
        if (!validationUserId()) check = false
        if (!validationUserName()) check = false
        if (!validationEmail()) check = false
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
                                "VALIDATION_USER_ID" -> text_input_user_id.error = "ユーザーIDの入力規則に違反しています"
                                "VALIDATION_USER_NAME" -> text_input_user_name.error = "ユーザー名の入力規則に違反しています"
                                "VALIDATION_EMAIL" -> text_input_email.error = "メールアドレスの入力規則に違反しています"
                                "VALIDATION_PASSWORD" -> text_input_password.error = "パスワードの入力規則に違反しています"
                                "ALREADY_USER_ID" -> text_input_user_id.error = "入力されたユーザーは既に登録されています"
                                "ALREADY_EMAIL" -> text_input_email.error = "入力されたメールアドレスは既に登録されています"
                                else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                }
            }
        }.execute(
            ApiParams(
                "account/sign-up",
                hashMapOf(
                    "user_id" to text_input_user_id.editText?.text.toString(),
                    "user_name" to text_input_user_name.editText?.text.toString(),
                    "email" to text_input_email.editText?.text.toString(),
                    "password" to text_input_password.editText?.text.toString()
                )
            )
        )
    }

}
