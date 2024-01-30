package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class AsyncFunction {

    /**
     * Async Function
     * ● Untuk membuat coroutine, kita tidak hanya bisa menggunakan function launch, ada function async
     *    yang bisa kita gunakan juga untuk membuat coroutine
     * ● Berbeda dengan launch function yang mengembalikan Job, async function mengembalikan Deferred
     * ● Deferred adalah turunan dari Job, yang membedakan adalah, Deferred membawa value hasil dari
     *    async function
     * ● Deferred itu mirip konsep Promise atau Future, dimana datanya akan ada nanti
     * ● Jika kita ingin menunggu data di Deferred sampai ada, kita bisa menggunakan method await()
     */

    suspend fun getFoo(): Int {

        delay(1_000)
        return 10
    }

    suspend fun getBar(): Int {

        delay(1_000)
        return 10
    }

    @Test
    fun testSuspendAsynchronouse() {

        runBlocking {
            val time = measureTimeMillis {
                val foo: Deferred<Int> = GlobalScope.async { getFoo() } // GlobalScope.async() // async() mengembalikan nilai dengan return Deferred<T>
                val bar = GlobalScope.async { getBar() }

                val total = foo.await() + bar.await() // await(): T // mengembalikan return value

                println("Total is: $total")
            }

            println("Time: $time")
            /**
             * result: method suspend berjalan secara asynchronouse dengan return value.. ini yang di harapkan.. ini seperti job Callable return Future<T> dan Promise di java thread
             * Total is: 20
             * Time: 1045
             */
        }
    }

    /**
     * awaitAll Function
     * ● Pada materi sebelumnya kita membuat beberapa async coroutine, lalu kita menggunakan await
     *    function untuk menunggu hasil nya
     * ● Pada job, tersedia joinAll untuk menunggu semua launch coroutine selesai
     * ● Kotlin juga menyediakan awaitAll untuk menunggu semua Deferred selesai mengembalikan value
     * ● awaitAll merupakan generic function, dan mengembalikan List<T> data hasil dari semua Deffered nya
     */

    @Test
    fun testAwaitAll() {

        runBlocking {
            val time = measureTimeMillis {
                val foo: Deferred<Int> = GlobalScope.async { getFoo() } // async() mengembalikan nilai dengan return Deferred<T>
                val bar = GlobalScope.async { getBar() }
                val total = awaitAll(foo, bar).sum() // <T> awaitAll(vararg deferreds: Deferred<T>): List<T> // multiple argument deferred

                println("Total value: $total")
            }

            println("time: $time")
            /**
             * result:
             * Total value: 20
             * time: 1057
             */
        }
    }

}