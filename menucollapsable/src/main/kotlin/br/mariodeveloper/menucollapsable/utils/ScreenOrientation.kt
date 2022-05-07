package br.mariodeveloper.menucollapsable.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun getScreenOrientation(): Int? {
	val context = LocalContext.current
	val activity = context.findActivity()
	return activity?.requestedOrientation
}

fun Context.findActivity(): Activity? = when (this) {
	is Activity -> this
	is ContextWrapper -> baseContext.findActivity()
	else -> null
}