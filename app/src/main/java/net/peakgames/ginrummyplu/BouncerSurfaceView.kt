package net.peakgames.ginrummyplu

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import net.peakgames.ginrummyplu.BouncerScreenSizeConstants.SCREEN_HEIGHT
import net.peakgames.ginrummyplu.BouncerScreenSizeConstants.SCREEN_WIDTH

class BouncerSurfaceView(context: Context): SurfaceView(context), SurfaceHolder.Callback {

    private var bouncerLooping: BouncerLooping? = null
    private var bouncerBackground: BouncerBackground? = null
    private var bouncer: Bouncer? = null
    private var bouncerButtons: BouncerButtons? = null
    private var bulletLeftList: MutableList<Bullet>? = null
    private var bulletRightList: MutableList<Bullet>? = null
    private var enemyLeftList: MutableList<Enemy>? = null
    private var enemyRightList: MutableList<Enemy>? = null

    private var startTimeBulletShot: Long = 0
    private val taimingBetweenBulletSpawns: Long = 1200

    private var startTimeEnemyLeftSpawns: Long = 0
    private var startTimeEnemyRightSpawns: Long = 0

    private val taimingBetweenEnemyLeftSpawns: Long = 2500
    private val taimingBetweenEnemyRightSpawns: Long = 3500

    private var gameState = 0
    private var gameScore = 0
    private val sharedPrefs = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

    init {
        isFocusable = true
        holder.addCallback(this)
        bouncerLooping = BouncerLooping(holder, this)
    }

    private fun getScaledBouncerBackground(): Bitmap {
        val bouncerBackgroundImage = BitmapFactory.decodeResource(resources, R.drawable.bouncer_background)
        val bouncerBackgroundImageWidth = bouncerBackgroundImage.width
        val bouncerBackgroundImageHeight = bouncerBackgroundImage.height
        val scaledRatio = (bouncerBackgroundImageWidth / bouncerBackgroundImageHeight).toFloat()
        val scaledBouncerBackgroundImageWidth = (scaledRatio * SCREEN_HEIGHT).toInt()
        return Bitmap.createScaledBitmap(bouncerBackgroundImage, scaledBouncerBackgroundImageWidth, bouncerBackgroundImageHeight, false)
    }

    private fun addBulletLeftToList(startTime: Long) {
        if (startTime > taimingBetweenBulletSpawns && bouncerButtons!!.isBouncerButtonLeftPressed) {
            bulletLeftList?.add(Bullet(BitmapFactory.decodeResource(resources, R.drawable.bullet_left_image), bouncer!!.bouncerRectangle().left, bouncer!!.bouncerRectangle().top + bouncer!!.bouncerImage.height / 2))
            startTimeBulletShot = System.nanoTime()
        }
    }

    private fun addBulletRightToList(startTime: Long) {
        if (startTime > taimingBetweenBulletSpawns && bouncerButtons!!.isBouncerButtonRightPressed) {
            bulletRightList?.add(Bullet(BitmapFactory.decodeResource(resources, R.drawable.bullet_right_image), bouncer!!.bouncerRectangle().right, bouncer!!.bouncerRectangle().top + bouncer!!.bouncerImage.height / 2))
            startTimeBulletShot = System.nanoTime()
        }
    }

    private fun updateBulletLeft() {
        bulletLeftList?.forEach { bulletLeft ->
            bulletLeft.updateBulletLeftMovement()
            if (bulletLeft.bulletRectangle().left < 0) bulletLeftList?.remove(bulletLeft)
        }
    }

    private fun updateBulletRight() {
        bulletRightList?.forEach { bulletRight ->
            bulletRight.updateBulletRightMovement()
            if (bulletRight.bulletRectangle().right > SCREEN_WIDTH + 20) bulletRightList?.remove(bulletRight)
        }
    }

    private fun addEnemyLeftToList(startTime: Long) {
        if (startTime > taimingBetweenEnemyLeftSpawns) {
            enemyLeftList?.add(Enemy(BitmapFactory.decodeResource(resources, R.drawable.enemy_left_image), SCREEN_WIDTH + (50..100).random(), bouncer!!.bouncerRectangle().top + 100))
            startTimeEnemyLeftSpawns = System.nanoTime()
        }
    }

    private fun addEnemyRightToList(startTime: Long) {
        if (startTime > taimingBetweenEnemyRightSpawns) {
            enemyRightList?.add(Enemy(BitmapFactory.decodeResource(resources, R.drawable.enemy_right_image), (-100..-50).random(), bouncer!!.bouncerRectangle().top))
            startTimeEnemyRightSpawns = System.nanoTime()
        }
    }

