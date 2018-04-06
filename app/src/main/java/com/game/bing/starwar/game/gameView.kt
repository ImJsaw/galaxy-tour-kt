package com.game.bing.starwar.game

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.support.v7.app.AlertDialog
import android.text.method.KeyListener
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

import com.game.bing.starwar.R
import com.game.bing.starwar.StartActivity
import com.game.bing.starwar.UpgradeActivity

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import android.content.Context.MODE_PRIVATE


class gameView : View {
    private val bitmaps = ArrayList<Bitmap>()
    private val bullets = ArrayList<bullet>()
    private val enemies = ArrayList<Enemy>()
    private val rewards = ArrayList<dropItem>()

    private var playerShotID: Int = 0
    private var enemyShotID: Int = 0

    private var hasEnemy = false

    private var status: Int = 0
    private val STATUS_START = 1
    //private final int statusDestroy = 0;
    private val STATUS_PAUSE = -1
    private val STATUS_GAMEOVER = 2
    private val STATUS_WIN = 3


    internal var player: player? = null

    private var goHome = false
    private var gameOverShow = false
    private var touchToHome = false

    private val mPaint = Paint()
    private var move = false
    private var frame: Long = 0
    private var score: Long = 0
    private var highScore: Long = 0
    private var curStage = 0
    private val totalStage = 2
    private var shiedCD: Boolean = false
    private var forkCD: Boolean = false
    private var forkAmmo: Boolean = false
    private var shootCD: Int = 0

    private var data: SharedPreferences? = null

    private var playerShotPool: SoundPool? = null
    private var enemyShotPool: SoundPool? = null

    private var c: Context? = null
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

    private val pauseRender: RectF
        get() = RectF((canvasWidth - 20 - bitmaps[3].width).toFloat(), 20f, (canvasWidth - 20).toFloat(), (20 + bitmaps[3].height).toFloat())


    constructor(context: Context) : super(context) {
        c = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        c = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        c = context
    }


    fun start(ids: IntArray) {
        //clear
        destroy()

        highScore = 0
        //get pic
        Log.d("bitmapFin", "not yet")
        for (id in ids) bitmaps.add(BitmapFactory.decodeResource(resources, id))
        postInvalidate()

        try {
            initSP()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("bitmapFin", "Y")
        //real start
        realStart()
    }


    @Throws(Exception::class)
    private fun initSP() {
        //设置最多可容纳5个音频流，音频的品质为5
        val soundPoolBuilder = SoundPool.Builder()
        soundPoolBuilder.setMaxStreams(3)
        soundPoolBuilder.setAudioAttributes(AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build())
        playerShotPool = soundPoolBuilder.build()
        enemyShotPool = soundPoolBuilder.build()

        enemyShotID = enemyShotPool!!.load(c, R.raw.shot_alien, 1)
        playerShotID = playerShotPool!!.load(c, R.raw.shot, 1)
        //soundID[2] = spl.load(c,R.raw.blast,1);

    }

    private fun realStart() {
        data = c!!.getSharedPreferences("data", MODE_PRIVATE)
        val pow = data!!.getInt("playerATK", 5)
        val bld = data!!.getInt("playerBLOOD", 10)

        player = player(bitmaps[0], bitmaps[8], bld, pow)
        //initial value
        status = STATUS_PAUSE //wait until story
        shiedCD = false
        forkCD = false
        forkAmmo = false
        score = 0
        shootCD = 30

        //run story
        runStory()

        status = STATUS_START


        //start
        postInvalidate()
    }

    private fun runStory() {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasHeight = canvas.height
        canvasWidth = canvas.width

        if (frame == 0L) checkNextMap(canvas)
        if (frame % shootCD == 0L && status == STATUS_START) playerShoot()


        when (status) {
            STATUS_START -> drawGame(canvas)
            STATUS_PAUSE -> drawPause(canvas)
            STATUS_GAMEOVER -> drawGameOver(canvas)
            STATUS_WIN -> {
                drawGameWin(canvas)
                touchToHome = true
            }
            else -> {
            }
        }

        frame++
    }

    private fun drawGame(canvas: Canvas) {
        mPaint.textSize = 50f
        //player bullet
        val bulletIterator = bullets.iterator()
        while (bulletIterator.hasNext()) {
            val bullet = bulletIterator.next()
            bullet.draw(canvas)
            bullet.move(0, 0)
            if (bulletDestroy(bullet)) bulletIterator.remove()
        }
        //draw enemy
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val e = enemyIterator.next()
            if (e.shoot()) enemyShoot(e)
            e.move(canvas.width, canvas.height / 2)
            if (collidePlayer(e)) {
                if (e.blood > 5) {
                    for (i in 0..4) e.damage(5)
                } else
                    enemyIterator.remove()
            }
            e.draw(canvas)
            hasEnemy = true
        }

        //draw drops
        val rewardIterator = rewards.iterator()
        while (rewardIterator.hasNext()) {
            val reward = rewardIterator.next()
            reward.move(0, 0)
            if (collidePlayer(reward))
                rewardIterator.remove()
            else if (reward.getY() > canvas.height) {
                if (reward.rewardType == 7) shiedCD = false
                if (reward.rewardType == 10) forkCD = false

                rewardIterator.remove()
            } else
                reward.draw(canvas)
        }

        //draw player
        if (player!!.alive)
            player!!.draw(canvas)
        else
            status = STATUS_GAMEOVER

        checkNextMap(canvas)

        //draw score
        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 50f
        mPaint.color = Color.WHITE
        canvas.drawText("SCORE:" + score.toString(), 20f, 50f, mPaint)

        //draw pause button
        canvas.drawBitmap(bitmaps[5], (canvas.width - 20 - bitmaps[5].width).toFloat(), 20f, mPaint)
        postInvalidate()
    }

