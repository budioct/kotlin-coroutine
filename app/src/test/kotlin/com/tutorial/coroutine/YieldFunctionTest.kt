package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class YieldFunctionTest {

    /**
     *
     */

    suspend fun runJob(number: Int) {

        // di thread pool atau (Dispatcher ke coroutine yang lain)

        println("Start job $number in thread ${Thread.currentThread().name}")
        yield() // memberi kesempatakn kepada coroutine yang lain untuk di eksekusi cukup sama dispatcher(Thread-pool) yang sekarang
        println("End job $number in thread ${Thread.currentThread().name}")
    }

    @Test
    fun testYieldFunction() {

        // kita akan test kalau Coroutine itu berjalan Concurrency jalan secara (sequential) bergantian dalam satu waktu
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        runBlocking {
            scope.launch { runJob(1) }
            scope.launch { runJob(2) }

            delay(2000)
        }
        /**
         * result: ini tanpa method yield() di suspend function
         * Start job 1 in thread pool-1-thread-1 @coroutine#2
         * End job 1 in thread pool-1-thread-1 @coroutine#2
         * Start job 2 in thread pool-1-thread-1 @coroutine#3
         * End job 2 in thread pool-1-thread-1 @coroutine#3
         *
         * dengan method yield()
         * Start job 1 in thread pool-1-thread-1 @coroutine#2
         * Start job 2 in thread pool-1-thread-1 @coroutine#3
         * End job 1 in thread pool-1-thread-1 @coroutine#2
         * End job 2 in thread pool-1-thread-1 @coroutine#3
         */

    }

}