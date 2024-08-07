package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
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

    /**
     * Channel Buffer Overflow
     * ● Walaupun kita sudah menggunakan buffer, ada kalanya buffer sudah penuh, dan sender tetap mengirimkan data
     * ● Dalam kasus seperti ini, kita bisa menggunakan beberapa strategy
     * ● Untuk mengatur ketika terjadi buffer overflow (kelebihan data yang ditampung oleh buffer), kita
     *    bisa menggunakan enum BufferOverflow
     *
     * BufferOverflow Enum      Keterangan
     * SUSPEND                  Block sender (suruh menunggu sampai buffer nya kosong)
     * DROP_OLDEST              Hapus data di buffer yang paling lama (paling depan)
     * DROP_LATEST              Hapus data di buffer yang paling baru (paling belakang)
     */

    @Test
    fun testChannelBufferOverflow() {

        runBlocking {

            val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_LATEST)

            // sender
            val job1 = launch {
                repeat(100) {
                    println("send data $it to channel")
                    channel.send(it)
                }
            }
            job1.join()

            // received
            val job2 = launch {
                repeat(10) {
                    println("receive data ${channel.receive()}")
                }
            }
            job2.join()

            channel.close()
        }
        /** result: BufferOverflow.DROP_OLDEST, jadi data sender akan di kirim terus tetapi karna capacity buffer hanya bisa menampung 10, jadi data yang paling lama akan di hapus. yang di terima data paling baru saja yang di kirim oleh sender
         ** result: BufferOverflow.DROP_LATEST, kebalikan dari BufferOverflow.DROP_OLDEST
         * send data 0 to channel
         * send data 1 to channel
         * send data 2 to channel
         * send data 3 to channel
         * send data 4 to channel
         * send data 5 to channel
         * send data 6 to channel
         * send data 7 to channel
         * send data 8 to channel
         * send data 9 to channel
         * send data 10 to channel
         * send data n to channel
         * receive data 90
         * receive data 91
         * receive data 92
         * receive data 93
         * receive data 94
         * receive data 95
         * receive data 96
         * receive data 97
         * receive data 98
         * receive data 99
         */
    }

    /**
     * Channel Undelivered Element
     * ● Kadang ada kasus dimana sebuah channel sudah di close, tetapi ada coroutine yang masih mencoba
     *    mengirim data ke channel
     * ● Ketika kita mencoba mengirim data ke channel yang sudah close, maka secara otomatis channel akan mengembalikan
     *    error ClosedSendChannelException
     * ● Namun pertanyaannya, bagaimana dengan data yang sudah dikirim?
     * ● Kita bisa menambah lambda function ketika membuat channel, sebagai fallback ketika sebuah data
     *    dikirim dan channel sudah di close, maka fallback tersebut akan dieksekusi
     * ● Function fallback tersebut bernama onUndeliveredElement
     */

    @Test
    fun testUndeliveredElement() {

        // method Channel<E>() adalah lambda dengan method
        // onUndeliveredElement: ((E) -> Unit)? = null


        val channel = Channel<Int>(capacity = 10) { value ->
            // lambda dari onUndeliveredElement()
            println("Undelivered Element $value")
        }
        channel.close()

        runBlocking {

            // sender
            val job = launch {
                channel.send(10)
                channel.send(100)
            }

            job.join()
        }
        /**
         * result: jadi saat mengirim sender 10 lalu sudah di tutup channel.close() line 244. akan Exception ClosedSendChannelException
         * terus saat sender mengirim 100 keburu tidak di eksekusi. jadi launch coroutine tidak bisa deliver karna sudah di tutup
         * Undelivered Element 10
         *
         * kotlinx.coroutines.channels.ClosedSendChannelException: Channel was closed
         * 	at kotlinx.coroutines.channels.BufferedChannel.getSendException(BufferedChannel.kt:1734)
         * 	at kotlinx.coroutines.channels.BufferedChannel.onClosedSend(BufferedChannel.kt:141)
         * 	at kotlinx.coroutines.channels.BufferedChannel.send$suspendImpl(BufferedChannel.kt:126)
         * 	at kotlinx.coroutines.channels.BufferedChannel.send(BufferedChannel.kt)
         * 	at com.tutorial.coroutine.ChannelTest$testUndeliveredElement$1$job$1.invokeSuspend(ChannelTest.kt:247)
         * 	at _COROUTINE._BOUNDARY._(CoroutineDebugging.kt:46)
         * 	at com.tutorial.coroutine.ChannelTest$testUndeliveredElement$1$job$1.invokeSuspend(ChannelTest.kt:247)
         * Caused by: kotlinx.coroutines.channels.ClosedSendChannelException: Channel was closed
         * 	at kotlinx.coroutines.channels.BufferedChannel.getSendException(BufferedChannel.kt:1734)
         * 	at kotlinx.coroutines.channels.BufferedChannel.onClosedSend(BufferedChannel.kt:141)
         * 	at kotlinx.coroutines.channels.BufferedChannel.send$suspendImpl(BufferedChannel.kt:126)
         * 	at kotlinx.coroutines.channels.BufferedChannel.send(BufferedChannel.kt)
         * 	at com.tutorial.coroutine.ChannelTest$testUndeliveredElement$1$job$1.invokeSuspend(ChannelTest.kt:247)
         */
    }

    /**
     * produce Function
     * ● Coroutine scope memiliki sebuah function bernama produce, ini digunakan untuk membuat
     *    sebuah coroutine yang digunakan untuk mengirim data ke channel, sederhananya kita bisa
     *    membuat channel secara mudah dengan menggunakan function produce ini
     * ● Hasil return dari produce adalah ReceiveChannel (parent interface dari Channel), yang hanya bisa
     *    digunakan untuk mengambil data
     */

    @Test
    fun testProduce() {

        val scope = CoroutineScope(Dispatchers.IO)

        // membuat channel manual
//        val channel = Channel<Int>()

        // sender
        // manual membuat coroutine untuk kirim data ke dalam channel nya
//        val job1 = scope.launch {
//            repeat(100) {
//                println("Send data $it to channel")
//                channel.send(it)
//            }
//        }

        // sender
        // membuat channel dari method produce
        val channel: ReceiveChannel<Int> = scope.produce {
            repeat(100) {
                send(it)
            }
        }

        // received
        val job2 = scope.launch {
            repeat(100) {
                println("Received data ${channel.receive()} from channel")
            }
        }

        runBlocking {
            joinAll(job2)
        }

    }

}