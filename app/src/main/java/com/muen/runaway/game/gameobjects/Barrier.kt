package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import com.muen.runaway.game.gameengine.Location

class Barrier(game: Game) : GameObject(game) {
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

        paint.color = Color.rgb(210,180,140)
        canvas.drawRect(0f, 0f, cellSize.toFloat(), cellSize.toFloat(), paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth=5f
        canvas.drawRect(10f,5f,cellSize.toFloat()-115f,cellSize.toFloat()-5f,paint)
        canvas.drawRect(50f,5f,cellSize.toFloat()-75f,cellSize.toFloat()-5f,paint)
        canvas.drawRect(90f,5f,cellSize.toFloat()-35f,cellSize.toFloat()-5f,paint)
        canvas.drawRect(130f,5f,cellSize.toFloat()+5f,cellSize.toFloat()-5f,paint)
    }

}