package com.vxx0.aso.ore_no_yome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import com.vxx0.aso.ore_no_yome.apis.ApiGetTask
import com.vxx0.aso.ore_no_yome.apis.ApiParams
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MainFragment.OnFragmentInteractionListener, MainProfileFragment.OnFragmentInteractionListener {
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val fragment = MainFragment()
        fragment.arguments = Bundle().apply {
            putString("action", "home")
        }
        title = "ホーム"
        supportFragmentManager.beginTransaction().replace(R.id.root_fragment, fragment).commit()
    }

    override fun onResume() {
        super.onResume()

        button_post_add.setOnClickListener {
            startActivity<MainPostAddActivity>()
        }

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        val userToken = pref.getString("token", "") as String

        ApiGetTask {
            if (it == null) {
                Toast.makeText(this, "ユーザー情報を取得できませんでした", Toast.LENGTH_SHORT).show()
            } else {
                when (it.getString("status")) {
                    "S00" -> {
                        val userName = it.getJSONObject("data").getString("user_name")
                        val userId = it.getJSONObject("data").getString("user_id")
                        val userIcon = it.getJSONObject("data").getString("icon")

                        text_user_name.text = userName
                        text_user_id.text = userId

                        if (!userIcon.isNullOrEmpty()) {
                            Picasso.get().load("https://aso.vxx0.com/ore-no-yome/upload/icon/$userIcon")
                                .into(user_icon)
                        }
                    }
                    "E00" -> {
                        val msgArray = it.getJSONArray("msg")
                        for (i in 0 until msgArray.length()) {
                            when (msgArray.getString(i)) {
                                "REQUIRED_PARAM" -> Toast.makeText(
                                    this,
                                    "必要な値が見つかりませんでした",
                                    Toast.LENGTH_SHORT
                                ).show()
                                "UNKNOWN_TOKEN" -> {
                                    Toast.makeText(this, "ログイントークンが不明です", Toast.LENGTH_SHORT).show()
                                }
                                else -> Toast.makeText(this, "不明なエラーが発生しました", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> Toast.makeText(this, "ユーザー情報を取得できませんでした", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute(ApiParams("user/get-my-info", hashMapOf("token" to userToken)))
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> false
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when (item.itemId) {
            R.id.menu_home -> {
                val fragment = MainFragment()
                fragment.arguments = Bundle().apply {
                    putString("action", "home")
                }
                supportFragmentManager.beginTransaction().replace(R.id.root_fragment, fragment).commit()
                title = "ホーム"
                button_post_add.visibility = View.VISIBLE
                menu.findItem(R.id.menu_search).isVisible = true
            }
            R.id.menu_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.root_fragment, MainProfileFragment()).commit()
                title = "プロフィール"
                button_post_add.visibility = View.GONE
                menu.findItem(R.id.menu_search).isVisible = false
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_favorite -> {
                val fragment = MainFragment()
                fragment.arguments = Bundle().apply {
                    putString("action", "favorite")
                }
                supportFragmentManager.beginTransaction().replace(R.id.root_fragment, fragment).commit()
                title = "お気に入り"
                button_post_add.visibility = View.GONE
                menu.findItem(R.id.menu_search).isVisible = false
            }
            R.id.menu_notice -> {
                val fragment = MainFragment()
                fragment.arguments = Bundle().apply {
                    putString("action", "notice")
                }
                supportFragmentManager.beginTransaction().replace(R.id.root_fragment, fragment).commit()
                title = "通知"
                button_post_add.visibility = View.GONE
                menu.findItem(R.id.menu_search).isVisible = false
            }
            R.id.menu_setting -> {
                // 未実装
                drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
            R.id.menu_help -> {
                // 未実装というかこれなに
                drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
            R.id.menu_sign_out -> {
                AlertDialog.Builder(this)
                    .setTitle("ログアウト")
                    .setMessage("ログアウトしますか？")
                    .setPositiveButton("ログアウト") { _, _ ->
                        val pref = getSharedPreferences("data", Context.MODE_PRIVATE)
                        pref.edit().clear().apply()
                        val intent = Intent(this, TitleActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                        /*
                        startActivity(
                            Intent(
                                this, TitleActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        */
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
                return false
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {}

}
