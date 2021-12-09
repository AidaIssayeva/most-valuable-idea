package com.cupsofcode.mostvaluableidea

import com.cupsofcode.mostvaluableidea.domain.GetItemUseCase
import com.cupsofcode.mostvaluableidea.domain.Model
import com.cupsofcode.mostvaluableidea.domain.ThrowErrorUseCase
import com.cupsofcode.mostvaluableidea.ui.mvi.MVIView
import com.cupsofcode.mostvaluableidea.ui.mvi.MVIViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class MVIViewModelTest: BaseTest() {

    private lateinit var viewmodel: MVIViewModel

    private lateinit var intentSubject: PublishSubject<MVIView.Intent>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        viewmodel = MVIViewModel()
        intentSubject = PublishSubject.create()
    }

    @Test
    fun `should return an initial viewState when viewmodel is binded`() {
        //given
        val expectedState = MVIView.ViewState()

        //when
        val resultsObserver = viewmodel.bindIntents(intentSubject).test()

        //then
        resultsObserver.assertValue(expectedState)
    }

    @Test
    fun `should return a viewState with data when viewmodel is binded`() {
        //given
        val viewState = MVIView.ViewState()
        val expectedData = Model()
        val expectedStates = arrayOf(
            viewState,
            viewState.copy(
                itemPrice = expectedData.price.toInt(),
                itemName = expectedData.name,
                dropdownValues = listOfNumbers(expectedData.maxCount),
                isLoading = false
            )
        )

        //when
        val resultsObserver = viewmodel.bindIntents(intentSubject).test()
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)

        //then
        resultsObserver.assertValues(*expectedStates)
    }

    @Test
    fun `should return a viewState with error when button is clicked`() {
        //given
        val viewState = MVIView.ViewState()
        val expectedError = Throwable("Something Bad Happened")
        val expectedStates = arrayOf(
            viewState,
            viewState.copy(
                error = expectedError,
                isLoading = false
            )
        )

        //when
        val resultsObserver = viewmodel.bindIntents(intentSubject).test()
        intentSubject.onNext(MVIView.Intent.ButtonClicked)

        //then
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedStates)
    }



    private fun listOfNumbers(maxNumber: Int): List<String> {
        val list = arrayListOf<String>()
        list.add("Count")
        for (number in 1..maxNumber) {
            list.add(number.toString())
        }
        return list
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }
}