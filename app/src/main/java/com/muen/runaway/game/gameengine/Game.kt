package com.muen.runaway.game.gameengine

import android.graphics.Canvas
import android.graphics.Paint
import java.util.function.Consumer


abstract class Game(val width: Float, val height: Float) {
    val gameState = State()
    private val taggedObjs = HashMap<String, GameObject>()
    val layers = mutableListOf<Layer>()
    val inputQueue = mutableListOf<Input>()

    fun enqueueInput(type: Input.Type, x: Float, y: Float) {
        val newInput = Input(type, Location(x, y))
        inputQueue.add(newInput)
    }

    fun clearInputQueue() {
        inputQueue.clear()
    }

    fun tagObj(tag: String, obj: GameObject) {
        taggedObjs[tag] = obj
    }

    fun getGameObjectWithTag(tag: String): GameObject? {
        return taggedObjs[tag]
    }

    open fun init() {}
    abstract fun doFrame(deltaTime: Long)
    fun render(canvas: Canvas, paint: Paint) {
        layers.forEach(Consumer { layer: Layer ->
            layer.gameObjects.forEach { go ->
                canvas.save()
                go.render(canvas, paint)
                paint.reset()
                canvas.restore()
            }
        })
    }

    init {
        init()
    }
}
