package com.cupsofcode.mostvaluableidea

import androidx.lifecycle.Observer
import com.cupsofcode.mostvaluableidea.domain.GetItemUseCase
import com.cupsofcode.mostvaluableidea.domain.Model
import com.cupsofcode.mostvaluableidea.domain.ThrowErrorUseCase
import com.cupsofcode.mostvaluableidea.ui.UIModel
import com.cupsofcode.mostvaluableidea.ui.mvvm.MVVMViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Captor
import java.util.concurrent.TimeUnit
import kotlin.math.exp


class MVVMViewModelTest : BaseTest() {

    private lateinit var viewModel: MVVMViewModel

    private val isButtonEnabledObserver: Observer<Boolean> by lazy {
        mockk { every { onChanged(any()) } just Runs }
    }

    private val itemObserver: Observer<UIModel> by lazy {
        mockk { every { onChanged(any()) } just Runs }
    }

    private val showLoadingObserver: Observer<Boolean> by lazy {

        mockk { every { onChanged(any()) } just Runs }
    }

    private val errorObserver: Observer<String> by lazy {
        mockk { every { onChanged(any()) } just Runs }
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = MVVMViewModel()
        viewModel.item.observeForever(itemObserver)
        viewModel.showLoading.observeForever(showLoadingObserver)
        viewModel.buttonEnabled.observeForever(isButtonEnabledObserver)
        viewModel.error.observeForever(errorObserver)

    }

    @Test
    fun `should return item when view is created`() {
        //given
        mockkObject(GetItemUseCase)
        mockkObject(ThrowErrorUseCase)
        val data = Model()
        val expectedItem = UIModel(
            itemPrice = data.price.toInt(),
            itemName = data.name,
            maxNumberInDropdown = data.maxCount
        )

        val itemSlot = slot<UIModel>()
        val loadingSlot = slot<Boolean>()

        //when
        viewModel.onViewCreated()
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)

        //then
        verify { GetItemUseCase.execute() }
        verify { ThrowErrorUseCase.execute() wasNot called }

        verify { itemObserver.onChanged(capture(itemSlot)) }
        verify { showLoadingObserver.onChanged(capture(loadingSlot)) }

        assertEquals(expectedItem.itemName, itemSlot.captured.itemName)
        assertEquals(expectedItem.itemPrice, itemSlot.captured.itemPrice)

    }

    @After
    fun tearDown() {
        viewModel.item.removeObserver(itemObserver)
        viewModel.showLoading.removeObserver(showLoadingObserver)
        viewModel.buttonEnabled.removeObserver(isButtonEnabledObserver)
    }
}