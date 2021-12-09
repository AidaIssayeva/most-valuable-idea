package com.cupsofcode.mostvaluableidea.ui.mvvm2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cupsofcode.mostvaluableidea.R

class MVVM2Fragment : Fragment() {

  private val mvvm2ViewModel: MVVM2ViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_complex, container, false)
    val textView: TextView = root.findViewById(R.id.item_price)
    mvvm2ViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
  }
}