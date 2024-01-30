package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*

class JobTest {

    /**
     * Job
     * ● Saat sebuah coroutine dijalankan menggunakan function launch, sebenarnya function tersebut
     *    mengembalikan sebuah object Job
     * ● Dengan object Job, kita bisa menjalankan, membatalkan atau menunggu sebuah coroutine
     */

    @Test
    fun testJob() {

        // coroutine di runBlocking
        // blocking tidak akan menunggu coroutine
        runBlocking {
            GlobalScope.launch {
                delay(2000)
                println("Coroutine: ${Date()} ${Thread.currentThread().name}")
            } // jalan asynchronous, tidak akan menuggu runBlocking()
        }
    }

    @Test
    fun testJobStart() {

        runBlocking {
            val job: Job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                delay(2000)
                println("Coroutine: ${Thread.currentThread().name}")
            } // jalan asynchronous

            job.start() // start() menjalankan job
            delay(3000) // menunggu proses job manual
        }
    }

    @Test
    fun testJobJoin() {

        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(10_000)
                println("Coroutine: ${Thread.currentThread().name}")
            } // jalan asynchronous

            job.join() // join() menunggu proses job sampai selesai otomatis
        }
    }

    @Test
    fun testJobCancel() {

        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(2000)
                println("Coroutine: ${Thread.currentThread().name}")
            }

            job.cancel() // cancel() membatalkan job
            delay(3000) // menunggu proses job manual
        }
    }


}