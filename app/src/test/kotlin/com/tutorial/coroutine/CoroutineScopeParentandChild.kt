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
     *
     * Note:
     * CoroutineContext: sebuah kumpulan data CoroutineContext.Element, yang paling utama (Parent Object Coroutine)
     * Dispatcher: digunakan untuk menentukan thread mana yang bertanggung jawab untuk mengeksekusi coroutine
     */

    @Test
    fun testChildDispatcher() {

        // Dispatcher Child Scope

        val parentDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val parentScope = CoroutineScope(parentDispatcher)

        val job = parentScope.launch {
            println("Start Block Coroutine Scope Parent 1 finish")
            println("Parent thread is: ${Thread.currentThread().name}")
            coroutineScope {
                launch {
                    println("Start Block Coroutine Scope Child 1 finish")
                    println("Child thread is: ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.join() // menunggu proses eksekusi hingga selesai
        }

        /**
         * result:
         * Start Block Coroutine Scope Parent 1 finish
         * Parent thread is: pool-1-thread-1 @coroutine#1
         * Start Block Coroutine Scope Child 1 finish
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
                    println("Start Block Coroutine Scope Child 1 finish")
                    println("Child thread is: ${Thread.currentThread().name}")
                }
            }
            delay(1000) // delay() mengecek apakah proses itu di batalkan atau tidak
            println("Start Block Coroutine Scope Parent 1 finish")
            println("Parent thread is: ${Thread.currentThread().name}")
        }

        runBlocking {
            job.cancelAndJoin() // cancelAndJoin() Membatalkan pekerjaan dan menangguhkan coroutine yang dipanggil hingga pekerjaan yang dibatalkan selesai.
        }

        /**
         * result: tidak ada hasil,
         */

    }

    /**
     * Coroutine Parent & Child
     * ● Selain coroutine scope, coroutine sendiri bisa memiliki child coroutine
     * ● Saat membuat coroutine child, secara otomatis kita akan mewarisi coroutine context yang ada di coroutine parent
     * ● Dan coroutine parent akan menunggu sampai eksekusi coroutine child nya selesai semua
     */

    @Test
    fun testParentChildCoroutine() {

        runBlocking {
            val job = GlobalScope.launch {
                // block parent
                println("Start Block Coroutine Parent")
                launch {
                    // block child
                    delay(2000)
                    println("Start Block Coroutine Child 1 finish")
                }

                launch {
                    delay(4000)
                    println("Start Block Coroutine Child 2 finish")
                }
                delay(1000)
                println("Finish Block Coroutine Parent")
            }

            job.join() // join() menunggu proses hingga selesai
        }

        /**
         * result:
         * Start Block Coroutine Parent
         * Finish Block Coroutine Parent
         * Start Block Coroutine Child 1 finish
         * Start Block Coroutine Child 2 finish
         */

    }

    /**
     * cancelChildren Function
     * ● Sebelumnya sudah dibahas kalo coroutine itu memiliki parent dan child
     * ● Coroutine akan direpresentasikan sebagai Job (Deferred tuturan dari Job), dan di Job kita bisa
     *    mendapatkan semua children nya menggunakan field children
     * ● Selain itu ada sebuah function bernama cancelChildren, function ini bisa kita gunakan untuk
     *    membatalkan semua coroutine children.
     * ● Jika kita membatalkan Job parent, kita tidak perlu membatalkan children nya secara manual,
     *    karena saat Job di batalkan, semua child nya akan dibatalkan
     */

    @Test
    fun testCoroutineCancelChildren() {

        runBlocking {
            val job = GlobalScope.launch {
                // block parent
                launch {
                    // block child
                    delay(2000)
                    println("Start Block Coroutine Child 1 finish")
                }
                launch {
                    delay(4000)
                    println("Start Block Coroutine Child 2 finish")
                }
                delay(1000)
                println("Finish Block Coroutine Parent")
            }
            delay(2000)
            job.cancelChildren() // cancelChildren() mebatalkan semua job children
            job.join() // join() menunggu proses eksekusi hingga selesai

        }
        /**
         * result: karena keburu waktu delay yang di tunggu 2 detik. coroutine child 2 tidak di eksekusi
         * Finish Block Coroutine Parent
         * Start Block Coroutine Child 1 finish
         */

    }


}