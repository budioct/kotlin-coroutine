package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import java.util.*

class StateFlow {

    /**
     * State Flow
     * ● State Flow adalah turunan dari Shared Flow, artinya di State Flow, kita bisa membuat banyak receiver
     * ● Pada State Flow, receiver hanya akan menerima data paling baru
     * ● Jadi jika ada receiver yang sangat lambat dan sender mengirim data terlalu cepat, yang akan
     *    diterima oleh receiver adalah data paling akhir
     * ● State Flow cocok digunakan untuk maintain state, dimana memang biasanya state itu biasanya
     *    hanya satu data, tidak peduli berapa kali perubahan data tersebut, yang paling penting pada state adalah data terakhir
     * ● Untuk mendapatkan data state nya, kita bisa menggunakan field value di State Flow
     * ● Untuk membuat receiver kita bisa menggunakan asStateFlow()
     * ● State Flow bisa dirancang sebagai pengganti Conflated Broadcast Channel
     */

    @Test
    fun testStateFlow(){

        /**
         * Note: State Flow maintance main state, state itu biasanya hanya 1 data, tidak perduli perbahanya datanya. yang paling penting adalah data terakhir (latest)
         * di state flow jangan menggunakan buffer
         * state flow akan menerima data paling baru state sebelumnya akan di replace, cocok untuk mengantikan conflieted channel
         */

        val scope: CoroutineScope = CoroutineScope(Dispatchers.IO) // coroutineScope IO
        val stateFlow: MutableStateFlow<Int> = MutableStateFlow(0) // instance Shared Flow

        // job sender
        scope.launch {

            repeat(10){
                println("  Send          $it | ${Date()}")
                stateFlow.emit(it) // emit() mengirim data flow
                delay(1000)
            }
        }

        // job receiver
//        scope.launch {
//            stateFlow.asStateFlow()
//                .map { "Received Job1=  $it - ${Date()}" }
//                .collect {
//                    delay(1000)
//                    println(it)
//                }
//        }

        // job receiver
        scope.launch {
            stateFlow.asStateFlow()
                .map { "Received Job2=  $it - ${Date()}" }
                .collect {
                    // ketika di mengambil data seperti ini supaya lebih akurat
                    println(it)
                    delay(2000)
                }
        }

        runBlocking {

            delay(22_000)
            scope.cancel() // cancel() membatalkan coroutine scope
        }
        /**
         * result: hasil received loncat loncat
         * Received Job2=  0 - Wed Feb 07 15:14:21 WIB 2024
         *   Send          0 | Wed Feb 07 15:14:21 WIB 2024
         *   Send          1 | Wed Feb 07 15:14:22 WIB 2024
         * Received Job2=  1 - Wed Feb 07 15:14:23 WIB 2024
         *   Send          2 | Wed Feb 07 15:14:23 WIB 2024
         *   Send          3 | Wed Feb 07 15:14:24 WIB 2024
         * Received Job2=  3 - Wed Feb 07 15:14:25 WIB 2024
         *   Send          4 | Wed Feb 07 15:14:25 WIB 2024
         *   Send          5 | Wed Feb 07 15:14:26 WIB 2024
         * Received Job2=  5 - Wed Feb 07 15:14:27 WIB 2024
         *   Send          6 | Wed Feb 07 15:14:27 WIB 2024
         *   Send          7 | Wed Feb 07 15:14:28 WIB 2024
         * Received Job2=  7 - Wed Feb 07 15:14:29 WIB 2024
         *   Send          8 | Wed Feb 07 15:14:29 WIB 2024
         *   Send          9 | Wed Feb 07 15:14:30 WIB 2024
         * Received Job2=  9 - Wed Feb 07 15:14:31 WIB 2024
         */

    }

}