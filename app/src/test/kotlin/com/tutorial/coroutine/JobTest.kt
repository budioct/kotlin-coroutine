package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.system.measureTimeMillis

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

    /**
     * joinAll Function
     * ● Kadang kita akan membuat coroutine lebih dari satu sekaligus
     * ● Untuk menunggu semua Job coroutine selesai berjalan kita bisa menggunakan join() function
     * ● Namun jika kita panggil satu-satu tiap Job coroutine nya, akan sangat mengganggu sekali
     * ● Kotlin menyediakan joinAll(jobs) function untuk menunggu semua job selesai
     */

    @Test
    fun testJoinAll() {

        val time = measureTimeMillis {
            runBlocking {
                val job1 = GlobalScope.launch {
                    delay(1000)
                    println("Job 1: ${Thread.currentThread().name}")
                }
                val job2 = GlobalScope.launch {
                    delay(1000)
                    println("Job 2: ${Thread.currentThread().name}")
                }

                // cara ini bertele tele
                // job1.join()
                // job2.join()

                // cara ringkas
                joinAll(job1, job2) // joinAll(vararg jobs: Job): Unit = jobs.forEach { it.join() } // jalan secara aysnchronouse. bukan sequential
            }
        }
        println("time: $time")

    }


}