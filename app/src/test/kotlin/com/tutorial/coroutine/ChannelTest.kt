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

    /**
     * Channel Buffer
     * ● Secara default, channel hanya bisa menampung satu data, artinya jika kita mencoba mengirim data
     *    lain ke channel, maka kita harus menunggu data yang ada diambil.
     * ● Namun kita bisa menambahkan buffer di dalam channel atau istilahnya capacity. Jadi defaultnya
     *    capacity nya adalah 0 (buffer atau antrian yang bisa ditampung)
     *
     * Contoh Constant Channel Capacity
     * Constant                 Capacity            Keterangan
     * Channel.UNLIMITED        Int.MAX_VALUE       Total kapasitas buffer nya Int.MAX_VALUE atau bisa dibilang unlimited
     * Channel.RENDEZVOUS       0                   Tidak memiliki buffer
     * Channel.BUFFER           64 atau bisa di     Total kapasitas buffer nya 64 atau sesuai properties
     *                          setting via
     *                          properties
     */

    @Test
    fun testChannelUnlimited() {

        // contoh channel dengan kemampuan unlimetd / nilai max dari Int
        // jadi sender akan mengirim ke Channel dan akan di ambil oleh receiver

        runBlocking {

            //val channel = Channel<Int>(capacity = Channel.UNLIMITED) // instance Channel.. capacity adalah argument di assaigment dengan number atau enum dari Channel
            val channel = Channel<Int>(capacity = 10)

            // sender
            val job1 = launch {
                println("Send data 1 to Channel")
                channel.send(1) // send(E) mengirim data ke channel
                println("send data 2 to Channel")
                channel.send(2)
//              loop send data ke channel
//                repeat(100){
//                println("Send data $it to Channel")
//                    channel.send(it)
//                }
            }

            // receiver
            val job2 = launch {
                println("Receive first data ${channel.receive()}") // receive() get data
                println("Receive second data ${channel.receive()}")
            }

            joinAll(job1, job2)
            channel.close()
        }
        /**
         * result:
         * Send data 1 to Channel
         * send data 2 to Channel
         * Receive first data 1
         * Receive second data 2
         */

    }

    @Test
    fun testChannelConfilated() {

        // contoh channel dengan kemampuan datanya negatif 1
        // kalau kirim capacity -1, artinya semua data yang di kirim data awal awalnya (paling lama) akan di hapus, jadi yang di ambil data paling terakhir
        // Channel.CONFLATED.. saat yaitu kirim data habis itu kirim data , jadi data pertama belum sampai ke terima oleh coroutine yang lain. maka data tersebut akan di hapus

        runBlocking {

            val channel = Channel<Int>(capacity = Channel.CONFLATED)

            // sender
            val job1 = launch {
                println("Send data 1 to Channel")
                channel.send(1) // send(E) mengirim data ke channel
                println("send data 2 to Channel")
                channel.send(2)
            }
            job1.join()

            // received
            val job2 = launch {
                println("received Data ${channel.receive()}")
            }
            job2.join()

            channel.close()
        }
        /**
         * result:
         * Send data 1 to Channel
         * send data 2 to Channel
         * received Data 2
         */

    }

}