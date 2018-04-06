package com.game.bing.starwar.game

import android.graphics.Bitmap

internal class dropItem(bitmap: Bitmap, x: Float, y: Float, type: Int, val rewardType: Int) : sprite(bitmap, x, y, type) {

    init {
        setdy(10f)
    }

    fun rewardType(): Int {
        return rewardType
        //heal 1
        //shied 2
    }
}
