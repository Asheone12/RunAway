package com.muen.runaway

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL

        val textView = AppCompatTextView(this)
        textView.text = "ROUGE LIKE"
        textView.gravity = Gravity.CENTER
        textView.textSize = 42f
        linearLayout.addView(textView)

        val play = MaterialButton(this)
        play.text = "Play"
        play.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this,
                    GameActivity::class.java
                )
            )
        }
        linearLayout.addView(play)

        setContentView(linearLayout)
    }
}