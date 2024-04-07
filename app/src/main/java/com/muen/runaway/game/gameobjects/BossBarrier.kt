package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import com.muen.runaway.game.gameengine.Location


class BossBarrier(game: Game?) : GameObject(game!!) {
    override fun render(canvas: Canvas, paint: Paint) {
//        things you can do in this render method for reference.
//        val coords: Location = state["coords"] // gets the location of the object in the grid
//        val cellSize: Int = game.gameState["cellSize"] // gets the size of each cell in the game
//        val myX = coords.x * cellSize // gets the current x position (in pixels) in screen space
//        val myY = coords.y * cellSize // gets the current y position (in pixels) in screen space
        val coords: Location = state["coords"]
        val cellSize: Float = game.gameState["cellSize"]
        val myX = coords.x * cellSize
        val myY = coords.y * cellSize

        canvas.translate(myX, myY)

        paint.color = Color.GRAY
        canvas.drawRect(0f, 0f, cellSize.toFloat(), cellSize.toFloat(), paint)
        paint.color = Color.rgb(128,0,0)
        canvas.drawRect(4f, 4f, cellSize.toFloat()-4f, cellSize.toFloat()-4f, paint)
        paint.color = Color.GRAY
        canvas.drawRect(10f, 10f, cellSize.toFloat()-70f, cellSize.toFloat()-80f, paint)
        paint.color = Color.rgb(128,0,0)
        canvas.drawRect(14f, 14f, cellSize.toFloat()-74f, cellSize.toFloat()-84f, paint)
        paint.color = Color.GRAY
        canvas.drawRect(90f, 100f, cellSize.toFloat(), cellSize.toFloat(), paint)
        paint.color = Color.rgb(128,0,0)
        canvas.drawRect(94f, 104f, cellSize.toFloat()-4f, cellSize.toFloat()-4f, paint)
    }
}