package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoroutineDispatcherTest {

    /**
     * Coroutine Dispatcher
     * ● Selain ada Job di dalam CoroutineContext, ada juga object CoroutineDispatcher.
     * ● CoroutineDispatcher digunakan untuk menentukan thread mana yang bertanggung jawab untuk mengeksekusi coroutine
     * ● Secara default sudah ada setting default dispatcher, namun kita bisa menggantinya jika kita mau
     *
     * Dispatchers
     * Ada object Dispatchers yang bisa kita gunakan untuk mengganti CoroutineDispatcher
     *  ● Dispatchers.Default, ini adalah default dispatcher, isinya minimal 2 thread, atau sebanyak jumlah
     *     cpu (mana yang lebih banyak). Dispatcher ini cocok untuk proses coroutine yang cpu-bound
     *  ● Dispatcher.IO, ini adalah dispatcher yang berisikan thread sesuai dengan kebutuhan, ketika butuh
     *     akan dibuat, ketika sudah tidak dibutuhkan, akan dihapus, mirip cache thread pool di executor
     *     service. Dispatcher ini akan sharing thread dengan Default dispatcher
     *  ● Dispatchers.Main, ini adalah dispatchers yang berisikan main thread UI, cocok ketika kita butuh
     *     running di thread main seperti di Java Swing, JavaFX atau Android. Untuk menggunakan ini, kita harus menambah library ui tambahan
     */

    @Test
    fun testDipatcher() {

        // menggunakan dispatcher

        println("Unit test in thread ${Thread.currentThread().name}")

        runBlocking {
            println("runBlocking in thread ${Thread.currentThread().name}")

            val job1 = GlobalScope.launch(Dispatchers.Default) {
                println("Job 1 run in Thread ${Thread.currentThread().name}")
            }
            val job2 = GlobalScope.launch(Dispatchers.IO) {
                println("Job 2 run in Thread ${Thread.currentThread().name}")
            }

            listOf(job1, job2).forEach { it.join() }
            /**
             * result:
             * Unit test in thread Test worker
             * runBlocking in thread Test worker @coroutine#1
             * Job 1 run in Thread DefaultDispatcher-worker-1 @coroutine#2
             * Job 2 run in Thread DefaultDispatcher-worker-2 @coroutine#3
             */

        }
    }

    /**
     * Unconfined vs Confined
     * Selain Default, IO dan Main, ada juga beberapa dispatchers yang lain
     * ● Dispatchers.Unconfined, ini adalah dispatcher yang tidak menunjuk thread apapun, biasanya akan
     *    melanjutkan thread di coroutine sebelumnya.
     * ● Confined (tanpa parameter), ini adalah dispatcher yang akan melanjutkan thread dari coroutine sebelumnya.
     * Apa bedanya Unconfined dan Confined, pada Unconfined, thread bisa berubah di tengah jalan jika
     * memang terdapat code yang melakukan perubahan thread
     */

    @Test
    fun testRunBlocking() {

        runBlocking {
            println("RunBlocking ${Thread.currentThread().name}")

            GlobalScope.launch(Dispatchers.Unconfined) {
                println("1 Unconfined: in thread ${Thread.currentThread().name}")
                delay(1000)
                println("2 Unconfined: in thread ${Thread.currentThread().name}")
                delay(1000)
                println("3 Unconfined: in thread ${Thread.currentThread().name}")
            }

            GlobalScope.launch {
                println("1 Confined: in thread ${Thread.currentThread().name}")
                delay(1000)
                println("2 Confined: in thread ${Thread.currentThread().name}")
                delay(1000)
                println("3 Confined: in thread ${Thread.currentThread().name}")
            }

            delay(3000)
            /**
             * result:
             * Unconfined: thread nya dynamic. bisa berubah di tengah jalan jika memang terdapat code yang melakukan perubahan
             * Confined: thread nya static
             *
             * 1 Unconfined: in thread Test worker @coroutine#2
             * 1 Confined: in thread DefaultDispatcher-worker-1 @coroutine#3
             * 2 Unconfined: in thread kotlinx.coroutines.DefaultExecutor @coroutine#2
             * 2 Confined: in thread DefaultDispatcher-worker-1 @coroutine#3
             * 3 Unconfined: in thread kotlinx.coroutines.DefaultExecutor @coroutine#2
             * 3 Confined: int thread DefaultDispatcher-worker-1 @coroutine#3
             */
        }
    }

    /**
     * Membuat Coroutine Dispatcher
     * ● Saat membuat aplikasi, kadang kita ingin flexible menentukan thread yang akan kita gunakan untuk menjalankan coroutine
     * ● Misal, kita ingin membedakan thread untuk layer web, layer http client, dan lain-lain
     * ● Oleh karena ini, membuat Coroutine Dispatcher sendiri sangat direkomendasikan.
     * ● Untuk membuat Coroutine Dispatcher secara manual, kita bisa melakukannya dengan cara menggunakan ExecutorService
     */

    @Test
    fun testCreateDispatcherWithExecutorService() {

        // instance dispatchcr dengan executor service
        val dispatcherService: ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher() // ExecutorService.asCoroutineDispatcher(): ExecutorCoroutineDispatcher // method extendsion return ExecutorCoroutineDispatcher
        val dispatcherWeb = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

        runBlocking {
            println("RunBlocking ${Thread.currentThread().name}")

            val job1 = GlobalScope.launch(dispatcherService) {
                println("1 Service dispatcherService run in thread ${Thread.currentThread().name}")
                delay(1000)
                println("2 Service dispatcherService run in thread ${Thread.currentThread().name}")
            }
            val job2 = GlobalScope.launch(dispatcherWeb) {
                println("1 Service dispatcherWeb run in thread ${Thread.currentThread().name}")
                delay(1000)
                println("2 Service dispatcherWeb run in thread ${Thread.currentThread().name}")
            }

            // eksekusi
            listOf(job1, job2).forEach { it.join() }
//            joinAll(job1, job2)

            /**
             * result: hasil thread adalah menggunakan thread pool segment coroutine
             * 1 Service dispatcherService run in thread pool-1-thread-1 @coroutine#2
             * 1 Service dispatcherWeb run in thread pool-2-thread-1 @coroutine#3
             * 2 Service dispatcherService run in thread pool-1-thread-2 @coroutine#2
             * 2 Service dispatcherWeb run in thread pool-2-thread-2 @coroutine#3
             */
        }
    }

    /**
     * withContext Function
     * ● Sebelumnya kita sudah tahu, bahwa ternyata saat kita melakukan delay(), suspend function
     *    tersebut akan di trigger di thread yang berbeda.
     * ● Bagaimana caranya jika kita ingin menjalankan code program kita dalam coroutine di thread yang
     *    berbeda dengan thread coroutine awalnya?
     * ● Untuk melakukan itu, kita bisa menggunakan function withContext()
     * ● Function withContext() sebenarnya bisa kita gunakan untuk mengganti CoroutineContext, namun
     *    karena CoroutineDispatcher adalah turunan CoroutineContext, jadi kita bisa otomatis mengganti
     *    thread yang akan digunakan di coroutine menggunakan function withContext()
     *
     * Note: CoroutineDispatcher adalah turunan CoroutineContext
     */

    @Test
    fun testWithContext() {

        val dispatcherClient: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        runBlocking {
            println("RunBlocking ${Thread.currentThread().name}")

            val job = GlobalScope.launch(Dispatchers.IO) {
                println("1. This code run in ${Thread.currentThread().name}")

                withContext(dispatcherClient) {
                    println("2. This code run in ${Thread.currentThread().name}")
                } // <T> withContext( context: CoroutineContext, block: suspend CoroutineScope.() -> T ): T // ingin menjalankan coroutine di thread yang berbeda dengan yang awal
                println("3. This code run in ${Thread.currentThread().name}")

                withContext(dispatcherClient) {
                    println("4. This code run in ${Thread.currentThread().name}")
                }

                withContext(dispatcherClient) {
                    println("5. This code run in ${Thread.currentThread().name}")
                }

                println("6. This code run in ${Thread.currentThread().name}")
            }

            job.join()
            /**
             * result:
             * 1. This code run in DefaultDispatcher-worker-1 @coroutine#2
             * 2. This code run in pool-1-thread-1 @coroutine#2
             * 3. This code run in DefaultDispatcher-worker-1 @coroutine#2
             * 4. This code run in pool-1-thread-1 @coroutine#2
             * 5. This code run in pool-1-thread-1 @coroutine#2
             * 6. This code run in DefaultDispatcher-worker-1 @coroutine#2
             */
        }

    }

}