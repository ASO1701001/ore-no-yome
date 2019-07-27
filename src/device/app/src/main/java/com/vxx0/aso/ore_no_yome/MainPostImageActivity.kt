package com.vxx0.aso.ore_no_yome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_post_image.*
import kotlinx.android.synthetic.main.content_main_post_image.*

class MainPostImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_post_image)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")

        val fileName = intent.getStringExtra("file_name")
        val filePath = "https://aso.vxx0.com/ore-no-yome/upload/post/"
        Picasso.get().load(filePath + fileName).into(image)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}
