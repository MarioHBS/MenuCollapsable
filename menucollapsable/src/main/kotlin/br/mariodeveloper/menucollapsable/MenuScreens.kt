/**
 * Contém as telas e componentes responsáveis por mostrar o menu
 *
 * @author Mário Henrique
 * Created on 08/01/2022
 * Lst mod on 06/05/2022
 */
package br.mariodeveloper.menucollapsable

import android.os.Build

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import br.mariodeveloper.menucollapsable.utils.VerticalText

data class MenuModal(val color: Color, @DrawableRes val img: Int, @StringRes val title: Int, val destination: String)

/**
 * This is the top level surface of the Menu.
 * The options covers the view where the screens will be showed
 *
 * @author Mário Henrique
 */
@Composable
fun MenuSurface(
	viewModel: MenuViewModel,
	menus: List<MenuModal>,
	goes: NavHostController,
	timeOffset: Int,
	timeSize: Int,
	hasSettings: Boolean,
	settingRoute: String?
) {
	if (hasSettings && (settingRoute == null))
		throw IllegalArgumentException("Setting route cannot be null if hasSetting is True")
	val qtd = menus.size
	BoxWithConstraints(Modifier.wrapContentSize()) {
		val height = maxHeight / qtd
		val selectIdx by viewModel.selectionFlow.collectAsState()

		val backPress: () -> Unit = {
			viewModel.updateSelected(-1)
			goes.navigateUp()
		}

		val transition = updateTransition(targetState = (selectIdx != -1), label = "transition")
		val szImage by transition.animateDp({ tween(timeSize) }, label = "image size") {
			if (it) 0.dp else (height / 3)
		}
		val turnImage by transition.animateFloat( { tween(timeSize) }, label = "rotating") {
			if (it) 0f else -180f
		}
		val hgtTarget by transition.animateDp({ tween(timeSize) }, label = "shrink") {
			if (it) (height / 3) else (height + 10.dp)
		}
		val hgtOther by transition.animateDp({ tween(timeSize) }, label = "vanish") { if (it) 0.dp else (height + 10.dp) }
		val hgtFirst by transition.animateDp({ tween(timeSize) }, label = "vanish") { if (it) 0.dp else height }

		val offsets = arrayListOf<State<Float>>(remember { mutableStateOf(0f) })
		for (i in 1 until qtd)
			offsets.add(transition.animateFloat({ tween(timeOffset) }, label = "reposition $i") {
				if (it) 0f else (height.value * i)
			})
		offsets.reverse() // a construção da lista fica melhor dessa forma

		BackHandler((selectIdx != -1)) { backPress() }

		MenuOption(modifier = Modifier.align(Alignment.TopCenter),
			current = 0, index = selectIdx,
			offset = offsets[0].value,
			targetHgt =  hgtTarget, otherHgt = hgtFirst, imageSz = szImage,
			color = menus[0].color, idStr = menus[0].title, idImg = menus[0].img,
			turnImage,
			backPress = backPress,
			updateIdx = viewModel::updateSelected,
			selectedAction = { goes.navigate(menus[0].destination) }
		)
		for (idx in 1 until menus.size) {
			MenuOption(modifier = Modifier.align(Alignment.TopCenter),
				current = idx, index = selectIdx,
				offset = offsets[idx].value,
				targetHgt =  hgtTarget, otherHgt = hgtOther, imageSz = szImage,
				color = menus[idx].color, idStr = menus[idx].title, idImg = menus[idx].img,
				turnImage,
				backPress = backPress,
				updateIdx = viewModel::updateSelected,
				selectedAction = { goes.navigate(menus[idx].destination) }
			)
		}

		if (hasSettings)
			Icon(painterResource(R.drawable.ic_settings_36), contentDescription = "",
				Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp).clickable {
					viewModel.updateSelected(9)
					goes.navigate(settingRoute!!)
				})
	}
}

