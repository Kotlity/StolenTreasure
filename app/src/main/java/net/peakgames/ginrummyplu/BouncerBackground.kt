package net.peakgames.ginrummyplu

import android.graphics.Bitmap
import android.graphics.Canvas
import net.peakgames.ginrummyplu.BouncerScreenSizeConstants.SCREEN_WIDTH

class BouncerBackground(private val bouncerBackgroundImage: Bitmap) {

    private var bouncerBackgroundImagePositionX = 0
    private val bouncerBackgroundImagePositionY = 0
    private val bouncerBackgroundMovingSpeed = -2

    fun updateBouncerBackground() {
        bouncerBackgroundImagePositionX += bouncerBackgroundMovingSpeed
        if (bouncerBackgroundImagePositionX < - SCREEN_WIDTH) bouncerBackgroundImagePositionX = 0
    }

    fun drawBouncerBackground(canvas: Canvas) {
        canvas.drawBitmap(bouncerBackgroundImage, bouncerBackgroundImagePositionX.toFloat(), bouncerBackgroundImagePositionY.toFloat(), null)
        if (bouncerBackgroundImagePositionX < 0) canvas.drawBitmap(bouncerBackgroundImage, (bouncerBackgroundImagePositionX + SCREEN_WIDTH).toFloat(), bouncerBackgroundImagePositionY.toFloat(), null)
    }
}