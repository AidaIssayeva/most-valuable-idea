package com.cupsofcode.mostvaluableidea.ui.mvvm2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MVVM2ViewModel : ViewModel() {

  private val _text = MutableLiveData<String>().apply {
    value = "This is MVVM 2 Fragment"
  }
  val text: LiveData<String> = _text
}