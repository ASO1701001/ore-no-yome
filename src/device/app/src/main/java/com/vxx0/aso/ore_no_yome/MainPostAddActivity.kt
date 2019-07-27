package com.vxx0.aso.ore_no_yome

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vxx0.aso.ore_no_yome.adapters.CharaListAdapter
import com.vxx0.aso.ore_no_yome.adapters.CharaListItem
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiImagePostTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.activity_main_post_add.*
import kotlinx.android.synthetic.main.content_main_post_add.*
import java.io.IOException

class MainPostAddActivity : AppCompatActivity() {
    private lateinit var userToken: String
    private lateinit var charaSelect: String
    private val resultRequestPickImage01 = 1001
    private val resultRequestPickImage02 = 1002
    private var resultPickImage01: Bitmap? = null
    private var resultPickImage02: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_post_add)
        setSupportActionBar(toolbar)

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        userToken = pref.getString("token", "") as String
        if (userToken.isEmpty()) {
            startActivity(
                Intent(
                    this, MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        loading_background.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == RESULT_OK) {
            if (resultData != null) {
                when (requestCode) {
                    resultRequestPickImage01 -> {
                        val uri = resultData.data as Uri
                        try {
                            val bitmap: Bitmap = getBitmapFromUri(uri)
                            resultPickImage01 = bitmap
                            button_image_select_1.setImageBitmap(bitmap)
                            button_image_select_1.scaleType = ImageView.ScaleType.FIT_CENTER
                            button_image_select_2.visibility = View.VISIBLE
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    resultRequestPickImage02 -> {
                        val uri = resultData.data as Uri
                        try {
                            val bitmap: Bitmap = getBitmapFromUri(uri)
                            resultPickImage02 = bitmap
                            button_image_select_2.setImageBitmap(bitmap)
                            button_image_select_2.scaleType = ImageView.ScaleType.FIT_CENTER
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

    override fun onResume() {
        super.onResume()

        button_image_select_1.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, resultRequestPickImage01)
        }

        button_image_select_2.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, resultRequestPickImage02)
        }

        button_chara_select.setOnClickListener { view ->
            ApiGetTask {
                if (it == null) {
                    Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
                } else {
                    when (it.getString("status")) {
                        "S00" -> {
                            val charaData = it.getJSONArray("data")
                            val list = ArrayList<CharaListItem>()
                            for (i in 0 until charaData.length()) {
                                val charaListItem = CharaListItem()
                                val item = charaData.getJSONObject(i)
                                charaListItem.charaId = item.getString("id").toLong()
                                charaListItem.animeTitle = item.getString("anime_name")
                                charaListItem.charaTitle = item.getString("chara_name")
                                list.add(charaListItem)
                            }
                            val listView = ListView(this)
                            val charaListAdapter = CharaListAdapter(this)
                            charaListAdapter.setCharaList(list)
                            charaListAdapter.notifyDataSetChanged()
                            listView.adapter = charaListAdapter

                            val dialog = AlertDialog.Builder(this)
                                .setTitle("キャラクターを選択してください")
                                .setView(listView)
                                .create()
                            dialog.show()

                            listView.setOnItemClickListener { adapterView, _, i, _ ->
                                val item = adapterView.getItemAtPosition(i) as CharaListItem
                                button_chara_select.text = item.charaTitle
                                charaSelect = item.charaId.toString()
                                dialog.dismiss()
                            }

                        }
                        else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }.execute(ApiParams("post/get-chara-list"))
        }

        button_post_add.setOnClickListener { view ->
            if (text_input_post_content.text.isNullOrEmpty() || charaSelect.isNotEmpty() || userToken.isNotEmpty()) {
                loading_background.visibility = View.VISIBLE
                text_input_post_content.visibility = View.INVISIBLE
                button_chara_select.visibility = View.INVISIBLE
                image_area.visibility = View.INVISIBLE
                button_post_add.visibility = View.INVISIBLE

                ApiImagePostTask {
                    if (it == null) {
                        Snackbar.make(view, "APIとの通信に失敗しました", Snackbar.LENGTH_SHORT).show()
                    } else {
                        when (it.getString("status")) {
                            "S00" -> {
                                finish()
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
                                        "UNKNOWN_TOKEN" -> {
                                            Toast.makeText(this, "ログイントークンが不明です", Toast.LENGTH_SHORT).show()
                                            Intent(
                                                this, TitleActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        "POST_CONTENT_LENGTH_OVER" -> Snackbar.make(
                                            view,
                                            "投稿できる最大文字数を超えています",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else -> Snackbar.make(view, "不明なエラーが発生しました", Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    loading_background.visibility = View.GONE
                    text_input_post_content.visibility = View.VISIBLE
                    button_chara_select.visibility = View.VISIBLE
                    image_area.visibility = View.VISIBLE
                    button_post_add.visibility = View.VISIBLE
                }.execute(
                    ApiParams(
                        "post/add-post",
                        hashMapOf(
                            "token" to userToken,
                            "content" to text_input_post_content.text.toString(),
                            "chara_id" to charaSelect
                        ),
                        resultPickImage01,
                        resultPickImage02
                    )
                )
            }
        }
    }

}
