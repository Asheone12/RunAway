package com.muen.runaway.game

import com.muen.runaway.game.gameobjects.Barrier
import com.muen.runaway.game.gameobjects.BossBarrier
import com.muen.runaway.game.gameobjects.BossFloor
import com.muen.runaway.game.gameobjects.BossMonster
import com.muen.runaway.game.gameobjects.Door
import com.muen.runaway.game.gameobjects.Floor
import com.muen.runaway.game.gameobjects.Fog
import com.muen.runaway.game.gameobjects.GameOverMessage
import com.muen.runaway.game.gameobjects.Hud
import com.muen.runaway.game.gameobjects.Key
import com.muen.runaway.game.gameobjects.Monster
import com.muen.runaway.game.gameobjects.Player
import com.muen.runaway.game.gameengine.Layer
import com.muen.runaway.game.gameengine.Location
import com.muen.runaway.game.gameengine.State
import com.muen.runaway.game.gameengine.Game
import com.muen.runaway.game.gameengine.GameObject
import java.util.*


class RougeLike(width: Float, height: Float) : Game(width, height) {
    var numCols = 0
    var numRows = 0
    var cellSize = 0

    // this is an internal reference for all important objects
    lateinit var player: GameObject
    lateinit var key: GameObject
    lateinit var door: GameObject
    lateinit var hud: GameObject
    lateinit var gameOverMessage: GameObject

    // these act as object pools so each object can be reused between levels
    lateinit var monsters: ArrayList<GameObject>
    lateinit var barriers: ArrayList<GameObject>
    lateinit var floorTiles: ArrayList<GameObject>
    lateinit var fogTiles: ArrayList<GameObject>
    lateinit var bossFloorTiles: ArrayList<GameObject>
    lateinit var bossBarriers: ArrayList<GameObject>

