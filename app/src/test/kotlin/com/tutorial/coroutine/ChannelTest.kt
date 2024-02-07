package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.Test

class ChannelTest {

    /**
     * Channel
     * ● Channel adalah fitur di Kotlin Coroutine yang bisa digunakan untuk mentransfer aliran data dari satu tempat ke tempat lain
     * ● Channel mirip struktur data queue, dimana ada data masuk dan ada data keluar
     * ● Untuk mengirim data ke channel, kita bisa menggunakan function send() dan untuk mengambil
     *    data di channel kita bisa menggunakan function receive()
     * ● Channel itu sifatnya blocking, artinya jika tidak ada data di channel, saat kita mengambil data
     *    menggunakan receive() maka dia akan menunggu sampai ada data. Dan begitu juga ketika ada data
     *    di channel, dan tidak ada yang mengambilnya, saat kita send() data, dia akan menunggu sampai channel kosong (datanya diambil)
     * ● Untuk menutup channel, kita bisa menggunakan function close()
     */

    @Test
    fun testChannel() {

        runBlocking {

            // Channel<E>
            // jalan Queue. first in first out
            // Asynchronouse (sender akan mengirim data dan receiver akan menerima data. jika sendder tidak mengirim data maka akan di tunggu terus oleh receiver)
            val channel = Channel<Int>() // Channel<E> instance

            // sender
            val job1 = launch {
                println("Send data 1 to Channel")
                channel.send(1) // send(E) mengirim data ke channel
                println("send data 2 to Channel")
                channel.send(2)
            }

            // receiver
            val job2 = launch {
                println("Receive first data ${channel.receive()}") // receive() get data
                println("Receive second data ${channel.receive()}")
            }

            joinAll(job1, job2) // joinAll(vararg) // mengabungkan job. akan di tunggu hingga proses selesai
            channel.close() // close() untuk menuntup channel
        }
        /**
         * result:
         * Send data 1 to Channel
         * Receive first data 1
         * send data 2 to Channel
         * Receive second data 2
         */

    }

}