package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiImagePostTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.activity_main_profile_edit.*
import kotlinx.android.synthetic.main.content_main_profile_edit.*
import java.io.IOException
import java.util.regex.Pattern

class MainProfileEditActivity : AppCompatActivity() {
    private lateinit var userToken: String
    private val resultRequestPick = 1001
    private var resultPick: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        userToken = pref.getString("token", "") as String
        ApiGetTask {
            if (it == null) {
                Toast.makeText(this, "APIとの通信に失敗しました", Toast.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val data = it.getJSONObject("data")
                        if (!data.getString("icon").isNullOrEmpty()) {
                            val url = "https://aso.vxx0.com/ore-no-yome/upload/icon/"
                            Picasso.get().load(url + data.getString("icon")).into(button_icon_edit)
                        }
                        text_input_profile.editText?.setText(data.getString("profile"), TextView.BufferType.NORMAL)
//                        text_input_user_id.editText?.setText(data.getString("user_id"), TextView.BufferType.NORMAL)
                        text_input_user_name.editText?.setText(data.getString("user_name"), TextView.BufferType.NORMAL)
//                        text_input_email.editText?.setText(data.getString("email"), TextView.BufferType.NORMAL)
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "REQUIRED_PARAM" -> Toast.makeText(this, "必要な値が見つかりませんでした", Toast.LENGTH_SHORT).show()
                                "UNKNOWN_TOKEN" -> Toast.makeText(this, "ユーザートークンが不明です", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(this, "不明なエラーが発生しました", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> Toast.makeText(this, "不明なエラーが発生しました", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute(ApiParams("user/get-my-info", hashMapOf("token" to userToken)))

        button_profile_edit.setOnClickListener { view ->
            profileEdit(view)
        }

        button_icon_edit.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, resultRequestPick)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == RESULT_OK) {
            if (resultData != null) {
                when (requestCode) {
                    resultRequestPick -> {
                        val uri = resultData.data as Uri
                        try {
                            val bitmap: Bitmap = getBitmapFromUri(uri)
                            resultPick = bitmap
                            button_icon_edit.setImageBitmap(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun validationProfile(): Boolean {
        return true
    }

//    private fun validationUserId(): Boolean {
//        val userId = text_input_user_id.editText?.text.toString().trim()
//
//        return when {
//            userId.isEmpty() -> {
//                text_input_user_id.error = "ユーザーIDが入力されていません"
//                false
//            }
//            !Pattern.compile("^[a-zA-Z0-9-_]{4,30}\$").matcher(userId).find() -> {
//                text_input_user_id.error = "4文字以上30文字以下の半角英数字で入力してください"
//                false
//            }
//            else -> {
//                text_input_user_id.error = null
//                true
//            }
//        }
//    }

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

//    private fun validationEmail(): Boolean {
//        val email = text_input_email.editText?.text.toString().trim()
//
//        return when {
//            email.isEmpty() -> {
//                text_input_email.error = "メールアドレスが入力されていません"
//                false
//            }
//            else -> {
//                text_input_email.error = null
//                true
//            }
//        }
//    }
//
//    private fun validationPassword(): Boolean {
//        val password = text_input_password.editText?.text.toString().trim()
//
//        return when {
//            password.isEmpty() -> {
//                text_input_password.error = "パスワードが入力されていません"
//                false
//            }
//            else -> {
//                text_input_password.error = null
//                true
//            }
//        }
//    }

    private fun profileEdit(view: View) {
        ApiImagePostTask {
            if (it == null) {
                Snackbar.make(view, "通信失敗", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "通信成功", Snackbar.LENGTH_SHORT).show()
            }
        }.execute(
            ApiParams(
                "user/edit-my-info",
                hashMapOf(
                    "token" to userToken,
//                    "user_id" to text_input_user_id.editText!!.text.toString(),
                    "user_name" to text_input_user_name.editText!!.text.toString(),
//                    "email" to text_input_email.editText!!.text.toString(),
//                    "password" to text_input_password.editText!!.text.toString(),
                    "profile" to text_input_profile.editText!!.text.toString()
                ),
                resultPick
            )
        )
    }

}
