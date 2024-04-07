package com.muen.runaway.game.gameengine

import android.graphics.Canvas
import android.graphics.Paint


abstract class GameObject(protected var game: Game) {
    var state = State()
        protected set

    open fun update(elapsedTime: Long) {}
    open fun render(canvas: Canvas, paint: Paint) {}
}