package net.peakgames.ginrummyplu

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class Bullet(private val bulletImage: Bitmap, private var bulletPositionX: Int, private val bulletPositionY: Int) {

    private val bulletMovementSpeed = 18

    fun bulletRectangle() = Rect(bulletPositionX, bulletPositionY, bulletPositionX + bulletImage.width, bulletPositionY + bulletImage.height)

    fun updateBulletLeftMovement() {
        bulletPositionX -= bulletMovementSpeed
    }

    fun updateBulletRightMovement() {
        bulletPositionX += bulletMovementSpeed
    }

    fun drawBullet(canvas: Canvas) {
        canvas.drawBitmap(bulletImage, bulletPositionX.toFloat(), bulletPositionY.toFloat(), null)
    }
}