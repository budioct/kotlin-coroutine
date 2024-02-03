package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class ExceptionHandlingTest {

    /**
     * Exception Propagation
     * ● Secara garis besar, exception di coroutine itu ada yang di ekspose ke yang memanggil coroutine ada yang tidak.
     * ● Pada launch, exception tidak akan di ekspose ketika memanggil function join, namun pada async
     *    exception akan di expose ketika memanggil function await
     */

    @Test
    fun testExceptionLaunch() {

        runBlocking {
            val job = GlobalScope.launch {
                println("Start coroutine")
                throw IllegalArgumentException()
            }

            job.join()
            println("Fisnish")
            /**
             * result: sifat exception launch. jadi job.join tidak tahu kalau yang di eksekusi exception, jadi print Finish di eksekusi (sangat bahaya, harusnya tidak di eksekusi)
             * Start coroutine
             * Exception in thread "DefaultDispatcher-worker-1 @coroutine#2" java.lang.IllegalArgumentException
             * 	at com.tutorial.coroutine.ExceptionHandlingTest$testExceptionLaunch$1$job$1.invokeSuspend(ExceptionHandlingTest.kt:23)
             * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
             * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
             * 	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:589)
             * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:806)
             * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:710)
             * 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:697)
             * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@47abec5a, Dispatchers.Default]
             * Fisnish
             */
        }

    }

    @Test
    fun testExceptionAsync() {

        runBlocking {
            val deferred = GlobalScope.async {
                println("Start coroutine")
                throw IllegalArgumentException()
            }

            // method async() harus menangkap Exception dengan try catch
            // untuk menangani Exceptionya runtime kita gunakan try catch
            try {
                deferred.await() // bisa mendeteksi Exception.. jadi program di baris bawah nya tidak akan di eksekusi
            } catch (e: IllegalArgumentException) {
                // ketika mendeteksi Exception IllegalArgumentException, maka block ini akan di eksekusi
                println("Error")
            } finally {
                println("Fisnish")
            }
            /**
             * result: sifat exception async. jadi deferred.await() bisa mendeteksi Exception.. jadi program di baris bawah nya tidak akan di eksekusi (ini aman)
             * Start coroutine
             * Error
             * Fisnish
             */
        }

    }

    /**
     * Coroutine Exception Handler
     * ● Kadang kita ingin mengatur cara penangkapan exception di coroutine, hal ini bisa dilakukan dengan
     *    menggunakan interface CoroutineExceptionHandler
     * ● CoroutineExceptionHandler adalah turunan dari CoroutineContext.Element, sehingga kita bisa
     *    menambahkannya kedalam coroutine context
     * ● Ingat jenis CancellationException (dan turunannya) tidak akan diteruskan ke exception handler
     * ● Coroutine exception handler hanya jalan di launch, tidak jalan di async, untuk async, kita tetap
     *    harus menangkap exception nya secara manual
     */

    @Test
    internal fun testExceptionHandler() {

        // jika ingin membuat costume Exception Handler tanpa harus menggunakan try catch
        // interface CoroutineExceptionHandler adalah pewarisan dari CoroutineContext.Element sehingga bisa tambah di CoroutineContext
        // CancellationException (dan turunannya) tidak akan diteruskan ke exception handler
        // hanya jalan di method CoroutineScope.launch().. tidak bisa jalan di async()

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Message from interface CoroutineExceptionHandler ---> Ups, error with message ${throwable.message}") // Ups, error with message Ups Broo
        }

        // coroutine scope
        val scope = CoroutineScope(exceptionHandler + Dispatchers.IO)
        val job = scope.launch(exceptionHandler) {
            println("Run Job")
            throw IllegalArgumentException("Ups Broo") // throwable message
        }

        runBlocking {

            // coroutine global scope
            val job2 = GlobalScope.launch(exceptionHandler) {
                println("Start coroutine")
                throw IllegalArgumentException("Error Broo")
            }

            job.join()
            job2.join()
        }
        /**
         * result:
         * Run Job
         * Message from interface CoroutineExceptionHandler ---> Ups, error with message Ups Broo
         *
         * Start coroutine
         * Message from interface CoroutineExceptionHandler ---> Ups, error with message Error Broo
         */

    }


}