package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class MutexTest {

    /**
     * Shared Mutable State (Mutex)
     * ● Saat kita belajar Kotlin Collection, kita sudah tau tentang Immutable dan Mutable
     * ● Saat menggunakan coroutine, sangat disarankan untuk menggunakan data Immutable, apalagi jika data tersebut di sharing ke beberapa coroutine
     * ● Hal ini agar datanya aman, karena tidak bisa diubah oleh coroutine lain, jadi tidak akan terjadi problem race condition
     * ● Namun, bagaimana jika ternyata kita memang butuh sharing mutable data di beberapa coroutine secara sekaligus?
     */

    @Test
    fun testCoroutineRaceCondition() {

        var counter = 0 // hasil dari coroutine
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher() // dispatcher (Thread)
        val scope = CoroutineScope(dispatcher)

        // method repeat() untuk loop, serupa dengan loop for, while, do while, dan range
        // kita buat loop 100 yang di dalamnya loop 1000.. jadi 1)00 x 1_000 = 100_000
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    counter++
                }
            }
        }

        runBlocking {
            delay(5_000)
        }
        println("Total Counter: $counter")
        /**
         * result: hasil tidak sesuai ekpetasi yaitu 100_000.. ini race condition, karena ini berjalan secara Asynchronouse/parallel tidak saling menunggu. jadi ada job yang terlewat
         * Total Counter: 53971
         */

    }

    /**
     * Mutex
     * ● Mutex (Mutual exclusion) adalah salah satu fitur di Kotlin Coroutine untuk melakukan proses locking
     * ● Dengan menggunakan mutex, kita bisa pastikan bahwa hanya ada 1 coroutine yang bisa mengakses
     *    kode tersebut, code coroutine yang lain akan menunggu sampai coroutine pertama selesai
     */

    @Test
    fun testCoroutineMutex() {

        var counter = 0 // hasil dari coroutine
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher() // dispatcher (Thread)
        val scope = CoroutineScope(dispatcher)
        val mutex = Mutex() // interface untuk menangani coroutine berjalan secara Asynchoronouse concurrency


        // method repeat() untuk loop, serupa dengan loop for, while, do while, dan range
        // kita buat loop 100 yang di dalamnya loop 1000.. jadi 1)00 x 1_000 = 100_000
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    mutex.withLock {
                        // withLock() // merubah immutable menjadi mutable (interinsic lock)
                        // job mutable harus di wrapper dengan mutext, supaya berjalan concurrency, tidak ada job yang terlewat
                        counter++
                    }
                }
            }
        }

        runBlocking {
            delay(2_000)
        }
        println("Total Counter: $counter")
        /**
         * result: hasil sesuai ekpetasi yaitu 100_000. karena ini berjalan secara Asynchronouse/concurrency bergantian. tidak ada job yang terlewat
         * Total Counter: 100000
         */

    }


}