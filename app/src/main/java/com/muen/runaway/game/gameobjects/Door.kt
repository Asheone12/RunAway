package com.muen.runaway.game.gameobjects


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import com.muen.runaway.game.gameengine.Location


class Door(game: Game?) : GameObject(game!!) {
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
        //paint.style = Paint.Style.STROKE
        //paint.strokeWidth = 2f
        paint.color = Color.rgb(222,184,135)
        canvas.drawRect(1f, 1f, (cellSize - 1).toFloat(), (cellSize - 1).toFloat(), paint)
        paint.color = Color.rgb(139,69,19)
        canvas.drawRect(10f, 10f, (cellSize - 10).toFloat(), (cellSize - 10).toFloat(), paint)
        paint.color = Color.rgb(184,134,11)
        canvas.drawCircle((cellSize-35).toFloat(),80f,20f,paint)
        paint.color=Color.GRAY
        canvas.drawRect(115f,100f,cellSize.toFloat()-29f,cellSize.toFloat()-13f, paint)
    }
}