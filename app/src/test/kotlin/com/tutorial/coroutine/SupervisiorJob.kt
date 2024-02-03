package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class SupervisiorJob {

    /**
     * Supervisor Job (Pengawas Job)... hanya bisa di lakukan di parent
     *
     * Job
     * ● Secara default, saat kita membuat coroutine scope atau menjalankan coroutine, tipe coroutine tersebut adalah Job
     * ● Dalam Job, saat terjadi error di salah satu coroutine, maka error tersebut akan di propagate(sebarkan / eskalasi) ke parent nya
     * ● Dan secara otomatis parent akan membatalkan semua coroutine
     */

    @Test
    fun testJob(){

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + Job())

        // coroutine tidak di jalankan oleh parent Job
        val job1 = scope.launch {
            delay(2000)
            println("Job 1 Done")
        }

        // coroutine ini akan Exception lalu eskalasi ke parent Job, lalu membatalkan coroutine yang lainya
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("Job 2 Failed")
        }

        runBlocking {

            joinAll(job1, job2)
        }
        /**
         * result: hasil akan error, karena job2 kena Exception terus hasil nya di eskalasi ke parent Job lalu parent membatalkan coroutine selanjutnya atau yang lainya.. ini adalah sifat dasar job di kotlin
         * Exception in thread "pool-1-thread-3 @coroutine#2" java.lang.IllegalArgumentException: Job 2 Failed
         * 	at com.tutorial.coroutine.SupervisiorJob$testJob$job2$1.invokeSuspend(SupervisiorJob.kt:31)
         * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
         * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
         * 	at java.base/java.lang.Thread.run(Thread.java:1583)
         * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@18da28c6, java.util.concurrent.ThreadPoolExecutor@7283d3eb[Running, pool size = 4, active threads = 2, queued tasks = 0, completed tasks = 2]]
         */
    }

    @Test
    fun testSupervisiorJob(){

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + SupervisorJob()) // SupervisorJob() adalah turunan dari Job(), ketika coroutine ada yang Exception lalu eskalasi ke parent job, tetapi job tidak akan menghentikan coroutine yang lain nya

        // coroutine akan tetap di jalankan, karna SupervisiorJob()
        val job1 = scope.launch {
            delay(2000)
            println("Job 1 Done")
        }

        // coroutine ini akan Exception lalu eskalasi ke parent Job, lalu membatalkan coroutine yang lainya
        val job2 = scope.launch {
            delay(1000)
            throw IllegalArgumentException("Job 2 Failed")
        }

        runBlocking {
            joinAll(job1, job2)
        }
        /**
         * result: // SupervisorJob() adalah turunan dari Job(), ketika coroutine ada yang Exception lalu eskalasi ke parent job, tetapi job tidak akan menghentikan coroutine yang lain nya
         * Exception in thread "pool-1-thread-3 @coroutine#2" java.lang.IllegalArgumentException: Job 2 Failed
         * 	at com.tutorial.coroutine.SupervisiorJob$testSupervisiorJob$job2$1.invokeSuspend(SupervisiorJob.kt:64)
         * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
         * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
         * 	at java.base/java.lang.Thread.run(Thread.java:1583)
         * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@24deb721, java.util.concurrent.ThreadPoolExecutor@70e38ce1[Running, pool size = 3, active threads = 1, queued tasks = 0, completed tasks = 2]]
         * Job 1 Done
         */

    }

    /**
     * supervisorScope Function
     * ● Kadang ada kondisi dimana kita tidak memiliki akses untuk mengubah sebuah coroutine scope
     * ● Karena secara default sifatnya adalah Job, maka kita bisa menggunakan supervisorScope function untuk membuat
     *    coroutine yang sifatnya SupervisorJob
     */

    @Test
    fun testSupervisiorScopeFunction1(){

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + Job())

        runBlocking {
            scope.launch {
                // scope coroutine parent

                launch {
                    // coroutine tidak di jalankan oleh parent Job
                    // scope coroutine child
                    delay(2000)
                    println("Child 1 Done")
                }

                launch {
                    // coroutine ini akan Exception lalu eskalasi ke parent Job, lalu membatalkan coroutine yang lainya
                    // scope coroutine child
                    delay(1000)
                    throw IllegalArgumentException("Child 2 Error")
                }
            }

            delay(3000)
        }
        /**
         * result: jika coroutine child kena Exception di akan eskalasi ke Job Parent dan akan menghentikan coroutine yang lainya
         * Exception in thread "pool-1-thread-2 @coroutine#3" java.lang.IllegalArgumentException: Child 2 Error
         * 	at com.tutorial.coroutine.SupervisiorJob$testSupervisiorScopeFunction$1$1$2.invokeSuspend(SupervisiorJob.kt:106)
         * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
         * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
         * 	at java.base/java.lang.Thread.run(Thread.java:1583)
         * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@37eb20c9, java.util.concurrent.ThreadPoolExecutor@13df2a8c[Running, pool size = 4, active threads = 1, queued tasks = 0, completed tasks = 4]]
         */

    }

    @Test
    fun testSupervisiorScopeFunction2(){

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + Job())

        runBlocking {
            scope.launch {
                // scope coroutine parent

                supervisorScope {
                    // scope supervisiorScope
                    // SupervisorJob() adalah turunan dari Job(), ketika coroutine ada yang Exception lalu eskalasi ke parent job, tetapi job tidak akan menghentikan coroutine yang lain nya

                    launch {
                        // coroutine akan tetap di jalankan, karna SupervisiorJob()
                        // scope coroutine child
                        delay(2000)
                        println("Child 1 Done")
                    }

                    launch {
                        // coroutine ini akan Exception lalu eskalasi ke parent Job, lalu membatalkan coroutine yang lainya
                        // scope coroutine child
                        delay(1000)
                        throw IllegalArgumentException("Child 2 Error")
                    }
                }
            }

            delay(3000)
        }
        /**
         * result: penggunaan method supervisorScope() // SupervisorJob() adalah turunan dari Job(), ketika coroutine ada yang Exception lalu eskalasi ke parent job, tetapi job tidak akan menghentikan coroutine yang lain nya
         * Exception in thread "pool-1-thread-4 @coroutine#4" java.lang.IllegalArgumentException: Child 2 Error
         * 	at com.tutorial.coroutine.SupervisiorJob$testSupervisiorScopeFunction2$1$1$1$2.invokeSuspend(SupervisiorJob.kt:158)
         * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
         * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
         * 	at java.base/java.lang.Thread.run(Thread.java:1583)
         * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(4), "coroutine#4":StandaloneCoroutine{Cancelling}@7593a791, java.util.concurrent.ThreadPoolExecutor@163370c2[Running, pool size = 4, active threads = 1, queued tasks = 0, completed tasks = 3]]
         * Child 1 Done
         */

    }

    /**
     * Exception Handler di Job dan Supervisor Job
     * ● Exception handler di Job ataupun di Supervisor Job secara default akan di propagate(sebarkan / eskalasi) ke parent nya
     * ● Artinya jika kita membuat CoroutineExceptionHandler, kita harus membuatnya di parent, tidak
     *    bisa di coroutine child nya.
     * ● Jika kita menambahkan exception handler di coroutine child nya, maka itu tidak akan pernah digunakan
     *
     * note: jadi child coroutine tidak bisa handle Exception yang di lakukan hanya propagate(sebarkan / eskalasi) ke parent Job.
     * Parent Job lah yang akan mengeksekusi Exception nya
     */

    @Test
    fun testJobExceptionHandler(){

        // pembuatan Exception Handler yang salah

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Error: ${throwable.message}") // ketika Erorr ini akan di tampilkan sebagai pesan nya
        }

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher() // sebagai Thread
        val scope = CoroutineScope(dispatcher) // coroutine scope

        runBlocking {
            val job = scope.launch {
                // scope coroutine parent

                launch (exceptionHandler){ // useless atau not used.. jadi ketika error dia tidak akan di eksekusi hanya eskalasi ke parent Job nya
                    // coroutine ini akan Exception lalu eskalasi ke parent Job, lalu membatalkan coroutine yang lainya
                    // scope coroutine child
                    println("Job Child")
                    throw IllegalArgumentException("Child Error Brooo!!")
                }
            }

            job.join()
        }
        /**
         * result: error nya tetap Runtime Execption.. bukan Exception Handling nya
         * Job Child
         * Exception in thread "pool-1-thread-2 @coroutine#3" java.lang.IllegalArgumentException: Child Error Brooo!!
         * 	at com.tutorial.coroutine.SupervisiorJob$testJobExceptionHandler$1$job$1$1.invokeSuspend(SupervisiorJob.kt:212)
         * 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
         * 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
         * 	at java.base/java.lang.Thread.run(Thread.java:1583)
         * 	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@328e5330, java.util.concurrent.ThreadPoolExecutor@15f47664[Running, pool size = 2, active threads = 1, queued tasks = 0, completed tasks = 1]]
         */

    }

    /**
     * Exception Handler dengan supervisorScope
     * ● Salah satu cara agar exception handler bisa dilakukan di coroutine child adalah dengan menggunakan supervisorScope
     * ● Saat menggunakan supervisorScope, maka exception bisa di gunakan di parent coroutine di
     *    supervisorScope, atau sebenarnya coroutine child di scope yang ada diatas nya
     * ● Tapi ingan jika terjadi error di child nya coroutine yang ada di supervisorScope, maka tetap akan di propagate ke parent coroutine di cupervisorScope
     */

    @Test
    fun testSupervisorJobExceptionHandler() {

        // mengakali supervisiorJob dengan Exception Handler

        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            println("Error ${throwable.message}")
        } // ketika Erorr ini akan di tampilkan sebagai pesan nya

        val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        runBlocking {
            val job = scope.launch {
                // scope coroutine super parent

                supervisorScope {
                    // scope supervisor method
                    launch(exceptionHandler) {// use di scope supervisorScope
                        // scope coroutine parent
                        launch(exceptionHandler) { // useless atau not used
                            // coroutine ini akan Exception lalu eskalasi ke parent Job. dan Exception masuk ke Handling oleh CoroutineExceptionHandler
                            // scope coroutine child
                            println("Job Child")
                            throw IllegalArgumentException("Child Error Brooo!!")
                        }
                    }
                }
            }

            job.join()
        }
        /**
         * result: tidak akan Runtime Exception. karna sudah di rubah menjadi Exception Handling
         * Job Child
         * Error Child Error Brooo!!
         */
    }

}