    private fun drawPause(canvas: Canvas) {
        //player bullet
        for (bullet in bullets) {
            bullet.draw(canvas)
        }
        //draw enemy
        for (e in enemies) {
            e.draw(canvas)
        }

        //draw player
        player!!.draw(canvas)

        //draw score
        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 50f
        mPaint.color = Color.WHITE
        canvas.drawText("SCORE:" + score.toString(), 20f, 50f, mPaint)

        //draw pause button
        canvas.drawBitmap(bitmaps[5], (canvas.width - 20 - bitmaps[5].width).toFloat(), 20f, mPaint)

        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 80f
        mPaint.color = Color.WHITE
        canvas.drawText("煞氣a時間暫停!", (canvas.width / 2 - 300).toFloat(), (canvas.height / 2).toFloat(), mPaint)

        postInvalidate()
    }

    private fun drawGameOver(canvas: Canvas) {
        countPoint()

        if (gameOverShow) return // not repeat paint

        val reviveDialog = AlertDialog.Builder(c!!)
                .setTitle("Hint")
                .setMessage("你死惹Q_Q")
                .setNegativeButton("重新開始") { dialogInterface, i ->
                    Log.d("act", "restart")
                    restart()
                }
                .create()
        reviveDialog.setCancelable(false)
        reviveDialog.show()



        gameOverShow = true
    }

    private fun restart() {
        destroyRecycleBitmaps()
        realStart()
    }

    private fun destroyRecycleBitmaps() {
        gameOverShow = false
        frame = 0
        curStage = 0
        score = 0
        if (player != null) player!!.destroy()
        player = null
        for (b in bullets) b.destroy()
        bullets.clear()
        for (e in enemies) e.destroy()
        enemies.clear()
        for (reward in rewards) reward.destroy()
        rewards.clear()

    }

    private fun destroy() {
        destroyRecycleBitmaps()
        //clear bitmap
        for (bitmap in bitmaps) bitmap.recycle()
        bitmaps.clear()
    }

    private fun checkNextMap(canvas: Canvas) {
        if (hasEnemy) {
            hasEnemy = false
            return
        }
        curStage++
        if (curStage <= totalStage) {
            showText("目前關卡:" + curStage.toString())
            loadStage(canvas)
            player!!.setX((canvas.width / 2).toFloat())
            player!!.setY((canvas.height - 200).toFloat())
        } else
            status = STATUS_WIN
    }

    private fun drawGameWin(canvas: Canvas) {
        countPoint()
        if (score > highScore) highScore = score

        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 200f
        mPaint.color = Color.GREEN
        mPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("VICTORY", (canvasWidth / 2).toFloat(), 400f, mPaint)

        mPaint.textSize = 80f
        mPaint.color = Color.WHITE
        canvas.drawText("High Score : " + highScore.toString(), (canvasWidth / 2).toFloat(), (canvas.height / 2).toFloat(), mPaint)
        canvas.drawText("Score : " + score.toString(), (canvasWidth / 2).toFloat(), (canvas.height / 2 + 200).toFloat(), mPaint)

        mPaint.textSize = 60f
        mPaint.color = Color.GREEN
        canvas.drawText("觸碰返回主畫面 ", (canvas.width / 2).toFloat(), (canvas.height - 300).toFloat(), mPaint)

    }

    private fun loadStage(canvas: Canvas) {
        var enemy: Enemy
        when (curStage) {
            1 -> for (x in 1..7) {
                for (y in 1..4) {
                    enemy = Enemy(bitmaps[3], (canvas.width * x / 8).toFloat(), (20 + canvas.height / 2 * y / 5).toFloat(), 40, curStage, 1)
                    enemies.add(enemy)
                }
            }
            2 -> {
                enemy = Enemy(bitmaps[2], (canvas.width / 2).toFloat(), 300f, 1000, curStage, 3)
                enemies.add(enemy)
            }
            else -> {
            }
        }
    }

