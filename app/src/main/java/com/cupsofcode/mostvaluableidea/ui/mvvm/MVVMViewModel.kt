package com.cupsofcode.mostvaluableidea.ui.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cupsofcode.mostvaluableidea.R
import com.cupsofcode.mostvaluableidea.domain.GetItemUseCase
import com.cupsofcode.mostvaluableidea.domain.ThrowErrorUseCase
import com.cupsofcode.mostvaluableidea.ui.UIModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MVVMViewModel : ViewModel() {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val _buttonName = MutableLiveData<Int>().apply {
        value = R.string.button_continue
    }
    val buttonName: LiveData<Int> = _buttonName

    private val _buttonEnabled = MutableLiveData<Boolean>(false)

    val buttonEnabled: LiveData<Boolean> = _buttonEnabled

    private val _showLoading = MutableLiveData<Boolean>(true)

    val showLoading: LiveData<Boolean> = _showLoading

    private val _item = MutableLiveData<UIModel>()

    val item: LiveData<UIModel> = _item

    private val _error = MutableLiveData<String>()

    val error: LiveData<String> = _error

    private val _totalPrice = MutableLiveData<Int>()

    val totalPrice: LiveData<Int> = _totalPrice

    fun onViewCreated() {
        GetItemUseCase.execute()
            .map {
                UIModel(
                    itemName = it.name,
                    maxNumberInDropdown = it.maxCount,
                    itemPrice = it.price.toInt()
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _showLoading.value = true }
            .doFinally { _showLoading.value = false }
            .subscribe({ item ->
                _item.value = item
            }, { throwable ->
                _error.value = throwable.message
            })
            .also {
                compositeDisposable.add(it)
            }
    }

    fun buttonClicked() {
        ThrowErrorUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _showLoading.value = true }
            .doFinally { _showLoading.value = false }
            .subscribe({}, { throwable ->
                _error.value = throwable.message
            }).also {
                compositeDisposable.add(it)
            }
    }

    fun positionSelected(position: Int) {
        val totalPrice = position * (_item.value?.itemPrice ?: 0)
        _totalPrice.value = totalPrice
        _buttonEnabled.value = position > 0
    }

    fun backClicked() {
        Completable.complete()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, { throwable ->
                _error.value = throwable.message
            })
            .also {
                compositeDisposable.add(it)
            }

    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}