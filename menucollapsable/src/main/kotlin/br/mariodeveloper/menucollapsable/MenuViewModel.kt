package br.mariodeveloper.menucollapsable

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manage state of the entire menu
 *
 * @author Mário Henrique
 * Created on 05/05/2022
 */
class MenuViewModel : ViewModel() {
//	val viewModelState = MutableStateFlow(MenuVMState())
	private val _selectedItem = MutableStateFlow(-1)

	val selectionFlow: StateFlow<Int>
		get() = _selectedItem

	fun updateSelected(selection: Int) {
//		viewModelState.update { it.copy(selectedItem = selection) }
		_selectedItem.value = selection
	}
}

/*data class MenuVMState(
	val selectedItem: Int = -1
)*/