package com.example.nasaphotooftheday.utils

import android.content.Context
import android.util.AttributeSet
import com.example.nasaphotooftheday.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabHelper@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FloatingActionButton(context,attrs,defStyleAttr) {
    fun setState(state : String){
        when (state) {
            "Image_min" -> this.setImageDrawable(context.getDrawable(R.drawable.ic_maximize))
            "Image_max" -> this.setImageDrawable(context.getDrawable(R.drawable.ic_x))
            "Video_min" -> this.setImageDrawable(context.getDrawable(R.drawable.ic_play))
            "Video_max" -> this.setImageDrawable(context.getDrawable(R.drawable.ic_pause))
        }

    }
}