package com.example.mystory.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.mystory.R


class AuthEditText : AppCompatEditText {

	private lateinit var validBackground: Drawable
	private lateinit var invalidBackground: Drawable
	private var isError = false

	constructor(context: Context) : super(context) {
		init()
	}
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		background = if(isError) invalidBackground else validBackground
	}

	private fun init() {
		isError = false
		validBackground = ContextCompat.getDrawable(context, R.drawable.bg_auth_input) as Drawable
		invalidBackground = ContextCompat.getDrawable(context, R.drawable.bg_auth_input_invalid) as Drawable
		background = validBackground
		validation(inputType)
	}

	private fun validation(inputType: Int) {
		val (message, validator) = when (inputType - 1) {
			InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
				context.getString(R.string.invalidPassword) to { it: String -> it.length >= 8 }
			}
			InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
				context.getString(R.string.invalidEmail) to { it: String ->
					val regex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
					regex.matches(it)
				}
			}
			InputType.TYPE_TEXT_VARIATION_PERSON_NAME -> {
				context.getString(R.string.invalidName) to { it: String -> it.length >= 3 }
			}
			else -> return
		}

		addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(
				s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				isError = !validator(s.toString())
				error = if(isError) message else null
			}
			override fun afterTextChanged(s: Editable?) {}
		})
	}
}