@Composable
fun LandscapeMenuSurface(
	viewModel: MenuViewModel,
	menus: List<MenuModal>,
	goes: NavHostController,
	timeOffset: Int,
	timeSize: Int,
	hasSettings: Boolean,
	settingRoute: String?
) {
	if (hasSettings && (settingRoute == null))
		throw IllegalArgumentException("Setting route cannot be null if hasSetting is True")

	val qtd = menus.size
	BoxWithConstraints(Modifier.wrapContentSize()) {
		val width = maxWidth / qtd
		val selectIdx by viewModel.selectionFlow.collectAsState()

		val backPress: () -> Unit = {
			viewModel.updateSelected(-1)
			goes.navigateUp()
		}

		val transition = updateTransition(targetState = (selectIdx != -1), label = "transition")
		val szImage by transition.animateDp({ tween(timeSize) }, label = "image size") {
			if (it) 0.dp else (width / 3)
		}
		val turnArrow by transition.animateFloat( { tween(timeSize) }, label = "rotating") {
			if (it) -90f else 90f //right and left
		}
		val wdtTarget by transition.animateDp({ tween(timeSize) }, label = "shrink") {
			if (it) (width / 3) else (width + 10.dp)
		}
		val wdtOther by transition.animateDp({ tween(timeSize) }, label = "vanish") { if (it) 0.dp else (width + 10.dp) }
		val wdtFirst by transition.animateDp({ tween(timeSize) }, label = "vanish") { if (it) 0.dp else width }

		val offsets = arrayListOf<State<Float>>(remember { mutableStateOf(0f) })
		for (i in 1 until qtd)
			offsets.add(transition.animateFloat({ tween(timeOffset) }, label = "reposition $i") {
				if (it) 0f else (width.value * i)
			})
		offsets.reverse()

		BackHandler((selectIdx != -1)) { backPress() }

		LandscapeMenuOption(modifier = Modifier.align(Alignment.CenterStart),
			current = 0, index = selectIdx,
			offset = offsets[0].value,
			targetWdt = wdtTarget, otherWdt = wdtFirst, imageSz = szImage,
			color = menus[0].color, idStr = menus[0].title, idImg = menus[0].img,
			rotate = turnArrow,
			backPress = backPress,
			updateIdx = viewModel::updateSelected,
			selectedAction = { goes.navigate(menus[0].destination) }
		)
		for (idx in 1 until menus.size) {
			LandscapeMenuOption(modifier = Modifier.align(Alignment.CenterStart),
				current = idx, index = selectIdx,
				offset = offsets[idx].value,
				targetWdt = wdtTarget, otherWdt = wdtOther, imageSz = szImage,
				color = menus[idx].color, idStr = menus[idx].title, idImg = menus[idx].img,
				rotate = turnArrow,
				backPress = backPress,
				updateIdx = viewModel::updateSelected,
				selectedAction = { goes.navigate(menus[idx].destination) }
			)
		}
	}
}

