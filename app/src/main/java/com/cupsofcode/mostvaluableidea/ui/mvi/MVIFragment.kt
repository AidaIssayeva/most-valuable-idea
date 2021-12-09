package com.cupsofcode.mostvaluableidea.ui.mvi

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
import com.cupsofcode.mostvaluableidea.R
import com.cupsofcode.mostvaluableidea.databinding.FragmentSimpleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class MVIFragment : Fragment() {

    private val mviViewModel: MVIViewModel by viewModels()

    private lateinit var binding: FragmentSimpleBinding

    private val intents: PublishSubject<MVIView.Intent> by lazy {
        PublishSubject.create()
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val alertDialog by lazy {
        MaterialAlertDialogBuilder(binding.root.context)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                intents.onNext(MVIView.Intent.DialogDismissed)
            }
            //TODO: comment out this code to show the SSOT
            .setOnDismissListener {
                intents.onNext(MVIView.Intent.DialogDismissed)
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
        mviViewModel.bindIntents(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ viewstate ->

                render(view.context, viewstate)
            }, {

            }).also {
                compositeDisposable.add(it)
            }
        setListeners()
    }

    private fun render(context: Context, viewstate: MVIView.ViewState) {
        binding.toolbar.title = context.getString(viewstate.navBarTitle)

        binding.loadingBar.isVisible = viewstate.isLoading
        switchError(viewstate.error)

        binding.button.text = context.getString(viewstate.buttonName)
        binding.button.isEnabled = viewstate.isButtonEnabled

        binding.itemName.text = viewstate.itemName
        binding.itemPrice.text = context.getString(R.string.text_price, viewstate.itemPrice)
        binding.totalPrice.text = context.getString(
            R.string.text_total_selected,
            viewstate.itemName,
            viewstate.totalPrice
        )
        setDropdownAdapter(viewstate.dropdownValues, viewstate.selectedValueIndex)

    }


    private fun setListeners() {
        binding.button.setOnClickListener {
            intents.onNext(MVIView.Intent.ButtonClicked)
        }
        binding.toolbar.setNavigationOnClickListener {
            intents.onNext(MVIView.Intent.OnBackClicked)
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                intents.onNext(MVIView.Intent.DropdownNumberSelected(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    private fun setDropdownAdapter(list: List<String>, selectedIndex: Int) {
        val adapter = ArrayAdapter(
            binding.spinner.context,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(selectedIndex)
    }

    private fun switchError(throwable: Throwable?) {
        if (throwable != null) {
            alertDialog.setMessage(throwable.message)
            alertDialog.show()
        } else {
            alertDialog.hide()
        }
    }

    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
    }
}