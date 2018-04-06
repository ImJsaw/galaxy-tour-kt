package com.game.bing.starwar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import java.util.Timer
import java.util.TimerTask

class StartActivity : AppCompatActivity() {

    internal lateinit var title: TextView
    private var button = arrayOfNulls<Button>(4)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        title = findViewById<View>(R.id.title_starwar) as TextView
        //title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/WCL-09.ttf"));

        val font2 = Typeface.createFromAsset(resources.assets, "fonts/WCL-09.ttf")
        button[0] = findViewById<View>(R.id.StartButton) as Button
        button[0]!!.setTypeface(font2)//24

        button[1] = findViewById<View>(R.id.OptionButton) as Button
        button[1]!!.setTypeface(font2)//28

        button[2] = findViewById<View>(R.id.HelpButton) as Button
        button[2]!!.setTypeface(font2)//34

        button[3] = findViewById<View>(R.id.AboutButton) as Button
        button[3]!!.setTypeface(font2)//40

    }

    fun onClick(view: View) {
        if (view.id == R.id.StartButton) {
            GameStart()
        }
        if (view.id == R.id.AboutButton) {
            aboutBox.show(this)
        }
        if (view.id == R.id.HelpButton) {
            for (i in 0..3) {
                button[i]!!.setBackgroundColor(Color.BLACK)
                button[i]!!.setTextColor(Color.GREEN)
            }
        }


    }

    private fun GameStart() {
        val intent = Intent(this, UpgradeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitConfirm!!) {
                System.exit(0)
            } else {
                exitConfirm = true
                Toast.makeText(this, "再按一次返回鍵退出遊戲", Toast.LENGTH_SHORT).show()
                if ((!exitBuffer!!)) {
                    endAPP()
                }
            }
        }
        return false
    }

    private fun endAPP() {
        exitBuffer = true
        val exitTimer = Timer()
        val exitTask = object : TimerTask() {
            override fun run() {
                exitConfirm = false
                exitBuffer = false
            }
        }
        exitTimer.schedule(exitTask, 2000)
    }

    companion object {

        private var exitConfirm: Boolean? = false
        private var exitBuffer: Boolean? = false //避免重複timer導致退出
    }


}
