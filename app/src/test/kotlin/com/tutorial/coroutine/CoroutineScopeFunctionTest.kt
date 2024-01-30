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
        return 10
    }

    suspend fun getBar(): Int {
        delay(1000)
        return 30
    }

    suspend fun getSum(): Int = coroutineScope {
        val foo: Deferred<Int> = async { getFoo() }
        val bar: Deferred<Int> = async { getBar() }
        foo.await() + bar.await()
    }

    @Test
    fun testCoroutineScope(){

        val scope: CoroutineScope = CoroutineScope(Dispatchers.IO) // instance dispatchers.IO untuk mengabungkan beberapa suspend function, lalu return value tersebut
        val job = scope.launch {
            val result = getSum()
            println("Result: $result")
        }

        runBlocking {
            job.join() // join() menunggu proses hingga selesai
        }

        /**
         * result:
         * Result: 40
         */

    }


}