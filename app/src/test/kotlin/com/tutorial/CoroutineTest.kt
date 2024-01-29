package com.tutorial

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.Date

class CoroutineTest {

    /**
     * Pengenalan Coroutine
     *
     * Problem di Java Thread
     * ● Java Thread tidak didesain untuk melakukan Concurrency Programming.
     * ● Java Thread hanya bisa melakukan satu hal sampai selesai, baru melakukan hal lain
     * ● Salah satu implementasi Concurrency Programming yang saat ini paling populer adalah Go-Lang Goroutine
     * ● Untungnya di Kotlin, ada fitur yang bernama Coroutine, salah satu implementasi Concurrency Programming
     *
     * Pengenalan Coroutine
     * ● Coroutine sering diistilahkan sebagai lightweight thread (thread ringan), walaupun sebenernya
     *    coroutine sendiri bukanlah thread.
     * ● Coroutine sebenarnya di eksekusi di dalam thread, namun dengan coroutine sebuah thread bisa
     *    memiliki kemampuan untuk menjalankan beberapa coroutine secara bergantian (concurrent)
     * ● Artinya jika sebuah thread menjalankan 10 coroutine, sebenarnya thread akan menjalankan
     *    coroutine satu per satu secara bergantian
     * ● Perbedaan lain thread dan coroutine adalah coroutine itu murah dan cepat, sehingga kita bisa
     *    membuat ribuan atau bahkan jutaan coroutine secara cepat dan murah tanpa takut kelebihan memory
     *
     * Suspend Function
     * ● Suspend computation adalah komputasi yang bisa ditangguhkan (ditunda waktu eksekusinya).
     * ● Sebelumnya kita tahu untuk menangguhkan komputasi di Java, kita biasanya menggunakan
     *    Thread.sleep(), sayangnya Thread.sleep() akan mem-block thread yang sedang berjalan saat ini. Sehingga tidak bisa digunakan.
     * ● Kotlin memiliki sebuah fitur bernama suspending function, dimana kita bisa menangguhkan waktu
     *    eksekusi sebuah function, tanpa harus mem-block thread yang sedang menjalankannya.
     * ● Syarat menjalankan suspend function di Kotlin adalah, harus dipanggil dari suspend function lainnya.
     */

    @Test
    fun testSuspend() {

        runBlocking {
            helloWorld() // menjalankan suspend function di Kotlin // tanpa harus mem-block thread yang sedang menjalankannya.
        }
    }

    // suspending function
    suspend fun helloWorld() {
        println("Hello World: ${Date()}")
        delay(2_000) // suspend fun delay(timeMillis: Long) // kemampuan corutine kotlin
        println("Hello World: ${Date()}")

        /**
         * result:
         * Hello World: Mon Jan 29 21:10:15 WIB 2024
         * Hello World: Mon Jan 29 21:10:17 WIB 2024
         */
    }


}