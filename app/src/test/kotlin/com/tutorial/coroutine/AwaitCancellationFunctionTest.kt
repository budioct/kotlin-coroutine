package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class AwaitCancellationFunctionTest {

    /**
     * awaitCancellation Function
     * ● Secara default, sebuah coroutine akan berhenti ketika seluruh code selesai dijalankan
     * ● Jika ada kebutuhan kita tidak mau coroutine berhenti sampai di Job nya di cancel, maka kita bisa
     *    menggunakan function awaitCancellation
     * ● Function awaitCancellation akan throw CancellationException jika job di cancel, dan tidak akan
     *    menghentikan coroutine jika belum di cancel
     */

    @Test
    fun testAwaitCancellation() {

        runBlocking {
            val job = launch {
                try {
                    println("Start Job ${Thread.currentThread().name}")
                    awaitCancellation() // dimana coroutine tidak akan di berhentikan jika job nya di cancel
                    // tidak perlu loop manual/ cek isActinve
                    //while (isActive){  } // tidak perlu seperti ini
                } finally {
                    println("Cancellation ${Thread.currentThread().name}")
                }
            }
            delay(5_000)
            job.cancelAndJoin()
            /**
             * result:
             * Start Job Test worker @coroutine#2
             * Cancellation Test worker @coroutine#2
             */
        }
    }

}