package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject


class GameOverMessage(game: Game?) : GameObject(game!!) {
    override fun render(canvas: Canvas, paint: Paint) {
        // this object is a little different than the others as is doesn't have a position inherently.
        // its position is determined at render time.
        // here are some of the things that will be useful for you.
        // game.height // get the height of the game
        // game.width; // get the width of the game
        val isPlaying: Boolean = game.gameState["playing"]
        if (isPlaying) return

        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, 2000f, 2000f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(525f, game.height/2,300f, paint)
        canvas.drawRect(300f,game.height/2, 750f, game.height/2 +350, paint)
        paint.color = Color.BLACK
        canvas.drawCircle(375f,game.height/2 -100f,100f,paint)
        canvas.drawCircle(675f,game.height/2 - 100f,100f,paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f
        canvas.drawRect(320f, game.height/2+220f,730f,game.height/2 +330f, paint)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.textSize = 170f
        canvas.drawText("GAME OVER", 50f, game.height /2, paint)
    }
}