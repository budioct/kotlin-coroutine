package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class CoroutineScopeFunctionTest {

    /**
     * coroutineScope Function
     * ● Kadang pembuatan coroutine scope itu terlalu kompleks jika hanya untuk kasus-kasus yang
     *    sederhana, misal saja kita hanya ingin menggabungkan beberapa suspend function, lalu mengembalikan nilai tersebut
     * ● Pada kasus yang sederhana, kita bisa menggunakan coroutineScope function untuk
     *    menggabungkan beberapa suspend function
     * ● Saat ada error di coroutine yang terdapat di dalam coroutine scope function tersebut, maka semua
     *    coroutine pun akan dibatalkan
     */

    suspend fun getFoo(): Int {
        delay(1000)
        println("Foo ${Thread.currentThread().name}")
        return 10
    }

    suspend fun getBar(): Int {
        delay(1000)
        println("Bar ${Thread.currentThread().name}")
        return 30
    }

    suspend fun getSum(): Int = coroutineScope {

        // method coroutine scope singkat
        // semua method suspend yang telah di panggil dengan method async() akan di bundle di coroutineScope
        val foo: Deferred<Int> = async { getFoo() }
        val bar: Deferred<Int> = async { getBar() }
        foo.await() + bar.await()
    }

    suspend fun getSumManual(): Int {

        // method coroutine scope  manual
        val scope = CoroutineScope(Dispatchers.IO)
        val foo = scope.async { getFoo() }
        val bar = scope.async { getBar() }
        return foo.await() + bar.await()
    }

    @Test
    fun testCoroutineScopeFunction() {

        val scope: CoroutineScope = CoroutineScope(Dispatchers.IO) // instance dispatchers.IO untuk mengabungkan beberapa suspend function, lalu return value tersebut
        val job = scope.launch {
            val result = getSum()
            println("Result: $result")
        }

        // Note: method suspend 'join()' harus dipanggil hanya dari CoroutineScope atau method suspend lainnya
        runBlocking {
            job.join() // join() menunggu proses hingga selesai
        }

        /**
         * result:
         * Foo DefaultDispatcher-worker-3 @coroutine#3
         * Bar DefaultDispatcher-worker-1 @coroutine#4
         * Result: 40
         */

    }


}