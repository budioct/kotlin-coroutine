package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class SemaphoreTest {

    /**
     * Semaphore
     * ● Sama seperti Mutex, Semaphore juga digunakan sebagai object untuk locking
     * ● Namun yang membedakan, pada Mutex, kita hanya memperbolehkan 1 coroutine yang bisa
     *    mengakses nya pada satu waktu
     * ● Namun pada Semaphore, kita bisa menentukan berapa jumlah corotine yang boleh mengakses nya pada satu waktu
     */

    @Test
    fun testCoroutineSemaphore() {

        /**
         * note: semakin semaphore permits banyak maka akan lebih cepat komputasi nya. tetapi resikonya ada beberapa job coroutine yang lewat
         */

        var counter = 0 // hasil dari coroutine
        val dispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher() // dispatcher (Thread)
        val scope = CoroutineScope(dispatcher)
        val semaphore = Semaphore(permits = 2) // serupa dengan mutex.. tetapi semaphore bisa menentukan jumlah coroutine yang boleh mengakses pada satu waktu

        // method repeat() untuk loop, serupa dengan loop for, while, do while, dan range
        // kita buat loop 100 yang di dalamnya loop 1000.. jadi 1)00 x 1_000 = 100_000
        repeat(100) {
            scope.launch {
                repeat(1000) {
                    semaphore.withPermit {
                        // withPermit() // merubah immutable menjadi mutable (interinsic lock)
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