    private fun playerShoot() {
        var bullet = bullet(bitmaps[1], player!!.getX(), player!!.getY() - 100, 3, player!!.playerPower)
        bullets.add(bullet)
        if (forkAmmo) {
            bullet = bullet(bitmaps[1], player!!.getX(), player!!.getY() - 100, 3, player!!.playerPower)
            bullet.setdx(1f)
            bullets.add(bullet)

            bullet = bullet(bitmaps[1], player!!.getX(), player!!.getY() - 100, 3, player!!.playerPower)
            bullet.setdx(-1f)
            bullets.add(bullet)

        }

        playerShotPool!!.play(playerShotID, 1f, 1f, 1, 0, 1f)
    }

    private fun enemyShoot(e: Enemy) {
        val bullet = bullet(bitmaps[4], e.getX(), e.getY() + 150, 4, e.atkPower)
        bullet.setdy(10f)
        bullets.add(bullet)
        enemyShotPool!!.play(enemyShotID, 1f, 1f, 1, 0, 1f)
    }

    private fun bulletDestroy(bullet: bullet): Boolean {
        return bullet.getY() < 0 || bullet.getY() > canvasHeight || collideEnemy(bullet) || collidePlayer(bullet)
    }

    private fun collideEnemy(sprite: sprite): Boolean {
        val EnemyIterator: MutableIterator<Enemy> = enemies.iterator()
        while (EnemyIterator.hasNext()) {
            val e = EnemyIterator.next()
            if (sprite.render.intersect(e.render) && sprite.type == 3) {
                e.damage(player!!.playerPower)
                score += 1
                if (e.dropReward()) { //掉落機制待平衡
                    dropReward(e)
                }
                if (e.blood <= 0) {
                    EnemyIterator.remove()
                    score += 5
                }
                return true
            }
        }
        return false
    }

    private fun dropReward(e: Enemy) {
        var temp = 0//6 heal 7 shied 9 ammo
        var drop = true
        val r = Math.random()
        if (r < 0.3 && !shiedCD) {
            temp = 7
            shiedCD = true
        } else if (r < 0.5)
            temp = 6
        else if (r < 0.8)
            temp = 9
        else if (!forkCD) {
            temp = 10
            forkCD = true
        } else
            drop = false

        if (drop) {
            val reward = dropItem(bitmaps[temp], e.getX(), e.getY() + 20, 5, temp)
            rewards.add(reward)
        }

    }

    private fun collidePlayer(sprite: sprite): Boolean {
        if (sprite.render.intersect(player!!.render)) {
            when (sprite.type) {
                1 ->
                    //enemy
                    if (!player!!.shiedStatus())
                        playerDead()
                    else
                        player!!.setShied(false)
                3 ->
                    //bullet
                    return false
                4 -> {
                    //Enemy bullet
                    val b = sprite as bullet
                    if (!player!!.shiedStatus()) {
                        player!!.damage(b.atkPower)
                        if (player!!.blood < 0) playerDead()
                    }
                }
                5 -> {
                    //reward
                    score += 2
                    val d = sprite as dropItem
                    when (d.rewardType) {
                        6 -> {
                            //heal
                            val maxBlood = player!!.maxBlood
                            player!!.heal(maxBlood / 10)
                            Log.d("maxBlood", maxBlood.toString())
                            if (player!!.blood > maxBlood) player!!.blood = maxBlood//void over heal
                        }
                        7 -> {
                            //shied
                            player!!.setShied(true)
                            setShiedTimer()
                        }
                        9 -> {
                            //ammo
                            shootCD = (shootCD * 0.75).toInt()
                            if (shootCD < 10) shootCD = 10
                        }
                        10 -> {
                            //fork
                            forkAmmo = true
                            setForkTimer()
                        }
                    }
                }
            }
            return true
        }
        return false
    }

    private fun setForkTimer() {
        val shiedTimer = Timer()
        val shiedTask = object : TimerTask() {
            override fun run() {
                forkAmmo = false
                forkCD = false
            }
        }
        shiedTimer.schedule(shiedTask, 3000)
    }

    private fun setShiedTimer() {
        val shiedTimer = Timer()
        val shiedTask = object : TimerTask() {
            override fun run() {
                player!!.setShied(false)
                shiedCD = false
            }
        }
        shiedTimer.schedule(shiedTask, 3000)
    }

    private fun playerDead() {
        player!!.alive = false
    }

    private fun showText(s: String) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show()
    }

    private fun countPoint() {
        val getPoint = score.toInt() / 100
        val oiPoint = data!!.getInt("restPoint", 0)

        val editor = data!!.edit()
        editor.putInt("restPoint", oiPoint + getPoint)
        editor.apply()

        showText("獲得" + getPoint.toString() + "點升級點")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (touchToHome) goHome = true
            if (player!!.render.contains(touchX, touchY) && status == STATUS_START)
                move = true
            else if (pauseRender.contains(touchX, touchY)) status *= -1
        } else if (event.action == MotionEvent.ACTION_UP) move = false
        if (move) {
            player!!.setPosition(touchX, touchY)
        }
        return true
    }

    fun goHome(): Boolean {
        return goHome
    }


}
