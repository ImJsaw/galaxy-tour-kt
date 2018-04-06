package com.game.bing.starwar.game

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

internal class player constructor(bitmap: Bitmap, private val shiedPic: Bitmap, setBlood: Int, power: Int) : character(bitmap, setBlood, 2) {
    private var shied = false
    private val shiedRender = RectF()
    public var maxBlood: Int = setBlood
    var playerPower: Int = power

    override fun setX(x1: Float) {
        super.setX(x1)
        shiedRender.left = render.left - 20
        shiedRender.right = render.right + 20
    }

    override fun setY(y1: Float) {
        super.setY(y1)
        shiedRender.top = render.top - 90
        shiedRender.bottom = render.top - 90 + shiedPic.height
    }

    internal fun setShied(status: Boolean) {
        shied = status
    }

    internal fun shiedStatus(): Boolean {
        return shied
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (shiedStatus()) canvas.drawBitmap(shiedPic, null, shiedRender, null)

    }

}
