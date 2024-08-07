package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.Date

class CoroutineScopeTest {

    /**
     * Coroutine Scope
     * ● Sampai saat ini, kita selalu menggunakan GlobalScope untuk membuat coroutine. Kita belum
     *    membahas sama sekali tentang apa itu GlobalScope? GlobalScope sebenarnya adalah salah satu implementasi Coroutine Scope
     * ● Semua coroutine itu sebenarnya dijalankan dari sebuah coroutine scope.
     * ● Function launch dan async yang selama ini kita gunakan, sebenarnya adalah extention function dari coroutine scope
     * ● Secara sederhana, coroutine scope adalah object life cycle nya coroutine.
     *
     * Penggunaan Coroutine Scope
     * ● CoroutineScope biasanya digunakan dalam sebuah flow yang saling berhubungan
     * ● Misal saat kita membuka sebuah halaman di mobile, maka kita akan membuat screen, lalu
     *    mengambil data ke server, lalu setelah mendapatkanya kita akan menampilkan data tersebut di screen.
     * ● Flow tersebut harus saling terintegrasi, jika misal flow tersebut sukses maka harus sukses semua,
     *    jika dibatalkan, maka harus dibatalkan proses selanjutnya
     * ● Hal tersebut jika diibaratkan tiap aktivitas adalah coroutine, maka flow tersebut di simpah dalam sebuah coroutine scope.
     *
     * GlobalScope
     * ● Sebelumnya kita selalu menggunakan GlobalScope untuk membuat coroutine
     * ● Sebenarnya penggunaan GlobalScope tidak dianjurkan dalam pembuatan aplikasi
     * ● Hal ini dikarenakan, jika semua coroutine menggunakan GlobalScope, maka secara otomatis akan
     *    sharing coroutine scope, hal ini agak menyulitkan saat kita misal ingin membatalkan sebuah flow,
     *    karena saat sebuah coroutine scope di batalkan, maka semua coroutine yang terdapat di scope tersebut akan dibatalkan
     */

    @Test
    internal fun testNewScope() {

        // key internal adalah visibilty modifier. internal Artinya hanya bisa diakses di module/project yang sama.

        // CoroutineScope(context: CoroutineContext): CoroutineScope // CoroutineScope membukus coroutinecontext
        // Dispatchers seperti Job, tugasnya yang menentukan courutine ada di thread mana
        val scope = CoroutineScope(Dispatchers.Default) // fun CoroutineScope(context: CoroutineContext): CoroutineScope // Dispatchers.Default secara otomatis akan tambahkan Job

        scope.launch {
            delay(1000)
            println("Run ${Thread.currentThread().name}")
        }

        scope.launch {
            delay(1000)
            println("Run ${Thread.currentThread().name}")
        }

        runBlocking {
            delay(2000)
            println("Done")
        }

        /**
         * result:
         * Run DefaultDispatcher-worker-1 @coroutine#2
         * Run DefaultDispatcher-worker-2 @coroutine#1
         * Done
         */

    }

    @Test
    internal fun testScopeCancle() {

        // key internal adalah visibilty modifier. internal Artinya hanya bisa diakses di module/project yang sama.

        // CoroutineScope(context: CoroutineContext): CoroutineScope // CoroutineScope membukus coroutinecontext
        // Dispatchers seperti Job, tugasnya yang menentukan courutine ada di thread mana
        val scope = CoroutineScope(Dispatchers.Default) // fun CoroutineScope(context: CoroutineContext): CoroutineScope // Dispatchers.Default secara otomatis akan tambahkan Job

        scope.launch {
            delay(2000)
            println("Run ${Thread.currentThread().name}")
        }

        scope.launch {
            delay(2000)
            println("Run ${Thread.currentThread().name}")
        }

        runBlocking {
            delay(1000)
            scope.cancel() // cancel() membatalkan coroutine scope
            delay(2000)
            println("Done")
        }

        /**
         * result: object.cancle : CoroutineScope.  semua job atau deffered<T> akan di batalkan
         * Done
         */

    }

}