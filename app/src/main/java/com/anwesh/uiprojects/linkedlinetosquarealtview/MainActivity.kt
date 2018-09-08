package com.anwesh.uiprojects.linkedlinetosquarealtview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linetosquareview.LineToSquareLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LineToSquareLineView.create(this)
    }
}



