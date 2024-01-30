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
        } // jalan asynchronous

        println("Hello")
        // Thread.sleep(2_000) // tidak ada error compile tetapi akan mem-block thread yang sedang berjalan saat ini
        // delay(2_000) // Fungsi penangguhan 'delay()' harus dipanggil hanya dari coroutine atau fungsi penangguhan lainnya

        println("MENUNGGU")
        runBlocking {
            delay(2_000)
        } // fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T  // supaya kemampuan coroutine bisa digunakan
        println("SELESAI")
    }

    // suspending function
    suspend fun world() {

        delay(1_000)
        (1..5).forEach {
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
        (1..10000).forEach {
            thread {
                Thread.sleep(1000)
                println("Thread $it: ${Date()} ${Thread.currentThread().name}")
            } // jalan asynchronous
        }// Setiap loop membuat thread baru

        Thread.sleep(3000) // menunggu proses manual
        println("Done")
    }

    @Test
    fun testCoroutines() {
        (1..100000).forEach {
            GlobalScope.launch {
                delay(1000)
                println("Coroutine $it: ${Date()} ${Thread.currentThread().name}") // Coroutine 29: Tue Jan 30 09:50:32 WIB 2024 DefaultDispatcher-worker-4 @coroutine#29
            } // jalan asynchronous
        }// Setiap loop membuat thread baru

        runBlocking { delay(3000) }
        println("Done")
    }

}