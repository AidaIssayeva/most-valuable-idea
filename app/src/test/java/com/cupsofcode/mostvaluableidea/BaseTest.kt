package com.cupsofcode.mostvaluableidea

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.*

abstract class BaseTest {

    companion object {
        @BeforeClass @JvmStatic fun setupClass() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }

        @AfterClass @JvmStatic fun teardownClass() {
            RxAndroidPlugins.reset()
            RxJavaPlugins.reset()
        }
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()
    protected val testScheduler = TestScheduler()

    @Before
    open fun setUp() {
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
    }

}