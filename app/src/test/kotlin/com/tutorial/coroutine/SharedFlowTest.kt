package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import java.util.Date

class SharedFlowTest {

    /**
     * Shared Flow vs Flow
     * ● Shared Flow adalah turunan dari Flow, sehingga apa yang bisa dilakukan di Flow, bisa juga
     *    dilakukan di Shared Flow
     * ● Kemampuan Shared Flow yang tidak dimiliki oleh Flow adalah, pada Shared Flow, kita bisa
     *    membuat lebih dari satu receiver
     * ● Selain itu Shared Flow bersifat aktif atau hot, yang artinya ketika kita mengirim data ke Shared
     *    Flow, data langsung dikirim ke receiver tanpa perlu di collect terlebih dahulu oleh si receiver
     *
     * Shared Flow vs Broadcast Channel
     * ● Shared Flow mulai dikenalkan di Kotlin 1.4
     * ● Shared Flow dirancang sebagai pengganti Broadcast Channel
     * ● Shared Flow adalah turunan dari Flow, sehingga mendukung semua Flow operator, hal ini yang
     *    sangat membedakan dengan Channel yang hanya bisa menggunakan receive() untuk menerima
     *    data, di Shared Flow, kita bisa melakukan operasi apapun bawaan dari Flow operator
     * ● Shared Flow mendukung configurable buffer overflow strategy karena bisa menggunakan Flow Operator
     * ● Shared Flow bukanlah channel, sehingga tidak ada operasi close
     * ● Untuk membuat receiver dari Shared Flow, kita bisa menggunakan function asSharedFlow()
     *
     * flow() --> membuat flow
     * emit() --> mengirim data flow
     * collect --> mengambil data flow
     */

    @Test
    fun testSharedFlow(){

        /**
         * note SharedFlow default tidak ada buffer, jadi ketika kita set delay di Receiver itu 2 detika, maka Sender akan terkena dampak set delay Receiver
         * maka akan terjadi ketimpangan.. jadi sender akan ikut nunggu ketika emit() ke flow, sampai semua receiver berhasil..
         * untuk menangani ini kita perlu buffer(), untuk menangani seder dan receiver. jadi buffer ada di tengah2 antara seder dan receiver. jadi sender tidaka akn menunggu receiver sampai berhasil
         */

        val scope: CoroutineScope = CoroutineScope(Dispatchers.IO) // coroutineScope IO
        val sharedFlow: MutableSharedFlow<Int> = MutableSharedFlow<Int>() // Shared Flow

        // job sender
        scope.launch {

            repeat(10){
                println("  Send 1        $it | ${Date()}")
                sharedFlow.emit(it) // emit() mengirim data flow
                delay(1000)
            }
        }

        // job receiver
        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "Received Job1=  $it - ${Date()}" }
                .collect {
                    delay(1000)
                    println(it)
                }
        }

        // job receiver
        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .map { "Received Job2=  $it - ${Date()}" }
                .collect {
                    delay(2000)
                    println(it)
                }
        }

        runBlocking {

            delay(22_000)
            scope.cancel() // cancel() membatalkan coroutine scope
        }
        /**
         * result: hasil sesuai ekpetasi, receiver 1 menerima data per 1 detik, receiver 2 menerima data per 2 detik.. tanpa sender tidak menggu receiver berhasil menerima data, dia akan tetap kirim data dan di tampung buffer dan di teruskan ke receiver
         *   Send 1        0 | Wed Feb 07 14:39:32 WIB 2024
         *   Send 1        1 | Wed Feb 07 14:39:33 WIB 2024
         *   Send 1        2 | Wed Feb 07 14:39:34 WIB 2024
         * Received Job1=  1 - Wed Feb 07 14:39:33 WIB 2024
         * Received Job2=  1 - Wed Feb 07 14:39:33 WIB 2024
         *   Send 1        3 | Wed Feb 07 14:39:35 WIB 2024
         * Received Job1=  2 - Wed Feb 07 14:39:34 WIB 2024
         *   Send 1        4 | Wed Feb 07 14:39:36 WIB 2024
         * Received Job1=  3 - Wed Feb 07 14:39:35 WIB 2024
         * Received Job2=  2 - Wed Feb 07 14:39:35 WIB 2024
         *   Send 1        5 | Wed Feb 07 14:39:37 WIB 2024
         * Received Job1=  4 - Wed Feb 07 14:39:36 WIB 2024
         *   Send 1        6 | Wed Feb 07 14:39:38 WIB 2024
         * Received Job1=  5 - Wed Feb 07 14:39:37 WIB 2024
         * Received Job2=  3 - Wed Feb 07 14:39:37 WIB 2024
         *   Send 1        7 | Wed Feb 07 14:39:39 WIB 2024
         * Received Job1=  6 - Wed Feb 07 14:39:38 WIB 2024
         *   Send 1        8 | Wed Feb 07 14:39:40 WIB 2024
         * Received Job1=  7 - Wed Feb 07 14:39:39 WIB 2024
         * Received Job2=  4 - Wed Feb 07 14:39:39 WIB 2024
         *   Send 1        9 | Wed Feb 07 14:39:41 WIB 2024
         * Received Job1=  8 - Wed Feb 07 14:39:40 WIB 2024
         * Received Job1=  9 - Wed Feb 07 14:39:41 WIB 2024
         * Received Job2=  5 - Wed Feb 07 14:39:41 WIB 2024
         * Received Job2=  6 - Wed Feb 07 14:39:43 WIB 2024
         * Received Job2=  7 - Wed Feb 07 14:39:45 WIB 2024
         * Received Job2=  8 - Wed Feb 07 14:39:47 WIB 2024
         * Received Job2=  9 - Wed Feb 07 14:39:49 WIB 2024
         */


    }


}