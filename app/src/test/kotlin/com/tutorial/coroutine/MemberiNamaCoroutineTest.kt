package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class MemberiNamaCoroutineTest {

    /**
     * Memberi Nama Coroutine
     * ● Selain dispatcher, salah satu coroutine context yang lain adalah CoroutineName
     * ● CoroutineName bisa kita gunakan untuk mengubah nama coroutine sesuai dengan yang kita mau
     * ● Hal ini sangat bermanfaat ketika kita melakukan proses debugging
     *
     * note: bahwa ternyata saat kita melakukan delay(), suspend function tersebut akan di trigger di thread yang berbeda.
     * Bagaimana caranya jika kita ingin menjalankan code program kita dalam coroutine di thread yang berbeda dengan thread coroutine awalnya?
     * Function withContext() sebenarnya bisa kita gunakan untuk mengganti CoroutineContext, namun karena CoroutineDispatcher adalah turunan CoroutineContext, jadi kita bisa otomatis mengganti thread yang akan digunakan di coroutine menggunakan function withContext()
     */

    @Test
    fun coroutineName() {

        // menbuah nama lewat launch(CoroutineContext)
        // CoroutineName() method untuk merubah nama coroutine
        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch(CoroutineName("Parent")) {
            println("Thread in thread: ${Thread.currentThread().name}") // nama coroutine berubah --> @Parent#1

            withContext(CoroutineName("Child")) {
                println("Thread in thread: ${Thread.currentThread().name}") // nama coroutine berubah --> @Child#1
            }
        }

        runBlocking {

            job.join()
            /**
             * result:
             * Thread in thread: DefaultDispatcher-worker-1 @Parent#1
             * Thread in thread: DefaultDispatcher-worker-1 @Child#1
             */
        }
    }

    @Test
    fun coroutineNameContext() {

        // mengubah nama lewat CoroutineScope(CoroutineContext)
        // CoroutineName() method untuk merubah nama coroutine
        val scope = CoroutineScope(Dispatchers.IO + CoroutineName("test"))
        val job = scope.launch {
            println("Thread in thread: ${Thread.currentThread().name}") // nama coroutine berubah --> @test#1

            withContext(Dispatchers.IO) {
                println("Thread in thread: ${Thread.currentThread().name}") // nama coroutine berubah --> @test#1
            }
        }

        runBlocking {

            job.join()
            /**
             * result:
             * Thread in thread: DefaultDispatcher-worker-1 @test#1
             * Thread in thread: DefaultDispatcher-worker-1 @test#1
             */
        }
    }

}