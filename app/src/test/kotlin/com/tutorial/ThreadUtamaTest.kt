package com.tutorial

import org.junit.jupiter.api.Test
import java.lang.Thread

class ThreadUtamaTest {

    /**
     * Thread Utama
     * ● Saat kita menjalankan sebuah process (aplikasi) Kotlin di JVM, secara otomatis proses tersebut
     *    akan jalan di sebuah thread utama
     * ● Thread utama tersebut bernama main thread (JVM)
     * ● Saat kita menjalankan process JUnit, JUnit pun berjalan di thread tersendiri
     * ● Begitu juga jika kita membuat aplikasi kotlin Android, aplikasi tersebut akan berjalan di sebuah thread
     */

    @Test
    fun testMainthread() {

         val mainThread = Thread.currentThread().name // Test worker
         println("Nama Thread: $mainThread")
    }

}