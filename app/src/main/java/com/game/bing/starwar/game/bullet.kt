package com.game.bing.starwar.game

import android.graphics.Bitmap

internal class bullet(bitmap: Bitmap, x: Float, y: Float, type: Int, val atkPower: Int) : sprite(bitmap, x, y, type) {


    init {
        setdy(-10f)
    }

}
