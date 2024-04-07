package com.muen.runaway.game.gameengine

import com.muen.runaway.game.gameengine.GameObject


class Layer {
    var isStatic = false
    var gameObjects = mutableListOf<GameObject>()
}