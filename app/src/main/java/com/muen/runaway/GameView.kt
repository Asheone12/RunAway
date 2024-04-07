package com.muen.runaway
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.MotionEvent
import android.view.View
import com.muen.runaway.game.gameengine.Input
import com.muen.runaway.game.RougeLike
import com.muen.runaway.game.gameengine.Game

class GameView(context: Context?) : View(context), FrameCallback {
    var game: Game? = null
    var paint = Paint()
    var time: Long = 0
    @SuppressLint("ClickableViewAccessibility")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (game == null) {
            time = System.nanoTime()
            game = RougeLike(width.toFloat(), height.toFloat())
            setOnTouchListener { _: View?, motionEvent: MotionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    game?.enqueueInput(Input.Type.Touch, motionEvent.x, motionEvent.y)
                    return@setOnTouchListener true
                }
                false
            }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        game?.render(canvas, paint)
    }

    override fun doFrame(l: Long) {
        val deltaT = l - time
        time = l
        game?.doFrame(deltaT)
        Choreographer.getInstance().postFrameCallback(this)
        invalidate()
    }
}