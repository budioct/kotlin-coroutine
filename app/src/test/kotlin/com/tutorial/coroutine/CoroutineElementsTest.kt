package com.tutorial.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoroutineElementsTest {

    /**
     * Menggabungkan Context Element
     * ● Seperti yang pernah dibahas di materi CoroutineContext
     * ● CoroutineContext adalah kompulan dari Element-Element, contoh turunannya adalah Job,
     *    CoroutineDispatcher dan yang terakhir yang sempat kita bahas adalah CoroutineName
     * ● CoroutineContext memiliki method plus, sehingga sebenarnya kita bisa menggabungkan beberapa
     *    context element secara sekaligus, misal Dispatcher dan CoroutineName misalnya
     */

    @Test
    fun testCoroutineElements() {

        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val scope =  CoroutineScope(Dispatchers.IO + CoroutineName("test")) // mengabungkan Context Element dengan operator

        // mengabungkan CoroutineContext dengan cukup tambahkan operator '+'
        val job = scope.launch(CoroutineName("parent" + dispatcher)) {
            println("Parent run in thread ${Thread.currentThread().name}")
            withContext(CoroutineName("child") + Dispatchers.IO) {
                println("Child run in thread ${Thread.currentThread().name}")
            }
        }

        runBlocking {
            job.join()
        }

        /**
         * result:
         * Parent run in thread DefaultDispatcher-worker-1 @parentjava.util.concurrent.Executors$AutoShutdownDelegatedExecutorService@11bb571c#1
         * Child run in thread DefaultDispatcher-worker-1 @child#1
         */

    }

}