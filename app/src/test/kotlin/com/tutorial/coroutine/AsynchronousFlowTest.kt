package com.tutorial.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class AsynchronousFlowTest {

    /**
     * Asynchronous Flow
     * ● Sampai saat ini kita hanya membahas tentang coroutine yang tidak mengembalikan value (launch)
     *    dan mengembalikan satu value (async), bagaimana jika kita butuh sebuah coroutine yang
     *    mengembalikan data berkali-kali seperti layaknya collection?
     * ● Kotlin mendukung hal tersebut dengan nama Flow.
     * ● Flow mirip dengan sequence di Kotlin Collection, yang membedakan adalah flow berjalan sebagai
     *    coroutine dan kita bisa menggunakan suspend function di flow.
     * ● Flow adalah collection cold atau lazy, artinya jika tidak diminta datanya, flow tidak akan berjalan
     *   (kode nya tidak akan dieksekusi)
     *
     *   Membuat Flow
     * ● Untuk membuat flow, kita bisa menggunakan function flow()
     * ● Di dalam flow untuk mengirim data kita bisa menggunakan function emit()
     * ● Untuk mengakses data yang ada di flow, kita bisa menggunakan function collect()
     */

    @Test
    internal fun testCreateFlow() {

//        val flow = flow<Int> {
        val flow: Flow<Int> = flow {
            println("flow started")
            repeat(10) {
                delay(1000)
                emit(it) // emit() // mengirim data flow
            } // flow() //  membuat flow
        }

        runBlocking {
            flow.collect { println(it) } // collect() // mengakses data flow
        }
        /**
         * result: setiap counter akan di print setiap 1 detik
         * flow started
         * 0
         * 1
         * 2
         * 3
         * 4
         * 5
         * 6
         * 7
         * 8
         * 9
         */

    }


}