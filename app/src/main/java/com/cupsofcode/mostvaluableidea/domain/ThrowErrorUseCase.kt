package com.cupsofcode.mostvaluableidea.domain

import io.reactivex.Observable
import java.util.concurrent.TimeUnit


object ThrowErrorUseCase {

    fun execute(): Observable<Throwable> {
        return Observable.zip(
            Observable.error<Throwable>(Throwable("Something Bad Happened")),
            Observable.timer(5, TimeUnit.SECONDS)
        ) { actualDataStream, _ ->
            actualDataStream
        }
    }
}