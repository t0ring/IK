package com.andy.ikt

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.*

sealed class Maybe<out T> {
    object `Nothing#` : Maybe<Nothing>() {
        override fun toString(): String = "Nothing#"
    }

    data class Just<out T>(val value: T) : Maybe<T>()
}

fun <T, R> Maybe<T>.fmap(transform: (T) -> R): Maybe<R> = when (this) {
    Maybe.`Nothing#` -> Maybe.`Nothing#`
    is Maybe.Just -> Maybe.Just(transform(this.value))
}

infix fun <T, R> ((T) -> R).`($)`(maybe: Maybe<T>) = maybe.fmap(this)

fun <T, R> Iterable<T>.fmap(transform: (T) -> R): List<R> = this.map(transform)

fun <T, U, R> ((T) -> U).fmap(transform: (U) -> R) = { t: T -> transform(this(t)) }

infix fun <T, R> Maybe<(T) -> R>.`(*)`(maybe: Maybe<T>): Maybe<R> = when (this) {
    Maybe.`Nothing#` -> Maybe.`Nothing#`
    is Maybe.Just -> this.value `($)` maybe
}

infix fun <T, R> Iterable<(T) -> R>.`(*)`(iterable: Iterable<T>) = this.flatMap { iterable.map(it) }

fun <T> ((x: T, y: T) -> T).liftA2(m1: Maybe<T>, m2: Maybe<T>) = { y: T -> { x: T -> this(x, y) } } `($)` m1 `(*)` m2

fun half(x: Int) = if (x % 2 == 0)
    Maybe.Just(x / 2)
else
    Maybe.`Nothing#`

infix fun <T, R> Maybe<T>.`))=`(f: ((T) -> Maybe<R>)): Maybe<R> = when (this) {
    Maybe.`Nothing#` -> Maybe.`Nothing#`
    is Maybe.Just -> f(this.value)
}

data class IO<out T>(val `(-`: T)

infix fun <T, R> IO<T>.`))=`(f: ((T) -> IO<R>)): IO<R> = f(this.`(-`)

fun getLine(): IO<String> = IO(readLine() ?: "")

typealias FilePath = String

fun readFile(filename: FilePath): IO<String> = IO(File(filename).readText())

fun putStrLn(str: String): IO<Unit> = IO(println(str))

fun <T> `do`(ioOperations: () -> IO<T>) = ioOperations()

val foo = `do` {
    val filename = getLine().`(-`
    val contents = readFile(filename).`(-`
    putStrLn(contents)
}

fun main(args: Array<String>) {

    Maybe.Just(2).fmap { it + 3 }.apply {
        System.out.println(this)
    }

    val f1 = { x: Int -> x + 2 }.fmap { x: Int -> x + 3 }
    println(f1)

    val f2 = Maybe.Just { x: Int -> x + 3 } `(*)` Maybe.Just(2)
    println(f2)

    val f3 = listOf<(Int) -> Int>({ it * 2 }, { it + 3 }) `(*)` listOf(1, 2, 3)
    println(f3)

    val f4 = { y: Int -> { x: Int -> x + y } } `($)` Maybe.Just(5)
    println(f4)

    val f5 = Maybe.Just { x: Int -> x + 5 } `(*)` Maybe.Just(3)
    println(f5)

    val f6 = { y: Int -> { x: Int -> x + y } } `($)` Maybe.Just(5) `(*)` Maybe.Just(3)

    val f7 = { x: Int, y: Int -> x * y }.liftA2(Maybe.Just(5), Maybe.Just(3))

    val f8 = Maybe.Just(20) `))=` ::half `))=` ::half `))=` ::half

    getLine() `))=` ::readFile `))=` ::putStrLn
}

fun t1() = runBlocking<Unit> {
    val job = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    job.join() // waits for job's completion
    println("main: Now I can quit.")
}

fun t2() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val job = launch {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun t3() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val job = launch {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // cancellable computation loop
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun t4() = runBlocking<Unit> {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("I'm running finally")
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun t5() = runBlocking<Unit> {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("I'm running finally")
                delay(1000L)
                println("And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun t6() = runBlocking<Unit> {
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // will get cancelled before it produces this result
    }
    println("Result is $result")
}