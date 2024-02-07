package com.tutorial.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.Date

class TickerTest {

    /**
     * ticker Function
     * ● ticker adalah function yang bisa kita gunakan untuk membuat channel mirip dengan timer
     * ● Dengan ticker, kita bisa menentukan sebuah pesan akan dikirim dalam waktu timer yang sudah kita tentukan
     * ● Ini cocok jika kita ingin membuat timer menggunakan coroutine dan channel
     * ● Return value dari ticker function adalah ReceiveChannel<Unit>, dan setiap kita receive data,
     *    datanya hanya berupa data null
     *
     * note: untuk membuat menggunakan GlobalScope
     */

    @Test
    fun testTicker() {

        // ticker(): ReceiveChannel<Unit>
        // ticker() serupa dengan CoroutineScope.produce(): ReceiveChannel<E>. tanpa harus instance channel atau bikin CoroutineScope
        // ticker() // adalah method GlobalScope yang langsung membuat Channel dengan return value ReceiveChannel<E>
        val receiveChannel: ReceiveChannel<Unit> = ticker(delayMillis = 1000) // instance ticker() dengan param delay milis

        runBlocking {
            val job = launch {
                repeat(10) {
                    receiveChannel.receive() // receive() menerima data dari Channel
                    println("Data $it ${Date()}")
                }
            }

            job.join() // join() menunggu proses job hingga selesai
        }
        /**
         * result:
         * Data 0 Wed Feb 07 13:53:22 WIB 2024
         * Data 1 Wed Feb 07 13:53:23 WIB 2024
         * Data 2 Wed Feb 07 13:53:24 WIB 2024
         * Data 3 Wed Feb 07 13:53:25 WIB 2024
         * Data 4 Wed Feb 07 13:53:26 WIB 2024
         * Data 5 Wed Feb 07 13:53:27 WIB 2024
         * Data 6 Wed Feb 07 13:53:28 WIB 2024
         * Data 7 Wed Feb 07 13:53:29 WIB 2024
         * Data 8 Wed Feb 07 13:53:30 WIB 2024
         * Data 9 Wed Feb 07 13:53:31 WIB 2024
         */

    }

    @Test
    fun testTimer() {

        // ini adalah jika tidak menggunakan ticker (MANUAL)
        val receiveChannel = GlobalScope.produce<String?> {
            while (true){
                delay(1000)
                send(null)
            }
        }

        runBlocking {

            val job = launch {
                repeat(10) {
                    receiveChannel.receive() // receive() menerima data dari Channel
                    println("Data $it ${Date()}")
                }
            }

            job.join() // join() menunggu proses job hingga selesai
        }
        /**
         * result:
         * Data 0 Wed Feb 07 13:55:59 WIB 2024
         * Data 1 Wed Feb 07 13:56:00 WIB 2024
         * Data 2 Wed Feb 07 13:56:01 WIB 2024
         * Data 3 Wed Feb 07 13:56:02 WIB 2024
         * Data 4 Wed Feb 07 13:56:03 WIB 2024
         * Data 5 Wed Feb 07 13:56:04 WIB 2024
         * Data 6 Wed Feb 07 13:56:05 WIB 2024
         * Data 7 Wed Feb 07 13:56:06 WIB 2024
         * Data 8 Wed Feb 07 13:56:07 WIB 2024
         * Data 9 Wed Feb 07 13:56:08 WIB 2024
         */

    }

}