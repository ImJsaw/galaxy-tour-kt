package com.game.bing.starwar.game


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

internal open class sprite {
    var centerX: Float = 0.toFloat()
        set(x1) {
            field = x1
            render.left = centerX - width / 2
            render.right = centerX + width / 2
        }
    var centerY: Float = 0.toFloat()
        set(y1) {
            field = y1
            render.top = centerY - height / 2
            render.bottom = centerY + height / 2
        }
    private var width: Int = 0
    private var height: Int = 0
    private var dx = 0f
    private var dy = 0f
    val render = RectF()
    private var pic: Bitmap? = null
    var alive = true

    var type = 0
    //  0 sprite
    //  1 enemy
    //  2 player
    //  3 bullet
    //  4 enemyBullet
    //  5 reward

    constructor(bitmap: Bitmap, type: Int) {
        pic = bitmap
        width = pic!!.width
        height = pic!!.height
        this.type = type
    }

    constructor(bitmap: Bitmap, x: Float, y: Float) {
        pic = bitmap
        width = pic!!.width
        height = pic!!.height
        centerX = x
        centerY = y
    }

    constructor(bitmap: Bitmap, x: Float, y: Float, type: Int) {
        pic = bitmap
        width = pic!!.width
        height = pic!!.height
        centerX = x
        centerY = y
        this.type = type
    }

    fun setPosition(x: Float, y: Float) {
        setX(x)
        setY(y)
    }

    fun getdx(): Float {
        return dx
    }

    fun getdy(): Float {
        return dy
    }

    fun setdx(dx1: Float) {
        dx = dx1
    }

    fun setdy(dy1: Float) {
        dy = dy1
    }

    open fun setX(x : Float){
        centerX = x
        render.left = centerX - width / 2
        render.right = centerX + width / 2
    }

    open fun setY(y : Float){
        centerY = y
        render.top = centerY - height / 2
        render.bottom = centerY + height / 2
    }

    fun getX(): Float {
        return centerX
    }

    fun getY(): Float {
        return centerY
    }
    open fun move(x: Int, y: Int) {
        centerX += dx
        centerY += dy
    }

    fun bounce(x: Int, y: Int) {
        centerX += dx
        centerY += dy
        if (render.right > x) {
            centerX -= render.width() / 2
            dx = Math.random().toFloat() * -30
            dy = Math.random().toFloat() * 60 - 30
        }
        if (render.bottom > y) {
            centerY -= render.height() / 2
            dx = Math.random().toFloat() * 60 - 30
            dy = Math.random().toFloat() * -30
        }
        if (render.left < 0) {
            centerX = render.width() / 2
            dx = Math.random().toFloat() * 30
            dy = Math.random().toFloat() * 60 - 30
        }
        if (render.top < 0) {
            centerY  = render.height() / 2
            dx = Math.random().toFloat() * 60 - 30
            dy = Math.random().toFloat() * 30
        }

    }

    fun setpic(bitmap: Bitmap) {
        pic = bitmap
    }

    open fun draw(canvas: Canvas) {
        canvas.drawBitmap(pic!!, null, render, null)
    }


    fun destroy() {
        pic = null
        alive = false
    }


}
