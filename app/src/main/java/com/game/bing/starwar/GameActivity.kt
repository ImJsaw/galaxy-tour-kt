package com.game.bing.starwar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast

import com.game.bing.starwar.game.gameView

import java.util.Timer
import java.util.TimerTask

class GameActivity : Activity() {
    private var gameView: gameView? = null
    internal var t = Timer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        gameView = findViewById(R.id.gameView)

        val bitmapIDs = intArrayOf(R.drawable.plane, //0
                R.drawable.bullet, //1
                R.drawable.demon, //2
                R.drawable.demon_small, //3
                R.drawable.fire_ball, //4
                R.drawable.pause, //5
                R.drawable.medicine, //6
                R.drawable.shied, //7
                R.drawable.plane_shied, //8
                R.drawable.ammo, //9
                R.drawable.fork)//10

        setGoHomeListener()
        gameView!!.start(bitmapIDs)
    }

    private fun setGoHomeListener() {
        t.schedule(object : TimerTask() {
            override fun run() {
                if (gameView!!.goHome()) backToHome()
            }
        }, 2000, 1000)
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

    private fun backToHome() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        t.cancel()
        finish()
    }

    companion object {
        private var exitConfirm: Boolean? = false
        private var exitBuffer: Boolean? = false //避免重複timer導致退出
    }
}
