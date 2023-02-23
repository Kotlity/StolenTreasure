package net.peakgames.ginrummyplu

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class Enemy(private val enemyImage: Bitmap, private var enemyPositionX: Int, private val enemyPositionY: Int) {

    private val enemyMovementSpeed = 10

    fun enemyRectangle() = Rect(enemyPositionX, enemyPositionY, enemyPositionX + enemyImage.width, enemyPositionY + enemyImage.height)

    fun updateEnemyLeftMovement() {
        enemyPositionX -= enemyMovementSpeed
    }

    fun updateEnemyRightMovement() {
        enemyPositionX += enemyMovementSpeed
    }

    fun drawEnemy(canvas: Canvas) {
        canvas.drawBitmap(enemyImage, enemyPositionX.toFloat(), enemyPositionY.toFloat(), null)
    }
}