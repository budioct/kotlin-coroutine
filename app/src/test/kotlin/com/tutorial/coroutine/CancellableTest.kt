package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.Date

class CancellableTest {

    /**
     * Membatalkan Coroutine
     * ● Sebelumnya kita sudah tahu bahwa Job bisa kita batalkan menggunakan function cancel
     * ● Membatalkan coroutine kadang diperlukan, misal ketika kode program di coroutine terlalu lama
     * ● Semua function yang ada di package kotlinx.coroutines bisa dibatalkan.
     * ● Namun, jika dalam kode program kita, kita tidak mengecek status cancel, maka coroutine yang kita
     *    buat tidak akan bisa dibatalkan
     */

    @Test
    fun testCanNotCancle() {

        runBlocking {
            val job = GlobalScope.launch {
                println("Start ${Date()}")
                Thread.sleep(2_000)
                println("Done ${Date()}")
            }

            job.cancel() // cancel() membatalkan proses
            job.join() // join() menunggu proses hinga selesai

            /**
             * result: proses tidak bisa di batalkan karna  Thread.sleep() tidak bisa berjalan di runBlocking()
             * Start Tue Jan 30 11:24:02 WIB 2024
             * Done Tue Jan 30 11:24:04 WIB 2024
             */
        }
    }

    /**
     * Agar Coroutine Bisa Dibatalkan
     * ● Untuk mengecek apakah coroutine masih aktif atau tidak (selesai / dibatalkan), kita bisa
     *     menggunakan field isActive milik CoroutineScope
     * ● Untuk menandakan bahwa coroutine dibatalkan, kita bisa throw CancellationException
     */

    @Test
    fun testCancleCoroutine() {

        runBlocking {
            val job = GlobalScope.launch {

                // field isActive digunakan mengecek apakah coroutine masih aktif atau tidak (selesai /dibatalkan)
                if (!isActive) {
                    throw CancellationException() // menandakan untuk coroutine di batalkan
                }
                println("Start ${Date()}")

                ensureActive() // ensureActive() // adalah pengecekan sama seperti condition isActive
                Thread.sleep(2_000)

                ensureActive()
                println("Done ${Date()}")
            }

            job.cancel() // cancel() membatalkan proses
            job.join() // join() menunggu proses hinga selesai

            /**
             * result: proses bisa di batalkan karna pengecekan coroutine sudah tidak aktif, pengaruh dari job.cancle()
             * Start Tue Jan 30 11:30:58 WIB 2024
             */
        }
    }

    /**
     * Setelah Coroutine di Cancel
     * ● Standard coroutine adalah, ketika sebuah coroutine dibatalkan, maka kita perlu throw CancellableException
     * ● Karena throw CancellableException, artinya jika kita ingin melakukan sesuatu ketika sebuah
     *    coroutine dibatalkan, kita bisa menggunakan block try-finally
     */

    @Test
    fun testCancleFinally() {
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start ${Date()}")
                    delay(2_000)
                    println("Start ${Date()}") // tidak di jalankan karena keburu tidak aktif saat pengecekan coroutine
                } finally {
                    println("Finally") // berhasil atau gagal akan di jalankan
                }
            }

            job.cancelAndJoin() // cancelAndJoin() // method exstantion, ingin melakukan sesuatu jika coroutine dibatalkan

            /**
             * result:
             * Start Tue Jan 30 11:37:07 WIB 2024
             * Finally
             */
        }
    }


}