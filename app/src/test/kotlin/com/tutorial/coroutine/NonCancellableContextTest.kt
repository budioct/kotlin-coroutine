package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.*

class NonCancellableContextTest {

    /**
     * Check isActive di Finally
     * ● Sebelumnya kita tahu bahwa setelah coroutine di cancel, dan jika kita ingin melakukan sesuatu,
     *    kita bisa menggunakan block try-finally
     * ● Namun dalam block finally, kita tidak bisa menggunakan suspend method yang mengecek isActive,
     *    karena otomatis akan bernilai false, dan otomatis batal
     */

    @Test
    fun testCancleFinally() {
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start Job ${Date()}")
                    delay(2_000)
                    println("End Job ${Date()}") // tidak di jalankan karena keburu tidak aktif saat pengecekan coroutine
                } finally {
                    println(isActive) // cek apakah job nya masih aktif atau tidak aktif
                    delay(1000) // cek apakah job nya masih aktif atau tidak aktif
                    println("Finally") // tidak di jalankan
                }
            }

            job.cancelAndJoin() // cancelAndJoin() Membatalkan pekerjaan dan menangguhkan coroutine yang dipanggil hingga pekerjaan yang dibatalkan selesai.
            /**
             * result: kenapa hasil dari endjob dan finallay tidak di print. isActive itu cek coroutineContext[Job] masih active atau tidak, jadi sebenernua isActive itu access coroutineContext dan ngambil object Job nya lalu di cek job nya active atau tidak
             *         delay() juga sama seperti field 'isActive' ketika di lakukan delay() Job nya masih active apa tidak, karna ketika kita sudah cancel artinya dia akan throw CancellationException maka code di bawahnya tidak di eksekusi lagi
             *
             * Start Job DefaultDispatcher-worker-1 @coroutine#2
             * false
             */

        }
    }

    /**
     * Non Cancellable Context
     * ● Jika kita butuh memanggil suspend function yang mengecek isActive di block finally, dan berharap
     *    tidak dibatalkan eksekusinya, maka kita bisa menggunakan NonCancellable
     * ● NonCancellable adalah coroutine context yang mengoverride nilai-nilai cancellable sehingga
     *    seakan-akan coroutine tersebut tidak di batalkan
     */

    @Test
    fun testNonCancelable() {

        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start Job ${Thread.currentThread().name}")
                    delay(1000) // tidak di eksekusi
                    println("End Job ${Thread.currentThread().name}") // tidak di eksekusi
                } finally {
                    // withContext(NonCancellable){} // akan override method2 dan attribute dari NonCancellable turunan dari CoroutineContext.. ini suapaya code di block finally tidak bisa di batalkan
                    withContext(NonCancellable) {
                        println(isActive) // cek apakah job nya masih aktif atau tidak aktif
                        delay(1000) // cek apakah job nya masih aktif atau tidak aktif
                        println("Finallay ${Thread.currentThread().name}") // di jalnkan jika kodisi attribute isActive = true
                    }
                }
            }

            job.cancelAndJoin() // cancelAndJoin() Membatalkan pekerjaan dan menangguhkan coroutine yang dipanggil hingga pekerjaan yang dibatalkan selesai.
            /**
             * result:
             * Start Job DefaultDispatcher-worker-1 @coroutine#2
             * true
             * Finallay DefaultDispatcher-worker-1 @coroutine#2
             */
        }
    }


}