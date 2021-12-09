package com.cupsofcode.mostvaluableidea.ui.mvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cupsofcode.mostvaluableidea.R
import com.cupsofcode.mostvaluableidea.databinding.FragmentSimpleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MVVMFragment : Fragment() {

    private val mvvmViewModel: MVVMViewModel by viewModels()

    private lateinit var binding: FragmentSimpleBinding

    private val alertDialog by lazy {
        MaterialAlertDialogBuilder(binding.root.context)
            .setPositiveButton(R.string.button_ok) { _, _ ->

            }
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimpleBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mvvmViewModel.onViewCreated()
        setListeners()
        setObservers(view.context)
    }

    private fun setListeners() {
        binding.button.setOnClickListener {
            mvvmViewModel.buttonClicked()
        }
        binding.toolbar.setNavigationOnClickListener {
            mvvmViewModel.backClicked()
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mvvmViewModel.positionSelected(position)
                binding.button.isVisible = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    private fun setObservers(context: Context) {
        mvvmViewModel.buttonName.observe(viewLifecycleOwner, Observer { stringRes ->
            binding.button.text = context.getString(stringRes)
        })
        mvvmViewModel.buttonEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            binding.button.isEnabled = enabled
        })
        mvvmViewModel.showLoading.observe(viewLifecycleOwner, Observer { showLoading ->
            binding.loadingBar.isVisible = showLoading
        })
        mvvmViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            alertDialog.setMessage(errorMessage)
            alertDialog.show()
        })
        mvvmViewModel.item.observe(viewLifecycleOwner, Observer { item ->
            binding.itemName.text = item.itemName
            binding.itemPrice.text = context.getString(R.string.text_price, item.itemPrice)
            setDropdownAdapter(item.maxNumberInDropdown)
        })

        mvvmViewModel.totalPrice.observe(viewLifecycleOwner, Observer { price ->
            binding.totalPrice.text = context.getString(
                R.string.text_total_selected,
                mvvmViewModel.item.value?.itemName ?: "",
                price
            )
        })


    }

    private fun setDropdownAdapter(maxNumber: Int) {
        val adapter = ArrayAdapter(
            binding.spinner.context,
            android.R.layout.simple_spinner_dropdown_item,
            listOfNumbers(maxNumber)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
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