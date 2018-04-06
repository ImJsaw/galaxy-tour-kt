package com.game.bing.starwar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.game.bing.starwar.game.player

import java.util.Timer
import java.util.TimerTask

class UpgradeActivity : AppCompatActivity() {

    //Button atkP,atkM,bloodP,bloodM,spdP,spdM;
    private lateinit var atk: TextView
    private lateinit var blood: TextView
    private lateinit var spd: TextView
    private lateinit var restPoint: TextView
    private var data: SharedPreferences? = null
    private var pow: Int = 0
    private var bld: Int = 0
    private var tpow: Int = 0
    private var tbld: Int = 0

    private var point: Int = 0
    private var tempPoint: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        atk = findViewById(R.id.atkValue)
        blood = findViewById(R.id.bloodValue)
        spd = findViewById(R.id.spdValue)
        restPoint = findViewById(R.id.restPoint)





        data = getSharedPreferences("data", Context.MODE_PRIVATE)
        point = data!!.getInt("restPoint", 0)
        pow = data!!.getInt("playerATK", 5)
        bld = data!!.getInt("playerBLOOD", 10)

        tempPoint = point
        tpow = pow
        tbld = bld


        atk.text = tpow.toString()
        blood.text = tbld.toString()
        restPoint.text = "剩餘點數 : " + tempPoint.toString()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.atkPlus -> if (tempPoint > 0) {
                tempPoint--
                tpow++
                atk.text = tpow.toString()
                restPoint.text = "剩餘點數 : " + tempPoint.toString()
            }
            R.id.atkMinus -> if (tempPoint < point && tpow > pow) {
                tempPoint++
                tpow--
                atk.text = tpow.toString()
                restPoint.text = "剩餘點數 : " + tempPoint.toString()
            }
            R.id.bloodPlus -> if (tempPoint > 0) {
                tempPoint--
                tbld++
                blood.text = tbld.toString()
                restPoint.text = "剩餘點數 : " + tempPoint.toString()
            }
            R.id.bloodMinus -> if (tempPoint < point && tbld > bld) {
                tempPoint++
                tbld--
                blood.text = tbld.toString()
                restPoint.text = "剩餘點數 : " + tempPoint.toString()
            }
            R.id.spdPlus -> {
            }
            R.id.spdMinus -> {
            }
            R.id.confirm -> {
                val editor = data!!.edit()
                editor.putInt("restPoint", tempPoint)
                editor.putInt("playerATK", tpow)
                editor.putInt("playerBLOOD", tbld)
                editor.apply()

                point = tempPoint
                bld = tbld
                pow = tpow
            }
            R.id.cancel -> {
                tempPoint = point
                tpow = pow
                tbld = bld
                atk.text = tpow.toString()
                blood.text = tbld.toString()
                restPoint.text = "剩餘點數 : " + tempPoint.toString()
            }
            R.id.start_game -> GameStart()
            R.id.leftArrow -> Toast.makeText(this, "不要猴急.哥還在開發", Toast.LENGTH_SHORT).show()
            R.id.rightArrow -> Toast.makeText(this, "不要猴急.哥還在開發", Toast.LENGTH_SHORT).show()
        }//clear history
        //back to history
    }

    private fun GameStart() {
        val game = Intent(this, GameActivity::class.java)
        startActivity(game)
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
