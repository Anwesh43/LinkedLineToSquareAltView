package com.anwesh.uiprojects.linetosquareview

/**
 * Created by anweshmishra on 08/09/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5

fun Canvas.drawSLANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(scale - 0.5f, 0f)) * 2
    val origSize = gap/20
    val dSize : Float = origSize + (gap/2 - origSize) * sc1
    val x : Float = (w/2 - gap/2) * sc2 * (1f - 2 * (i % 2))
    paint.color = Color.parseColor("#1565C0")
    save()
    translate(w/2, gap * i + gap /2 + gap / 2)
//    if (scale == 0f) {
//        drawLine(0f, -gap/2, 0f, gap/2, paint)
//    }
    save()
    translate(x, 0f)
    drawRect(RectF(-dSize, -gap/2, dSize, gap/2), paint)
    restore()
    restore()
}

class LineToSquareLineView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * 0.1f
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SLANode(var i : Int, val state : State = State()) {
        private var next : SLANode? = null
        private var prev : SLANode? = null

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SLANode(i + 1)
                next?.prev = this
            }
        }

        init {
            addNeighbor()
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSLANode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SLANode {
            var curr : SLANode? = this.prev
            if (dir == 1) {
                curr = this.next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineToAltSquare(var i : Int) {
        private var root : SLANode = SLANode(0)
        private var curr : SLANode = root
        private var dir : Int = 1
        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : LineToSquareLineView) {
        private val ltas : LineToAltSquare = LineToAltSquare(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            ltas.draw(canvas, paint)
            animator.animate {
                ltas.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ltas.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : LineToSquareLineView {
            val view : LineToSquareLineView = LineToSquareLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}