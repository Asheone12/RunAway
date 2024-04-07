package com.muen.runaway.game.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import com.muen.runaway.game.gameengine.Location
import java.util.*
import kotlin.math.roundToInt


class BossMonster(game: Game?) : GameObject(game!!) {
    var pattern: ArrayList<Int>? = null
    var turnNumber = 0
    override fun update(elapsedTime: Long) {
        if (pattern == null) {
            pattern = ArrayList()
            pattern!!.add(0)
            pattern!!.add(1)
            for (i in 0 until state.get("level")) {
                val move = Math.random().roundToInt()
                pattern!!.add(move)
            }
        }
        val isAlive = state.get<Boolean>("alive")
        val turn = game.gameState.get<String>("turn")
        if (turn !== "monster") return
        game.gameState["endTurn"] = true
        if (!isAlive) return
        val action = pattern!![turnNumber % pattern!!.size]
        if (action == 1) { // move
            turnNumber++
            val map = game.gameState.get<Array<Array<GameObject?>>>("map")
            if (checkForPlayer()) {
                val player = game.getGameObjectWithTag("player")
                val playerLocation: Location = player!!.state["coords"]
                val myLocation: Location = state["coords"]
                if (myLocation.x != playerLocation.x && myLocation.y != playerLocation.y) {
                    if (myLocation.y < playerLocation.y) {
                        val other = map[myLocation.y.toInt() + 1][myLocation.x.toInt()]
                        if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.y = myLocation.y + 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    if (myLocation.y > playerLocation.y) {
                        val other = map[myLocation.y.toInt() - 1][myLocation.x.toInt()]
                        if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.y = myLocation.y - 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    if (myLocation.x < playerLocation.x) {
                        val other = map[myLocation.y.toInt()][myLocation.x.toInt() + 1]
                        if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.x = myLocation.x + 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    if (myLocation.x > playerLocation.x) {
                        val other = map[myLocation.y.toInt()][myLocation.x.toInt()]
                        if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.x = myLocation.x - 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    moveRandom()
                } else if (myLocation.x == playerLocation.x) { // same column
                    if (myLocation.y < playerLocation.y) {
                        val other = map[myLocation.y.toInt()][myLocation.x.toInt()]
                        if (other is Player) {
                            // end the game
                            other.state["alive"] = false
                            game.gameState["playing"] = false
                            return
                        } else if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.y = myLocation.y + 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    if (myLocation.y > playerLocation.y) {
                        val other = map[myLocation.y.toInt() - 1][myLocation.x.toInt()]
                        if (other is Player) {
                            // end the game
                            other.state["alive"] = false
                            game.gameState["playing"] = false
                            return
                        } else if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.y = myLocation.y - 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    moveRandom()
                } else if (myLocation.y == playerLocation.y) { // same row
                    if (myLocation.x < playerLocation.x) {
                        val other = map[myLocation.y.toInt()][myLocation.x.toInt() + 1]
                        if (other is Player) {
                            // end the game
                            other.state["alive"] = false
                            game.gameState["playing"] = false
                            return
                        } else if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.x = myLocation.x + 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    if (myLocation.x > playerLocation.x) {
                        val other = map[myLocation.y.toInt()][myLocation.x.toInt() - 1]
                        if (other is Player) {
                            // end the game
                            other.state["alive"] = false
                            game.gameState["playing"] = false
                            return
                        } else if (other == null) {
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                            myLocation.x = myLocation.x - 1
                            map[myLocation.y.toInt()][myLocation.x.toInt()] = this
                            return
                        }
                    }
                    moveRandom()
                }
            } else {
                moveRandom()
            }
        } else {
            turnNumber++
        }
    }

    private fun moveRandom() {
        val neighbors = mutableListOf<Int>()
        neighbors.add(1)
        neighbors.add(2)
        neighbors.add(3)
        neighbors.add(4)
        neighbors.shuffle()
        val map = game.gameState.get<Array<Array<GameObject?>>>("map")
        val myLocation: Location = state.get("coords")
        while (neighbors.isNotEmpty()) {
            val `val` = neighbors.removeAt(0)
            if (`val` == 1) {
                if (myLocation.y > 0 && map[myLocation.y.toInt() - 1][myLocation.x.toInt()] == null) {
                    map[myLocation.y.toInt() - 1][myLocation.x.toInt()] = this
                    map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                    myLocation.y = myLocation.y - 1
                    return
                }
            }
            if (`val` == 2) {
                if (myLocation.x < map[0].size - 1 && map[myLocation.y.toInt()][myLocation.x.toInt() + 1] == null) {
                    map[myLocation.y.toInt()][myLocation.x.toInt() + 1] = this
                    map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                    myLocation.x = myLocation.x + 1
                    return
                }
            }
            if (`val` == 3) {
                if (myLocation.y < map.size - 1 && map[myLocation.y.toInt() + 1][myLocation.x.toInt()] == null) {
                    map[myLocation.y.toInt() + 1][myLocation.x.toInt()] = this
                    map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                    myLocation.y = myLocation.y + 1
                    return
                }
            }
            if (`val` == 4) {
                if (myLocation.x > 0 && map[myLocation.y.toInt()][myLocation.x.toInt() - 1] == null) {
                    map[myLocation.y.toInt()][myLocation.x.toInt() - 1] = this
                    map[myLocation.y.toInt()][myLocation.x.toInt()] = null
                    myLocation.x = myLocation.x - 1
                    return
                }
            }
        }
    }

    private fun checkForPlayer(): Boolean {
        val player = game.getGameObjectWithTag("player")
        val playerLocation: Location = player!!.state.get("coords")
        val myLocation: Location = state.get("coords")
        val distance = Math.sqrt(
            Math.pow(
                (playerLocation.x - myLocation.x).toDouble(),
                2.0
            ) + Math.pow((playerLocation.y - myLocation.y).toDouble(), 2.0)
        )
        return distance < 5
    }

    override fun render(canvas: Canvas, paint: Paint) {
//        things you can do in this render method for reference.
//        val coords: Location = state["coords"] // gets the location of the object in the grid
//        val cellSize: Int = game.gameState["cellSize"] // gets the size of each cell in the game
//        val myX = coords.x * cellSize // gets the current x position (in pixels) in screen space
//        val myY = coords.y * cellSize // gets the current y position (in pixels) in screen space
//        val alive: Boolean = state["alive"] // get whether or not the monster is alive
//        val level: Int = state["level"]; // gets the difficulty level of the monster
//        val health: Int = state["health"]; // gets how much health the monster has left
        val coords: Location = state["coords"]
        val cellSize: Int = game.gameState["cellSize"]
        val myX = coords.x * cellSize
        val myY = coords.y * cellSize
        canvas.translate(myX, myY)
        val alive: Boolean = state["alive"]
        if (alive) {
            paint.color = Color.GRAY
            canvas.drawCircle(-15f,84f, 10f, paint)
            canvas.drawCircle(0f,97f, 19f, paint)
            canvas.drawCircle(35f,120f, 25f, paint)
            canvas.drawCircle(80f,120f, 30f, paint)
            canvas.drawCircle(120f,100f, 35f, paint)
            canvas.drawCircle(150f,50f, 40f, paint)
            canvas.drawCircle(200f,40f, 60f, paint)
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawCircle(-15f,84f, 5f, paint)
            canvas.drawCircle(0f,97f, 14f, paint)
            canvas.drawCircle(35f,120f, 20f, paint)
            canvas.drawCircle(80f,120f, 25f, paint)
            canvas.drawCircle(120f,100f, 30f, paint)
            canvas.drawCircle(150f,50f, 35f, paint)
            canvas.drawCircle(200f,40f, 55f, paint)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(230f,27f, 17f, paint)
        } else {
            paint.color = Color.GRAY
            canvas.drawCircle(40f,100f, 40f, paint)
            canvas.drawCircle(70f,60f, 45f, paint)
            canvas.drawCircle(100f,100f, 40f, paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.color = Color.BLACK
            canvas.drawCircle(40f, 100f,35f,paint )
            canvas.drawCircle(70f,60f, 40f, paint)
            canvas.drawCircle(100f,100f, 35f, paint)
            paint.style = Paint.Style.FILL
            paint.color = Color.RED
            canvas.drawCircle(90f,50f, 15f, paint)

        }





    }

    init {
        state["alive"] = true
        state["health"] = 6
        state["level"] = 1 // will be overridden by game after instantiation
    }
}