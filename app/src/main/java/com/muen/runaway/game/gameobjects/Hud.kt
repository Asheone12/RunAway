package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Location
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject


class Hud(game: Game?) : GameObject(game!!) {
    override fun render(canvas: Canvas, paint: Paint) {
        // I don't recommend making changes to this file
        val coords: Location = state["coords"]
        val myX = coords.x
        val myY = coords.y
        canvas.translate(myX, myY)
        val level = game.gameState.get<Int>("level")
        paint.textSize = 60f
        val player = game.getGameObjectWithTag("player")
        val hasKey = player!!.state.get<Boolean>("hasKey")
        canvas.drawText("LEVEL: $level  KEY: $hasKey", 15f, -15f, paint)
    }
}