    // init will be called automatically after instantiation is complete.
    override fun init() {
        numCols = 7
        cellSize = width.toInt() / numCols
        numRows = (height - 50).toInt() / cellSize
        println()
        // initialize layers
        // Layers describe the drawing order of game objects, in other words
        // objects in layer 1 will be drawn over top of objects in layer 0.
        // only object contained in the layers structure will be updated and rendered.

        // these 3 layers are static, meaning they will only be rendered and not updated
        // they don't need to be updated because they will never change once created.
        layers.add(Layer()) // floor
        layers[0].isStatic = true
        layers.add(Layer()) // barriers
        layers[1].isStatic = true
        layers.add(Layer()) // key, door
        layers[2].isStatic = true

        // these are dynamic layers, each object in these layers will
        // be updated in realtime
        layers.add(Layer()) // monsters,
        layers.add(Layer()) // player
        layers.add(Layer()) // fog
        layers.add(Layer()) // UI

        // initialize game state
        val gameState: State = gameState
        gameState["level"] = 1
        gameState["playing"] = true // will set to false when player dies
        gameState["cellSize"] = cellSize
        gameState["numRows"] = numRows
        gameState["numCols"] = numCols
        gameState["turn"] = "player"
        gameState["nextLevel"] = false
        gameState["endTurn"] = false
        gameState["bossLevel"] = 1

        // init consistent objects
        player = Player(this)
        tagObj("player", player)
        key = Key(this)
        door = Door(this)
        hud = Hud(this)
        hud.state["coords"] = Location(0f, height)
        gameOverMessage = GameOverMessage(this)

        // Prepare object pools
        floorTiles = ArrayList()
        monsters = ArrayList()
        barriers = ArrayList()
        fogTiles = ArrayList()
        bossBarriers = ArrayList()
        bossFloorTiles = ArrayList()
        // init floor tiles
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val floor: GameObject = Floor(this)
                floor.state["coords"] = Location(j.toFloat(), i.toFloat())
                floorTiles.add(floor)
            }
        }
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val floor: GameObject = BossFloor(this)
                floor.state["coords"] = Location(j.toFloat(), i.toFloat())
                bossFloorTiles.add(floor)
            }
        }

        // init fog tiles
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val fog: GameObject = Fog(this)
                fog.state["coords"] = Location(j.toFloat(), i.toFloat())
                fogTiles.add(fog)
            }
        }

        // build the first level
        buildNewLevel()
    }

    // all games follow the same basic pattern
    // while(playing):
    //   update everything
    //   draw everything
    //
    // each step should work independendent of the other steps
    // doFrame is the update method
    override fun doFrame(deltaTime: Long) {
        // update all game objects over time
        layers.forEach { layer ->
            if (!layer.isStatic) {
                layer.gameObjects.forEach { go -> go.update(deltaTime) }
            }
        }

        // check to see if either the player or the monsters performed their turn.
        // player performs turn by tapping on a square
        // monsters automatically perform their turn based on their AI
        val endTurn = gameState.get<Boolean>("endTurn")

        // if turn was ended then swap the turn state to whoever's turn it is next.
        if (endTurn) {
            val turn = gameState.get<String>("turn")
            if (turn === "player") {
                gameState["turn"] = "monster"
            } else {
                gameState["turn"] = "player"
            }
            gameState["endTurn"] = false
        }

        // check to see if the player completed the level
        val nextLevel = gameState.get<Boolean>("nextLevel")

        // if player completed level then reset and rebuild new level.
        if (nextLevel) {
            key.state["active"] = true
            player.state["hasKey"] = false
            val currentLevel = gameState.get<Int>("level")
            gameState["level"] = currentLevel + 1
            gameState["turn"] = "player"

            // every 5 levels is a boss level
            // as the game goes on the boss gets harder and harder.
            if (currentLevel + 1 % 5 == 0) {
                gameState["bossLevel"] = gameState.get<Int>("bossLevel") + 1
            }
            buildNewLevel()
            gameState["nextLevel"] = false
        }
    }

    // the rest of the code in this file is dedicated to level generation.
    // the following steps are performed:
    // 1. Generate a primitive version of the level using char[][] (more details on that further down)
    // 2. Clear out all layers and pool / reset existing objects for reuse.
    // 3. Transform primitive level into complex level with game objects
    //      a. New objects are only created as needed (for example if there are more monsters in this level)
    // 4. Store the map in the gameState for game objects to reference at run time
    // 5. Copy objects into layers for updating and rendering.
    fun buildNewLevel() {
        val newLevel = generateLevel()
        val currentLevelNumber = gameState.get<Int>("level")
        val isBossLevel = currentLevelNumber % 5 == 0
        // pool current layer objects
        layers.forEach { layer ->
            layer.gameObjects.forEach { gameObject ->
                if (gameObject is Barrier) {
                    barriers.add(gameObject)
                }
                if (gameObject is Floor) {
                    floorTiles.add(gameObject)
                }
                if (gameObject is BossBarrier) {
                    bossBarriers.add(gameObject)
                }
                if (gameObject is BossFloor) {
                    bossFloorTiles.add(gameObject)
                }
                if (gameObject is Monster) {
                    gameObject.state["alive"] = true
                    monsters.add(gameObject)
                }
                if (gameObject is Fog) {
                    gameObject.state["status"] = "hidden"
                    fogTiles.add(gameObject)
                }
            }
            layer.gameObjects.clear()
        }
        val map = Array(numRows) {
            arrayOfNulls<GameObject>(
                numCols
            )
        }
        // create game objects
        for (i in newLevel.indices) {
            for (j in 0 until newLevel[i].size) {
                var obj: GameObject? = null
                if (newLevel[i][j] == '*') {
                    obj = if (isBossLevel) {
                        if (bossBarriers.isEmpty()) BossBarrier(this) else bossBarriers.removeAt(
                            0
                        )
                    } else {
                        if (barriers.isEmpty()) Barrier(this) else barriers.removeAt(0)
                    }
                    layers[1].gameObjects.add(obj)
                }
                if (newLevel[i][j] == 'B') {
                    obj = if (monsters.isEmpty()) Monster(this) else monsters.removeAt(0)
                    layers[3].gameObjects.add(obj)
                }
                if (newLevel[i][j] == 'K' && !isBossLevel) {
                    obj = key
                    layers[2].gameObjects.add(obj)
                }
                if (newLevel[i][j] == 'E') {
                    obj = door
                    layers[2].gameObjects.add(obj)
                }
                if (newLevel[i][j] == 'P') {
                    obj = player
                    layers[4].gameObjects.add(obj)
                }
                if (newLevel[i][j] == 'Z') {
                    obj = BossMonster(this)
                    obj.state["level"] = gameState["bossLevel"]
                    layers[3].gameObjects.add(obj)
                }
                if (obj == null) continue
                obj.state["coords"] = Location(j.toFloat(), i.toFloat())
                map[i][j] = obj
            }
        }
        gameState["map"] = map

        // copy objects into layers.
        if (isBossLevel) {
            layers[0].gameObjects.addAll(bossFloorTiles)
            bossFloorTiles.clear()
        } else {
            layers[0].gameObjects.addAll(floorTiles)
            floorTiles.clear()
        }
        layers[6].gameObjects.add(hud)
        layers[6].gameObjects.add(gameOverMessage)
        layers[5].gameObjects.addAll(fogTiles)
        fogTiles.clear()
    }

    // this method generates a primite version of the level
    // the following is how each object is represented
    // ' ' = floor
    // '*' = wall / barrier
    // 'P' = player
    // 'B' = monster
    // 'Z' = boss monster
    // 'K' = key
    // 'E' = exit
    fun generateLevel(): Array<CharArray> {
        val level = Array(numRows) {
            CharArray(
                numCols
            )
        }
        val currentLevelNumber = gameState.get<Int>("level")
        addFloor(level)
        generateTerrain(level)
        addPlayer(level)
        addExit(level)
        addBandits(level, currentLevelNumber)
        if (currentLevelNumber % 5 == 0) {
            addExtra(level, 'Z')
        }
        addExtra(level, 'K')
        return level
    }

    companion object {
        fun addBandits(level: Array<CharArray>, currentLevel: Int) {
            var count = (Math.log(currentLevel.toDouble()) / Math.log(2.0)).toInt()
            if (count == 0) {
                count = 1
            }
            for (i in 0 until count) {
                addExtra(level, 'B')
            }
        }

        fun addExtra(level: Array<CharArray>, extra: Char) {
            val rng = Random()
            var row = rng.nextInt(level.size - 2)
            var col = rng.nextInt(level[0].size - 2)
            var placed = false
            while (!placed) {
                if (level[row][col] != ' ') {
                    if (row == 0) {
                        col--
                    } else {
                        row--
                    }
                } else {
                    level[row][col] = extra
                    placed = true
                }
            }
        }

        // player is always placed at the bottom left corner
        fun addPlayer(level: Array<CharArray>) {
            level[level.size - 1][0] = 'P'
        }

        // exit is always at the top right corner
        fun addExit(level: Array<CharArray>) {
            level[0][level[0].size - 1] = 'E'
        }

        // fills the entire map with empty space. Spaces will later be replaced by other objs.
        fun addFloor(level: Array<CharArray>) {
            for (row in level.indices) {
                for (col in 0 until level[row].size) {
                    level[row][col] = ' '
                }
            }
        }

        // this is where the magic happens
        // 1. Randomly places barriers in the maps
        // 2. Does a process call cellular automata to generate unique terrain from
        //    the random placement. Its basically a few rounds of conways game of life!
        fun generateTerrain(level: Array<CharArray>) {
            randomlyPlaceWalls(level)
            val generatedLevel = doCellularAutomata(level)
            // copy generateLevel into our actual level
            for (row in 1 until level.size - 1) {
                for (col in 1 until level[row].size - 1) {
                    level[row][col] = generatedLevel[row][col]
                }
            }
        }

        fun randomlyPlaceWalls(level: Array<CharArray>) {
            val rng = Random()
            for (row in 1 until level.size - 1) {
                for (col in 1 until level[row].size - 1) {
                    val random = rng.nextDouble()
                    if (random > .6) {
                        level[row][col] = '*'
                    }
                }
            }
        }

        fun doCellularAutomata(level: Array<CharArray>): Array<CharArray> {
            var tempLevel = copyLevel(level)
            for (i in 0..1) {
                val newTempLevel = Array(tempLevel.size) {
                    CharArray(
                        tempLevel[0].size
                    )
                }
                for (row in 1 until level.size - 1) {
                    for (col in 1 until level[row].size - 1) {
                        val count = countNeighbors(tempLevel, row, col)
                        if (tempLevel[row][col] == '*' && count < 3) {
                            newTempLevel[row][col] = ' '
                        } else if (tempLevel[row][col] == ' ' && count > 3) {
                            newTempLevel[row][col] = '*'
                        } else {
                            newTempLevel[row][col] = tempLevel[row][col]
                        }
                    }
                }
                tempLevel = newTempLevel
            }
            //
            return tempLevel
        }

        fun countNeighbors(level: Array<CharArray>, row: Int, col: Int): Int {
            var count = 0
            if (level[row - 1][col - 1] == '*') {
                count++
            }
            if (level[row - 1][col] == '*') {
                count++
            }
            if (level[row - 1][col + 1] == '*') {
                count++
            }
            if (level[row][col - 1] == '*') {
                count++
            }
            if (level[row][col + 1] == '*') {
                count++
            }
            if (level[row + 1][col - 1] == '*') {
                count++
            }
            if (level[row + 1][col] == '*') {
                count++
            }
            if (level[row + 1][col + 1] == '*') {
                count++
            }
            return count
        }

        fun copyLevel(level: Array<CharArray>): Array<CharArray> {
            val levelCopy = Array(level.size) {
                CharArray(
                    level[0].size
                )
            }
            for (row in level.indices) {
                for (col in 0 until level[row].size) {
                    levelCopy[row][col] = level[row][col]
                }
            }
            return levelCopy
        }
    }
}
