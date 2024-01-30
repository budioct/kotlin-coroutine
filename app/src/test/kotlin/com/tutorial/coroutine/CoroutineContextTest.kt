package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

class CoroutineContextTest {

    /**
     * Coroutine Context
     * ● Coroutine selalu berjalan dibarengi dengan object CoroutineContext
     * ● CoroutineContext adalah sebuah kumpulan data CoroutineContext.Element, yang paling utama
     *    contohnya adalah Job (turunan dari CoroutineContext.Element) dan CoroutineDispatcher (yang akan dibahas di materi tersendiri)
     */

    @ExperimentalStdlibApi
    @Test
    fun testAccessCoroutineContext(){

        runBlocking {
            val job = GlobalScope.launch {
                val context: CoroutineContext = coroutineContext

                println(context)
                println(context[Job])
                println(context[CoroutineDispatcher]) // akan error jika tidak menambah annotation: @ExperimentalStdlibApi
                /**
                 * result:
                 * [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Active}@56291cac, Dispatchers.Default]
                 * "coroutine#2":StandaloneCoroutine{Active}@56291cac
                 * Dispatchers.Default
                 */
            }

            job.join()
        }

    }

}