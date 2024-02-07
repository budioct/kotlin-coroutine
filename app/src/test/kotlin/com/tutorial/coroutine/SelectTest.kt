package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test

class SelectTest {

    /**
     * select Function
     * ● select Function memungkinkan kita untuk menunggu beberapa suspending function dan memilih yang pertama datanya tersedia
     * ● select Function bisa digunakan di Deferred dan juga Channel
     * ● Untuk Deffered, kita bisa menggunakan onAwait()
     * ● dan untuk ReceiveChannel, kita bisa menggunakan onReceive()
     */

    @Test
    fun testSelectDeferred(){

        // kita akan mencari return value yang paling cepat dari asynch()

        val scope = CoroutineScope(Dispatchers.IO)

        val deferred1: Deferred<Int> = scope.async {
            delay(1000)
            1000
        }

        val deferred2 = scope.async {
            delay(2000)
            2000
        }

        val deferred3 = scope.async {
            delay(500)
            500
        }

        val job = scope.launch {

            // select() mencari return value paling cepat dari async(): Deferred<E>
            val win = select<String> {
                deferred1.onAwait { "Result $it" } // onAwait() untuk equals result deferred
                deferred2.onAwait { "Result $it" }
                deferred3.onAwait { "Result $it" }
            }

            println("Yang Menang adalah: $win")
        }

        runBlocking {

            job.join()
        }
        /**
         * result:
         * Yang Menang adalah: Result 500
         */
    }

    @Test
    fun testSelectChannel(){

        // kita akan mencari return value yang paling cepat dari produce()

        val scope = CoroutineScope(Dispatchers.IO)

        val receiveChannel1 = scope.produce {
            delay(1000)
            send(1000) // send() mengirim data ke channel
        }

        val receiveChannel2 = scope.produce {
            delay(2000)
            send(2000)
        }

        val receiveChannel3 = scope.produce {
            delay(500)
            send(500)
        }

        val job = scope.launch {

            // select() mencari return value paling cepat dari async(): Deferred<E>
            val win = select<String> {
                receiveChannel1.onReceive { "Result $it" } // onReceive() untuk equals result receiveChannel
                receiveChannel2.onReceive { "Result $it" }
                receiveChannel3.onReceive { "Result $it" }
            }

            println("Yang Menang adalah: $win")
        }

        runBlocking {

            job.join()
        }
        /**
         * result:
         * Yang Menang adalah: Result 500
         */
    }

    @Test
    fun testSelectChannelAndDeffered(){

        // gabungan antara deffered dan receiveChannel

        val scope = CoroutineScope(Dispatchers.IO)

        val receiveChannel1 = scope.produce {
            delay(1000)
            send(1000) // send() mengirim data ke channel
        }

        val deferred1 = scope.async {
            delay(2000)
            2000
        }

        val deferred2 = scope.async {
            delay(500)
            500
        }

        val job = scope.launch {

            // select() mencari return value paling cepat dari async(): Deferred<E>
            val win = select<String> {
                receiveChannel1.onReceive { "Result $it" } // onReceive() untuk equals result receiveChannel
                deferred1.onAwait { "Result $it" }
                deferred2.onAwait { "Result $it" }
            }

            println("Yang Menang adalah: $win")
        }

        runBlocking {

            job.join()
        }
        /**
         * result:
         * Yang Menang adalah: Result 500
         */
    }

}