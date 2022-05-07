package br.mariodeveloper.menucollapsable.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
internal fun VerticalText(text: String = "", modifier: Modifier = Modifier, color: Color = Color.Unspecified, fontWeight: FontWeight? = null) {
	val letters = listOf(*text.map { it.toString() }.toTypedArray())

	Column(modifier, Arrangement.SpaceEvenly, Alignment.CenterHorizontally) {
		letters.forEach {
			Text(it, color = color, fontSize = 15.sp, fontWeight = fontWeight)
		}
	}
}