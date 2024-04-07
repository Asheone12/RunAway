package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import com.muen.runaway.game.gameengine.Location


class Floor(game: Game?) : GameObject(game!!) {
    override fun render(canvas: Canvas, paint: Paint) {
//        things you can do in this render method for reference.
//        val coords: Location = state["coords"] // gets the location of the object in the grid
//        val cellSize: Int = game.gameState["cellSize"] // gets the size of each cell in the game
//        val myX = coords.x * cellSize // gets the current x position (in pixels) in screen space
//        val myY = coords.y * cellSize // gets the current y position (in pixels) in screen space

        val coords: Location = state["coords"]
        val cellSize: Int = game.gameState["cellSize"]
        val myX = coords.x * cellSize
        val myY = coords.y * cellSize

        canvas.translate(myX, myY)
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        paint.strokeWidth = 2f
        canvas.drawRect(0f, 0f, cellSize.toFloat(), cellSize.toFloat(), paint)
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(112,128,144)
        canvas.drawRect(5f, 5f, cellSize.toFloat()-5f, cellSize.toFloat()-5f, paint)
        paint.style= Paint.Style.STROKE
        paint.strokeWidth = 7f
        paint.color = Color.rgb(0,0,0)
        canvas.drawRect(12f, 12f, cellSize.toFloat()-72f, cellSize.toFloat()-72f, paint)
        canvas.drawRect(72f, 12f, cellSize.toFloat()-12f, cellSize.toFloat()-72f, paint)
        canvas.drawRect(12f, 72f, cellSize.toFloat()-72f, cellSize.toFloat()-12f, paint)
        canvas.drawRect(72f, 72f, cellSize.toFloat()-12f, cellSize.toFloat()-12f, paint)
    }
}
