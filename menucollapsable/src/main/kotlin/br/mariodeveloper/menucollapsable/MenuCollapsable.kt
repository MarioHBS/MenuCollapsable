package br.mariodeveloper.menucollapsable

import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

/**
 * Created on 08/01/2022
 */

@Composable
fun MenuCollapsable(
	controller: NavHostController,
	timeOffset: Int = 800,
	timeSize: Int = 800,
	hasSettings: Boolean = false,
	settingRoute: String? = null,
	menusOptions: List<MenuModal>,
	composables: NavGraphBuilder.() -> Unit
) {
	val menuVM: MenuViewModel = viewModel()

	val qtdMenus = menusOptions.size
	val screenOrientation = LocalConfiguration.current.orientation
	when (screenOrientation) {
		Configuration.ORIENTATION_PORTRAIT -> {
			BoxWithConstraints(Modifier.fillMaxSize()) { //todo adicionar opção para quando tela for horizontal
				Column(Modifier.fillMaxSize()) {
					// space reserved for collapsed option of the menu
					Spacer(Modifier.height(this@BoxWithConstraints.maxHeight / (3 * qtdMenus)))
					NavHost(navController = controller, startDestination = "menu") {
						composables()
					}
				}
				// camada superior; o menu cobre a área em que as telas serão carregadas
				MenuSurface(menuVM, menus = menusOptions, goes = controller, timeOffset, timeSize, hasSettings, settingRoute)
			}
		}
		Configuration.ORIENTATION_LANDSCAPE -> {
			BoxWithConstraints(Modifier.fillMaxSize()) {
				Row (Modifier.fillMaxSize()) {
					// space reserved for collapsed option of the menu
					Spacer(Modifier.width(this@BoxWithConstraints.maxWidth / (3 * qtdMenus)))
					NavHost(navController = controller, startDestination = "menu") {
						composables()
					}
				}

				// camada superior; o menu cobre a área em que as telas serão carregadas
				LandscapeMenuSurface(menuVM, menus = menusOptions, goes = controller, timeOffset, timeSize, hasSettings, settingRoute)
			}
		}
		else -> {}
	}
}