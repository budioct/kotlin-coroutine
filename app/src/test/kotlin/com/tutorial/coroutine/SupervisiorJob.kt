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

}