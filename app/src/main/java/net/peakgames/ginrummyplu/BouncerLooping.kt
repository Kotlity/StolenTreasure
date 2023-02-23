package net.peakgames.ginrummyplu

import android.graphics.Canvas
import android.view.SurfaceHolder

class BouncerLooping(private val surfaceHolder: SurfaceHolder, private val surfaceView: BouncerSurfaceView): Thread() {

    private var isGameRunning: Boolean = false

    fun setGameRunning(running: Boolean) {
        isGameRunning = running
    }

    private val FPS = 60

    override fun run() {
        var startRunningTime: Long
        var afterOperationsTime: Long
        var waitTime: Long
        val targetTime = (1000 / FPS).toLong()

        while (isGameRunning) {
            startRunningTime = System.nanoTime()
            canvas = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    surfaceView.updateObjects()
                    surfaceView.draw(canvas!!)
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally {
                if (canvas != null) {
                    try { surfaceHolder.unlockCanvasAndPost(canvas) }
                    catch (e: Exception) { e.printStackTrace() }
                }
            }

            afterOperationsTime = (System.nanoTime() - startRunningTime) / 1000000
            waitTime = targetTime - afterOperationsTime

            try { sleep(waitTime) }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    companion object { private var canvas: Canvas? = null }
}