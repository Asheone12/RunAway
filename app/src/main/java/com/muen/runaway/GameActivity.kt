package com.muen.runaway

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.muen.runaway.GameView


class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameView = GameView(this)
        setContentView(gameView)
    }
}
