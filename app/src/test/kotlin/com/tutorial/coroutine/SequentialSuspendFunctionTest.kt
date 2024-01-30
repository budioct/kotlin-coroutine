package com.tutorial.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class SequentialSuspendFunctionTest {

    /**
     * Suspend Function Tidak Async
     * ● Secara default, sebenarnya sebuah suspend function tidaklah async, saat kita mengakses beberapa
     *    suspend function, semua akan dieksekusi secara sequential
     */

    suspend fun getFoo(): Int {

        delay(1_000)
        return 10
    }

    suspend fun getBar(): Int {

        delay(1_000)
        return 10
    }

    @Test
    fun testSuspend(){

        runBlocking {
            val time = measureTimeMillis {
                getBar() // dari methos suspend
                getBar() // dari methos suspend
            }

            println("Total time: $time")
            /**
             * result: butuh waktu 2 detik.. berarti method suspend tidak berjalan secara asynchronouse tetapi berjalan secara synchronouse(berurutan)
             * Total time: 2030
             */
        }
    }

    /**
     * Concurrent Dengan Launch
     * ● Jadi agar sebuah suspend function bisa berjalan secara concurrent, kita perlu menggunakan
     *    function launch ketika memanggil suspend function tersebut
     * ● Hal yang menyulitkan adalah, launch function mengembalikan Job, dan di dalam Job, kita tidak bisa
     *    mengembalikan nilai hasil dari coroutine.
     * ● Hal ini bisa dianalogikan bahwa launch itu alah menjalankan coroutine yang mengembalikan nilai
     *    Unit (tidak mengembalikan nilai)
     */

    @Test
    fun testSuspendCoroutine(){

        runBlocking {
            val time = measureTimeMillis {
                val job1 = GlobalScope.launch { getFoo() }
                val job2 = GlobalScope.launch { getBar() }

                job1.join() // join() menunggu proses hingga selesai
                job1.join()
            }

            println("Total time: $time")
            /**
             * result: butuh waktu 1 detik. berarti method suspend sudah berjalan secara aysnchronouse tetapi tidak bisa mendapatkan return value dari method nya
             *         karena GlobalScope.launch(Runabble) job seperti Runnable tidak bisa mengembalikan return value
             * Total time: 1037
             */
        }
    }



}