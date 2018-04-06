package com.game.bing.starwar.game

import android.graphics.Bitmap
import android.graphics.RectF

internal class Enemy(bitmap: Bitmap, x: Float, y: Float, setBlood: Int, private val curStage: Int, val atkPower: Int) : character(bitmap, x, y, setBlood, 1) {

    init {
        super.setdx(Math.random().toFloat() * 50)
        super.setdy(Math.random().toFloat() * 50)
    }

    fun dropReward(): Boolean {
        when (curStage) {
            1 -> return Math.random() < 0.1
            2 -> return Math.random() < 0.03
        }
        return true
    }

    //
    override fun move(x: Int, y: Int) {
        when (curStage) {
            1 -> {
            }
            2 -> super.bounce(x, y)
            else -> {
            }
        }
    }

    fun shoot(): Boolean {
        when (curStage) {
            1 -> if (Math.random() < 0.003) return true
            2 -> if (Math.random() < 0.01) return true
            else -> {
            }
        }
        return false
    }
}