    private fun updateEnemyLeft() {
        enemyLeftList?.forEach { enemyLeft ->
            enemyLeft.updateEnemyLeftMovement()
            if (enemyLeft.enemyRectangle().left < -250) enemyLeftList?.remove(enemyLeft)
        }
    }

    private fun updateEnemyRight() {
        enemyRightList?.forEach { enemyRight ->
            enemyRight.updateEnemyRightMovement()
            if (enemyRight.enemyRectangle().right > SCREEN_WIDTH + 250) enemyRightList?.remove(enemyRight)
        }
    }

    private fun checkCollisionBetweenEnemyLeftAndBouncer() {
        enemyLeftList?.forEach { enemyLeft ->
            if (Rect.intersects(enemyLeft.enemyRectangle(), bouncer!!.bouncerRectangle())) {
                gameOver()
            }
        }
    }

    private fun checkCollisionBetweenEnemyRightAndBouncer() {
        enemyRightList?.forEach { enemyRight ->
            if (Rect.intersects(enemyRight.enemyRectangle(), bouncer!!.bouncerRectangle())) {
                gameOver()
            }
        }
    }

    private fun checkCollisionBetweenEnemyLeftAndBulletRight() {
        enemyLeftList?.forEach { enemyLeft ->
            bulletRightList?.forEach { bulletRight ->
                if (Rect.intersects(enemyLeft.enemyRectangle(), bulletRight.bulletRectangle())) {
                    gameScore++
                    enemyLeftList?.remove(enemyLeft)
                    bulletRightList?.remove(bulletRight)
                }
            }
        }
    }

    private fun checkCollisionBetweenEnemyRightAndBulletLeft() {
        enemyRightList?.forEach { enemyRight ->
            bulletLeftList?.forEach { bulletLeft ->
                if (Rect.intersects(enemyRight.enemyRectangle(), bulletLeft.bulletRectangle())) {
                    gameScore++
                    enemyRightList?.remove(enemyRight)
                    bulletLeftList?.remove(bulletLeft)
                }
            }
        }
    }

    private fun isNewTopScore(): Boolean {
        val topScore = sharedPrefs.getInt("bestScore", 0)
        return gameScore > topScore
    }

    private fun setNewTopScore() {
        val sharedPrefsEditor = sharedPrefs.edit()
        sharedPrefsEditor.putInt("bestScore", gameScore).apply()
    }

    private fun setInitialBouncerPosition() {
        bouncer!!.bouncerPositionX = SCREEN_WIDTH / 2 - bouncer!!.bouncerImage.width / 2
    }

    private fun gameOver() {
        if (isNewTopScore()) setNewTopScore()
        gameState = 2
        enemyLeftList?.clear()
        enemyRightList?.clear()
        bulletLeftList?.clear()
        bulletRightList?.clear()
        gameScore = 0
        setInitialBouncerPosition()
    }

