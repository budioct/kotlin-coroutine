package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.Date

class TimeOut {

    /**
     * Menggunakan Timeout
     * ● Kadang kita ingin sebuah coroutine berjalan tidak lebih dari waktu yang telah ditentukan
     * ● Sebenarnya kita bisa melakukan hal tersebut secara manual, dengan cara menjalankan 2 job,
     *    dimana job ke dua akan membatalkan job pertama jika job pertama terlalu lama
     * ● Namun hal ini tidak perlu kita lakukan lagi, terdapat function withTimeout untuk melakukan hal tersebut.
     * ● Jika terjadi timeout melebihi waktu yang telah kita tentukan, maka secara otomatis function
     *    withTimeout akan thro TimeoutCancellationException
     *
     * Timeout Tanpa Membatalkan Coroutine
     * ● withTimeout akan throw TimeoutCancellationException, dimana itu adalah turunan dari
     *    CancellationException
     * ● Hal ini berakibat coroutine akan berhenti karena kita throw exception
     * ● Jika ada kasus dimana kita tidak ingin menghentikan coroutine-nya, kita bisa menggunakan
     *    function withTimeoutOrNull, dimana ini tidak akan throw exception, hanya mengembalikan null jika terjadi timeout
     */

    @Test
    fun testTimeOut() {

        // test coroutine dengan 100 kali dengan delay 1 detik

        runBlocking {
            val job = GlobalScope.launch {
                println("Start Coroutine")
                // repeat() looping
                repeat(100) {
                    delay(1000)
                    println("$it ${Date()}")
                }
            }

            job.join() // join() // menuggu proses hinga selesai
        }
    }

    @Test
    fun testTimeoutOrNull() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start Coroutine")
                // withTimeoutOrNull(timeMillis: Long // jika melebihi waktu 5 detika akan exception
                withTimeoutOrNull(5_000) {
                    // repeat() looping
                    repeat(100) {
                        delay(1000)
                        println("$it ${Date()}")
                    }
                }
            }

            job.join() // join() // menuggu proses hinga selesai
            /**
             * result: detik ke 5 poses langsung di hentikan
             * Coroutine Start
             * 0 Tue Jan 30 12:11:08 WIB 2024
             * 1 Tue Jan 30 12:11:09 WIB 2024
             * 2 Tue Jan 30 12:11:10 WIB 2024
             * 3 Tue Jan 30 12:11:11 WIB 2024
             */
        }
    }

}