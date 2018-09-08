package com.anwesh.uiprojects.linkedlinetosquarealtview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.linetosquareview.LineToSquareLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LineToSquareLineView = LineToSquareLineView.create(this)
        fullScreen()
        view.addOnMovementListener ({i ->
            createToast("movement with index ${i} completed")
        }, {i ->
            createToast("movement with index ${i} is reset")
        })
    }

    fun createToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}


