package com.cupsofcode.mostvaluableidea.ui.mvi2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class MVI2Fragment : Fragment() {

  private val mvi2ViewModel: MVI2ViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }

}