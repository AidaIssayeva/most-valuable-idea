package com.cupsofcode.mostvaluableidea.ui.mvi

import androidx.annotation.StringRes
import com.cupsofcode.mostvaluableidea.R


interface MVIView {

    sealed class Intent {

        // data
        data class Data(
            val maxNumberInDropdown: Int,
            val itemName: String,
            val itemPrice: Int
        ) : Intent()

        data class Error(val throwable: Throwable) : Intent()

        object Loading : Intent()

        object NoOp: Intent()

        // user actions
        data class DropdownNumberSelected(val number: Int) : Intent()
        object ButtonClicked : Intent()
        object OnBackClicked : Intent()
        object DialogDismissed: Intent()
    }

    data class ViewState(
        @StringRes val navBarTitle: Int =  R.string.navbar_title_cart,
        @StringRes val buttonName: Int =  R.string.button_continue,
        val isButtonEnabled: Boolean = false,
        val itemName: String = "",
        val itemPrice: Int? = null,
        val dropdownValues: List<String> = emptyList(),
        val selectedValueIndex: Int = 0,
        val totalPrice: Int = 0,
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )
}