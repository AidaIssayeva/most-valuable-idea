package com.cupsofcode.mostvaluableidea.ui.mvi

import androidx.lifecycle.ViewModel
import com.cupsofcode.mostvaluableidea.domain.GetItemUseCase
import com.cupsofcode.mostvaluableidea.domain.ThrowErrorUseCase
import com.cupsofcode.mostvaluableidea.ui.mvi.MVIView.Intent
import com.cupsofcode.mostvaluableidea.ui.mvi.MVIView.Intent.*
import com.cupsofcode.mostvaluableidea.ui.mvi.MVIView.ViewState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class MVIViewModel : ViewModel() {

    private val initialState = Callable {
        ViewState()
    }

    fun bindIntents(viewIntents: Observable<Intent>): Observable<ViewState> {
        return bindIntent(viewIntents)
            .subscribeOn(Schedulers.io())
            .scanWith(initialState, reducer)
            .onErrorReturn { ViewState(error = it) }
            .distinctUntilChanged()
    }

    private fun bindIntent(viewIntents: Observable<Intent>): Observable<Intent> {

        val dataIntent = GetItemUseCase.execute()
            .map<Intent> {
                Data(
                    itemPrice = it.price.toInt(),
                    itemName = it.name,
                    maxNumberInDropdown = it.maxCount
                )
            }
            .onErrorReturn { Error(throwable = it) }
            .startWith(Loading)

        val transformedIntents = viewIntents.publish {
            val buttonClicked = it.ofType(ButtonClicked::class.java)
                .debounce(300, TimeUnit.MILLISECONDS)
                .switchMap {
                    ThrowErrorUseCase.execute()
                        .map<Intent> { NoOp }
                        .onErrorReturn { Error(throwable = it) }
                        .startWith(Loading) //WHERE is the loading
                }

            val onBackClicked = it.ofType(OnBackClicked::class.java)
                .debounce(300, TimeUnit.MILLISECONDS)
                .flatMapCompletable { _ ->
                    Completable.complete()
                }.toObservable<Intent>()

            Observable.merge(
                buttonClicked,
                onBackClicked
            )
        }
        return Observable.merge(transformedIntents, dataIntent, viewIntents)
    }

    private val reducer =
        BiFunction<ViewState, Intent, ViewState> { previousState, intent ->
            when (intent) {
                is Data -> previousState.copy(
                    isLoading = false,
                    itemName = intent.itemName,
                    itemPrice = intent.itemPrice,
                    dropdownValues = listOfNumbers(intent.maxNumberInDropdown)
                )
                is Error -> previousState.copy(
                    error = intent.throwable,
                    isLoading = false
                )
                is DropdownNumberSelected -> {
                    val selectedIndex = intent.number
                    val totalPrice = selectedIndex * (previousState.itemPrice ?: 0)
                    previousState.copy(
                        selectedValueIndex = intent.number,
                        totalPrice = totalPrice,
                        isButtonEnabled = selectedIndex > 0
                    )
                }

                Loading -> previousState.copy(isLoading = true)
                DialogDismissed -> previousState.copy(error = null)
                else -> previousState
            }
        }

    private fun listOfNumbers(maxNumber: Int): List<String> {
        val list = arrayListOf<String>()
        list.add("Count")
        for (number in 1..maxNumber) {
            list.add(number.toString())
        }
        return list
    }
}