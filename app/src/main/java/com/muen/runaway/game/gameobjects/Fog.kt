package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Location
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import kotlin.math.pow


class Fog(game: Game?) : GameObject(game!!) {
    override fun update(elapsedTime: Long) {
        super.update(elapsedTime)
        if (checkForPlayer()) {
            state["status"] = "visible"
        } else {
            val status = state.get<String>("status")
            if (status === "visible") {
                state["status"] = "visited"
            }
        }
    }

    private fun checkForPlayer(): Boolean {
        val player = game.getGameObjectWithTag("player")
        val playerLocation: Location = player!!.state.get("coords")
        val myLocation: Location = state.get("coords")
        val distance = Math.sqrt(
            (playerLocation.x - myLocation.x).toDouble().pow(2.0) + (playerLocation.y - myLocation.y).toDouble()
                .pow(2.0)
        )
        return distance < 3
    }

    override fun render(canvas: Canvas, paint: Paint) {
        // I don't recommend making changes to this file other than
        // to comment out the drawing portion for easier debugging
        val coords: Location = state["coords"]
        val cellSize: Int = game.gameState["cellSize"]
        val myX = coords.x * cellSize
        val myY = coords.y * cellSize
        val status: String = state["status"]
        canvas.translate(myX, myY)

        // COMMENT OUT THESE LINES WHEN DEBUGGING
        if (status == "hidden") {
            canvas.drawRect(0f, 0f, cellSize.toFloat(), cellSize.toFloat(), paint)
        } else if (status == "visited") {
            paint.color = Color.argb(.5f, 0f, 0f, 0f)
            canvas.drawRect(0f, 0f, cellSize.toFloat(), cellSize.toFloat(), paint)
        } // don't draw anything if visible
        // END COMMENT OUT SECTION

    }

    init {
        state["status"] = "hidden"
    }
}
