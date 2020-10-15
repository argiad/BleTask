package com.crtmg.bletime

import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter


class AppBindingComponent {
    companion object {
        @BindingAdapter("android:typeface")
        @JvmStatic
        fun setTypeface(v: TextView, style: String?) {
            when (style) {
                "bold" -> v.setTypeface(null, Typeface.BOLD)
                else -> v.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}