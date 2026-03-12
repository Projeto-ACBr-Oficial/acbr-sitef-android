package com.mjtech.fiserv.clisitef.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mjtech.fiserv.clisitef.R
import com.mjtech.fiserv.clisitef.databinding.ViewItemMenuBinding

internal class ItemMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewItemMenuBinding

    init {
        orientation = VERTICAL

        binding = ViewItemMenuBinding.inflate(LayoutInflater.from(context), this)

        context.obtainStyledAttributes(attrs, R.styleable.ItemMenu).apply {
            val text = getString(R.styleable.ItemMenu_itemText)
            binding.tvLabel.text = text
            recycle()
        }
    }

    fun setText(text: String) {
        binding.tvLabel.text = text
    }
}