package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoroutineScopeParentandChild {

    /**
     * Coroutine Scope Parent & Child
     * ● Saat kita membuat sebuah coroutine scope dengan menggunakan function coroutineScope,
     *    sebenarnya kita telah membuat child scope dari parent scope nya
     * ● Coroutine scope itu saling berkaitan antara parent dan child nya
     * ● Saat kita membuat child scope, secara otomatis child scope akan menggunakan dispatcher milik parent
     * ● Dan saat kita membatalkan parent scope, maka semua child scope nya pun akan dibatalkan
     */

    @Test
    fun testChildDispatcher() {

        // Dispatcher Child Scope

        val parentDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val parentScope = CoroutineScope(parentDispatcher)

        val job = parentScope.launch {
            println("Parent thread is: ${Thread.currentThread().name}")
            coroutineScope {
                launch {
                    println("Child thread is: ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.join() // menunggu proses eksekusi hingga selesai
        }

        /**
         * result:
         * Parent thread is: pool-1-thread-1 @coroutine#1
         * Child thread is: pool-1-thread-1 @coroutine#3
         */

    }

    @Test
    fun testChildDispatcherCancel() {

        // Membatalkan Dispatcher Parent Scope

        val parentDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val parentScope = CoroutineScope(parentDispatcher)

        val job = parentScope.launch {
            coroutineScope {
                launch {
                    delay(1000) // delay() mengecek apakah proses itu di batalkan atau tidak
                    println("Child thread is: ${Thread.currentThread().name}")
                }
            }
            delay(1000) // delay() mengecek apakah proses itu di batalkan atau tidak
            println("Parent thread is: ${Thread.currentThread().name}")
        }

        runBlocking {
            job.cancelAndJoin() // cancelAndJoin() Membatalkan pekerjaan dan menangguhkan coroutine yang dipanggil hingga pekerjaan yang dibatalkan selesai.
        }

        /**
         * result: tidak ada hasil,
         */

    }

}