@Composable
private fun MenuOption(
	modifier: Modifier, current: Int, index: Int, offset: Float = 0f,
	targetHgt: Dp, otherHgt: Dp, imageSz: Dp,
	color: Color,
	@StringRes idStr: Int, @DrawableRes idImg: Int,
	rotate: Float = 0F,
	backPress: () -> Unit, updateIdx: (Int) -> Unit,
	selectedAction: () -> Unit
) {
	Card(modifier
		.offset(y = offset.dp)
		.fillMaxWidth()
		.height(if (index == current && current != 9) targetHgt else otherHgt)
		.clickable {
			if (index == current) backPress()
			else {
				updateIdx(current)
				selectedAction()
			}
		}
		.drawColoredShadow(Color.Black, .7f, offsetY = 10.dp),
		elevation = 5.dp
	) {
		Box {
			Column(Modifier.fillMaxSize()
					.background(color), Arrangement.Center, Alignment.CenterHorizontally
			) {
				Icon(painterResource(id = idImg), "", Modifier.size(imageSz), Color.White)
				Spacer(Modifier.height(5.dp))
				Text(stringResource(idStr).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(Modifier.height(5.dp))
			}
			Icon(painterResource(R.drawable.ic_arrow_down), "",
				Modifier.align(Alignment.BottomCenter).rotate(rotate) , Color.White)
		}
	}
}

@Composable
private fun LandscapeMenuOption(
	modifier: Modifier, current: Int, index: Int, offset: Float = 0f,
	targetWdt: Dp, otherWdt: Dp, imageSz: Dp, color: Color,
	@StringRes idStr: Int, @DrawableRes idImg: Int,
	rotate: Float = 0f,
	backPress: () -> Unit, updateIdx: (Int) -> Unit,
	selectedAction: () -> Unit
) {
	Card(modifier
		.offset(x = offset.dp)
		.fillMaxHeight()
		.width(if (index == current && current != 9) targetWdt else otherWdt)
		.clickable {
			if (index == current) backPress()
			else {
				updateIdx(current)
				selectedAction.invoke()
			}
		}
		.drawColoredShadow(Color.Black, .7f, offsetX = 10.dp),
		elevation = 5.dp
	) {
		Box { //todo testar linha dupla
			Row(Modifier.fillMaxSize().background(color), Arrangement.Center, Alignment.CenterVertically) {
				Icon(painterResource(id = idImg), "", Modifier.size(imageSz), Color.White)
				Spacer(Modifier.width(5.dp)) //todo ajustar distâncias dos elementos
				VerticalText(stringResource(id = idStr).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(Modifier.width(5.dp))
			}
			Icon(painterResource(R.drawable.ic_arrow_down), "the arrow",
			Modifier.align(Alignment.CenterEnd).rotate(rotate), Color.White)
		}
	}
}

private fun Modifier.drawColoredShadow(
	color: Color,
	alpha: Float = 0.2f,
	borderRadius: Dp = 0.dp,
	shadowRadius: Dp = 20.dp,
	offsetY: Dp = 0.dp,
	offsetX: Dp = 0.dp
) = this.drawBehind {
	val transparentColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		android.graphics.Color.toArgb(color.copy(alpha = 0.0f).value.toLong())
	} else
		color.toArgb()

	val shadowColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		android.graphics.Color.toArgb(color.copy(alpha = alpha).value.toLong())
	} else
		color.copy(alpha).toArgb()

	this.drawIntoCanvas {
		val paint = Paint()
		val frameworkPaint = paint.asFrameworkPaint()
		frameworkPaint.color = transparentColor
		frameworkPaint.setShadowLayer(
			shadowRadius.toPx(),
			offsetX.toPx(),
			offsetY.toPx(),
			shadowColor
		)
		it.drawRoundRect(
			0f,
			0f,
			this.size.width,
			this.size.height,
			borderRadius.toPx(),
			borderRadius.toPx(),
			paint
		)
	}
}

@Preview(name = "O Menu vertical")
@Composable
private fun PreviewMenu() {
	//MenuSurface(menus = , goes = , timeOffset = , timeSize = , hasSettings = , settingRoute = )
}

@Preview(name = "Opção do Menu")
@Composable
private fun PreviewOption() {
	val availableHgt = 100.dp
	var rotate = -180f
	Card(Modifier.fillMaxWidth().height(availableHgt + 10.dp).clickable { rotate = 0f }, elevation = 5.dp) {
		Box {
			Column(Modifier.fillMaxSize().background(Color.Blue), Arrangement.Center, Alignment.CenterHorizontally) {
				Icon(painterResource(id = R.drawable.ic_launcher_background),
					"", Modifier.size(availableHgt / 3), Color.White
				)
				Spacer(Modifier.height(5.dp))
				Text("MESAS", color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(Modifier.height(5.dp))
			}
			Icon(painterResource(R.drawable.ic_arrow_down), "",
				Modifier.align(Alignment.BottomCenter).rotate(rotate) , Color.White)
		}
	}
}

@Preview(name = "Opção do Menu em modo vertical")
@Composable
private fun PreviewVerticalOption() {
	val availableWidth = 200.dp
	var rotate = -90f
	Card(Modifier.fillMaxHeight().width(availableWidth + 10.dp).clickable { rotate = 90f }, elevation = 5.dp) {
		Box {
			Row(Modifier.fillMaxSize().background(Color.Green), Arrangement.Center, Alignment.CenterVertically) {
				Icon(painterResource(id = R.drawable.ic_launcher_background),
					"", Modifier.size(availableWidth / 3), Color.White)
				Spacer(Modifier.width(5.dp))
				VerticalText("EXTRAS", color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(Modifier.width(5.dp))
			}
			Icon(painterResource(R.drawable.ic_arrow_down), "the arrow",
				Modifier.align(Alignment.CenterEnd).rotate(rotate), Color.White)
		}
	}
}