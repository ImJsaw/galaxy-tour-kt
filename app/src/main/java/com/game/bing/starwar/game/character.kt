package com.game.bing.starwar.game


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log

internal open class character : sprite {
    private var maxBlood: Int = 0
    var blood: Int = 0
    var atkSpeed: Int = 0
    private val bloodRender = RectF()

    constructor(bitmap: Bitmap, setBlood: Int, type: Int) : super(bitmap, type) {
        maxBlood = setBlood
        blood = setBlood
    }

    constructor(bitmap: Bitmap, x: Float, y: Float, setBlood: Int, type: Int) : super(bitmap, x, y, type) {
        maxBlood = setBlood
        blood = setBlood
    }

    fun damage(damage: Int) {
        blood = blood - damage
    }

    fun heal(plusBlood: Int) {
        var plusBlood = plusBlood
        if (plusBlood == 0) plusBlood++
        blood += plusBlood
        Log.d("heal", plusBlood.toString())
        if (blood > maxBlood) blood = maxBlood
    }

    //setting about blood
    private fun updateBloodPos() {

        val temp = super.render
        bloodRender.left = temp.left
        bloodRender.right = temp.right
        bloodRender.bottom = temp.top - 10
        bloodRender.top = temp.top - 20
    }

    override fun draw(canvas: Canvas) {
        val mpaint = Paint()
        val temp = bloodRender
        super.draw(canvas)
        updateBloodPos()
        //full
        mpaint.color = Color.RED
        mpaint.style = Paint.Style.FILL
        canvas.drawRect(temp, mpaint)
        //blood
        mpaint.color = Color.GREEN
        mpaint.style = Paint.Style.FILL
        temp.right = temp.left + temp.width() * blood / maxBlood
        canvas.drawRect(temp, mpaint)
        //border
        mpaint.color = Color.BLACK
        mpaint.style = Paint.Style.STROKE
        canvas.drawRect(bloodRender, mpaint)
    }
}
