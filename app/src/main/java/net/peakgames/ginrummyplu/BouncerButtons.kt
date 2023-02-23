package net.peakgames.ginrummyplu

import android.graphics.Bitmap
import android.graphics.Canvas
import net.peakgames.ginrummyplu.BouncerScreenSizeConstants.SCREEN_HEIGHT

class BouncerButtons(val bouncerButtonLeftImage: Bitmap, val bouncerButtonRightImage: Bitmap) {

    var isNoButtonPressed = true
    var isBouncerButtonLeftPressed = false
    var isBouncerButtonRightPressed = false

    val bouncerButtonLeftXPosition = 100
    val bouncerButtonRightXPosition = 300
    val bouncerButtonsYPosition = SCREEN_HEIGHT - 150

    fun drawBouncerButtons(canvas: Canvas) {
        canvas.drawBitmap(bouncerButtonLeftImage, bouncerButtonLeftXPosition.toFloat(), bouncerButtonsYPosition.toFloat(), null)
        canvas.drawBitmap(bouncerButtonRightImage, bouncerButtonRightXPosition.toFloat(), bouncerButtonsYPosition.toFloat(), null)
    }
}