package com.cupsofcode.mostvaluableidea.domain

import io.reactivex.Observable
import java.util.concurrent.TimeUnit


object GetItemUseCase {

    fun execute(): Observable<Model> {
        return Observable.zip(
            Observable.just(
                Model()
            ),
            Observable.timer(2, TimeUnit.SECONDS)
        ) { actualDataStream, _ ->
            actualDataStream
        }
    }
}