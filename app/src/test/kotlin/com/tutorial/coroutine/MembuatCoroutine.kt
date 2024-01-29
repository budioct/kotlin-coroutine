package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.concurrent.thread

class MembuatCoroutine {

    /**
     * Membuat Coroutine
     * ● Coroutine tidak bisa berjalan sendiri, dia perlu berjalan di dalam sebuah Scope.
     * ● Salah satu scope yang bisa kita gunakan adalah GlobalScope (masih banyak scope yang ada, dan
     *    akan kita bahas nanti dimateri tersendiri)
     * ● Untuk membuat coroutine, kita bisa menggunakan method launch()
     * ● Dan di dalam coroutine, kita bisa memanggil suspend function
     */

    @Test
    fun testCoroutine() {

        // GlobalScope untuk menjalankan Coroutine, untuk menjalankanya dengan method launch()
        GlobalScope.launch {
            world() // menjalankan suspend function di Kotlin // tanpa harus mem-block thread yang sedang menjalankannya.
        }

        println("Hello")
        // Thread.sleep(2_000) // tidak ada error compile tetapi akan mem-block thread yang sedang berjalan saat ini
        // delay(2_000) // Fungsi penangguhan 'delay()' harus dipanggil hanya dari coroutine atau fungsi penangguhan lainnya

        runBlocking {
            delay(2_000)
        } // fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T  // supaya kemampuan coroutine bisa digunakan
    }

    // suspending function
    suspend fun world() {

        delay(1_000)
        (1..100).forEach {
            println("world")
        }
        /**
         * result:
         * Hello
         * world
         * world
         * n
         */
    }

    /**
     * Coroutine Sangat Ringan
     * ● Seperti yang sebelumnya dibahas, coroutine itu ringan dan cepat, sehingga saat kita membuat
     *    coroutine dalam jumlah besar, ini tidak akan berdampak terlalu besar dengan memory yang kita gunakan
     * ● Sekarang kita akan coba bandingkan membuat thread dan coroutine dalam jumlah banyak
     */

    @Test
    fun testThread() {
        (1..100000).forEach {
            thread {
                Thread.sleep(1000)
                println("Thread $it: ${Date()}")
            }
        }

        Thread.sleep(10_000)
        println("Done")
    }

    @Test
    fun testCoroutines() {
        (1..100000).forEach {
            GlobalScope.launch {
                delay(1000)
                println("Coroutine $it: ${Date()}")
            }
        }

        runBlocking { delay(1000) }
        println("Done")
    }

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
                println("Hello World")
            }
        }
    }

    @Test
    fun testJobStart() {

        // menjalankan job
        runBlocking {
            val job: Job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                delay(2000)
                println("Hello World")
            } // instance coroutine dalam job

            job.start() // start() menjalankan job
            delay(3000) // menunggu proses job manual
        }
    }

    @Test
    fun testJobJoin() {

        // menunggu job
        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(10_000)
                println("Hello World")
            }

            job.join() // join() menunggu proses job sampai selesai otomatis
        }
    }

    fun testJobCancel() {

        // membatalkan job
        runBlocking {
            val job: Job = GlobalScope.launch {
                delay(2000)
                println("Hello World")
            }

            job.cancel() // cancel() membatalkan job
            delay(3000) // menunggu proses job manual
        }
    }


}