    fun updateObjects() {
        if (gameState == 1) {
            bouncerBackground!!.updateBouncerBackground()
            if (bouncerButtons!!.isBouncerButtonLeftPressed) bouncer!!.updateBouncerLeftMovement()
            else if (bouncerButtons!!.isBouncerButtonRightPressed) bouncer!!.updateBouncerRightMovement()
            val startBulletShotTimeElapsed = (System.nanoTime() - startTimeBulletShot) / 1000000
            val startEnemyLeftTimeElapsed = (System.nanoTime() - startTimeEnemyLeftSpawns) / 1000000
            val startEnemyRightTimeElapsed = (System.nanoTime() - startTimeEnemyRightSpawns) / 1000000
            addBulletLeftToList(startBulletShotTimeElapsed)
            addBulletRightToList(startBulletShotTimeElapsed)
            updateBulletLeft()
            updateBulletRight()
            addEnemyLeftToList(startEnemyLeftTimeElapsed)
            addEnemyRightToList(startEnemyRightTimeElapsed)
            updateEnemyLeft()
            updateEnemyRight()
            checkCollisionBetweenEnemyLeftAndBouncer()
            checkCollisionBetweenEnemyRightAndBouncer()
            checkCollisionBetweenEnemyLeftAndBulletRight()
            checkCollisionBetweenEnemyRightAndBulletLeft()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        bouncerBackground!!.drawBouncerBackground(canvas!!)
        bouncer!!.drawBouncer(canvas)
        bouncerButtons!!.drawBouncerButtons(canvas)
        bulletLeftList?.forEach { bulletLeft ->
            bulletLeft.drawBullet(canvas)
        }
        bulletRightList?.forEach { bulletRight ->
            bulletRight.drawBullet(canvas)
        }
        enemyLeftList?.forEach { enemyLeft ->
            enemyLeft.drawEnemy(canvas)
        }
        enemyRightList?.forEach { enemyRight ->
            enemyRight.drawEnemy(canvas)
        }
        drawScore(canvas)
        drawBestScore(canvas)
        when(gameState) {
            0 -> drawStartText(canvas)
            2 -> drawEndText(canvas)
        }
    }

    private fun drawScore(canvas: Canvas) {
        val paint = Paint()
        paint.apply {
            textSize = 60f
            color = Color.WHITE
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Game score: $gameScore", 50f, 80f, paint)
    }

    private fun drawBestScore(canvas: Canvas) {
        val paint = Paint()
        paint.apply {
            textSize = 70f
            color = Color.YELLOW
            style = Paint.Style.FILL
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Best score: ${sharedPrefs.getInt("bestScore", 0)}", SCREEN_WIDTH - 100f, 80f, paint)
    }

    private fun drawStartText(canvas: Canvas) {
        val paint = Paint()
        paint.apply {
            color = Color.GREEN
            style = Paint.Style.FILL
            textSize = 70f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.SANS_SERIF
        }
        canvas.drawText("Touch the screen to start playing!", (SCREEN_WIDTH / 2).toFloat(), (SCREEN_HEIGHT / 2).toFloat(), paint)
    }

    private fun drawEndText(canvas: Canvas) {
        val paint = Paint()
        paint.apply {
            color = Color.RED
            style = Paint.Style.FILL
            textSize = 70f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.SANS_SERIF
        }
        canvas.drawText("You lose ! Tap the screen to start playing again!", (SCREEN_WIDTH / 2).toFloat(), (SCREEN_HEIGHT / 2).toFloat(), paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                gameState = 1
                if ((event.x >= bouncerButtons!!.bouncerButtonLeftXPosition) && event.x <= (bouncerButtons!!.bouncerButtonLeftXPosition + bouncerButtons!!.bouncerButtonLeftImage.width) && (event.y >= bouncerButtons!!.bouncerButtonsYPosition)
                    && event.y <= (bouncerButtons!!.bouncerButtonsYPosition + bouncerButtons!!.bouncerButtonLeftImage.height)) {
                    bouncerButtons!!.isNoButtonPressed = false
                    bouncerButtons!!.isBouncerButtonRightPressed = false
                    bouncerButtons!!.isBouncerButtonLeftPressed = true
                }
                else if ((event.x >= bouncerButtons!!.bouncerButtonRightXPosition) && event.x <= (bouncerButtons!!.bouncerButtonRightXPosition + bouncerButtons!!.bouncerButtonRightImage.width) && (event.y >= bouncerButtons!!.bouncerButtonsYPosition)
                    && event.y <= (bouncerButtons!!.bouncerButtonsYPosition + bouncerButtons!!.bouncerButtonRightImage.height)) {
                    bouncerButtons!!.isNoButtonPressed = false
                    bouncerButtons!!.isBouncerButtonLeftPressed = false
                    bouncerButtons!!.isBouncerButtonRightPressed = true
                }
            }
            MotionEvent.ACTION_UP -> {
                bouncerButtons!!.isNoButtonPressed = true
                bouncerButtons!!.isBouncerButtonLeftPressed = false
                bouncerButtons!!.isBouncerButtonRightPressed = false
            }
        }
        return true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        bouncerBackground = BouncerBackground(getScaledBouncerBackground())
        bouncer = Bouncer(BitmapFactory.decodeResource(resources, R.drawable.bouncer_image))
        bouncerButtons = BouncerButtons(BitmapFactory.decodeResource(resources, R.drawable.bouncer_button_left_image), BitmapFactory.decodeResource(resources, R.drawable.bouncer_button_right_image))
        bulletLeftList = mutableListOf()
        bulletRightList = mutableListOf()
        enemyLeftList = mutableListOf()
        enemyRightList = mutableListOf()
        startTimeBulletShot = System.nanoTime()
        startTimeEnemyLeftSpawns = System.nanoTime()
        startTimeEnemyRightSpawns = System.nanoTime()
        bouncerLooping!!.apply {
            setGameRunning(true)
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                bouncerLooping!!.apply {
                    setGameRunning(false)
                    join()
                }
            } catch (e: Exception) { e.printStackTrace() }
            retry = false
        }
    }

}