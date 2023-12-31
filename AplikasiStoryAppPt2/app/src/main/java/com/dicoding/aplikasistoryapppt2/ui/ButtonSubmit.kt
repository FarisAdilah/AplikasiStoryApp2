package com.dicoding.aplikasistoryapppt2.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.aplikasistoryapppt2.R

class ButtonSubmit : AppCompatButton {
    private lateinit var colorBackground: Drawable
    private var txtColor: Int = 0


    constructor(context : Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs : AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        colorBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = colorBackground

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
    }


}