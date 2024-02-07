package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import org.junit.jupiter.api.Test

class BroadcastChannelTest {

    /**
     * Broadcast Channel
     * ● Secara default channel hanya boleh memiliki 1 receiver
     * ● Namun Kotlin Coroutine mendukung Broadcast Channel, ini adalah channel khusus yang receiver nya bisa lebih dari satu
     * ● Setiap kita mengirim data ke channel ini, secara otomatis semua receiver bisa mendapatkan data tersebut
     * ● BroadcastChannel memiliki function openSubscription() untuk membuat ReceiveChannel baru
     * ● Broadcast channel tidak mendukung kapasitas buffer 0 dan UNLIMITED
     */

    @Test
    fun testBroadcastChannel() {

        // instance dengan kapasitas buffer channel 10
        val broadcastChannel = BroadcastChannel<Int>(capacity = 10)

        // penerima broadcast
        val receiveChannel1: ReceiveChannel<Int> = broadcastChannel.openSubscription() // openSubscription // akan return ReceiveChannel<Int> serupa dengan method CoroutineScope.produce(): ReceiveChannel<E>
        val receiveChannel2 = broadcastChannel.openSubscription()

        // coroutine
        val scope = CoroutineScope(Dispatchers.IO)

        // sender
        val jobSender = scope.launch {
            repeat(10) {
                broadcastChannel.send(it) // send mengirim data ke Channel
            }
        }

        // receiver 1 broadcast
        val job1 = scope.launch {
            repeat(10) {
                println("receive job 1 : ${receiveChannel1.receive()}") // receive() mengambil data dari Channel
            }
        }

        // receiver 2 broadcast
        val job2 = scope.launch {
            repeat(10) {
                println("receive job 2 : ${receiveChannel2.receive()}") // receive() mengambil data dari Channel
            }
        }

        runBlocking {

            joinAll(job1, job2, jobSender)
        }
        /**
         * result:
         * receive job 1 : 0
         * receive job 2 : 0
         * receive job 1 : 1
         * receive job 2 : 1
         * receive job 1 : 2
         * receive job 2 : 2
         * receive job 1 : 3
         * receive job 2 : 3
         * receive job 1 : 4
         * receive job 2 : 4
         * receive job 1 : 5
         * receive job 2 : 5
         * receive job 1 : 6
         * receive job 2 : 6
         * receive job 1 : 7
         * receive job 2 : 7
         * receive job 1 : 8
         * receive job 1 : 9
         * receive job 2 : 8
         * receive job 2 : 9
         */

    }

    /**
     * broadcast Function
     * ● Sama seperti produce function, untuk membuat broadcast channel secara langsung dengan
     *    coroutine nya, kita bisa menggunakan function broadcast di coroutine scope
     * ● Hasil dari broadcast function adalah BroadcastChannel
     */

    @Test
    fun testBroadcastFunction() {

        // scope coroutine
        val scope = CoroutineScope(Dispatchers.IO)

        // membuat broadcastchannel manual
//        val broadcastChannel = BroadcastChannel<Int>(capacity = 10)
//        val jobSend = scope.launch {
//            repeat(10) {
//                broadcastChannel.send(it)
//            }
//        }

        // membuat broadcastchannel dengan method CoroutineScope.broadcast: BroadcastChannel<E>
        val broadcastChannel = scope.broadcast<Int>(capacity = 10) {
            repeat(10) {
                send(it) // send() mengirim data ke Channel
            }
        }

        // penerima broadcast
        val receiveChannel1: ReceiveChannel<Int> = broadcastChannel.openSubscription() // openSubscription // akan return ReceiveChannel<Int> serupa dengan method CoroutineScope.produce(): ReceiveChannel<E>
        val receiveChannel2 = broadcastChannel.openSubscription()

        // receiver 1 broadcast
        val job1 = scope.launch {
            repeat(10) {
                println("receive job 1 : ${receiveChannel1.receive()}") // receive() mengambil data dari Channel
            }
        }

        // receiver 2 broadcast
        val job2 = scope.launch {
            repeat(10) {
                println("receive job 2 : ${receiveChannel2.receive()}") // receive() mengambil data dari Channel
            }
        }

        runBlocking {

            joinAll(job1, job2)
        }
        /**
         * result:
         * receive job 1 : 0
         * receive job 1 : 1
         * receive job 1 : 2
         * receive job 1 : 3
         * receive job 1 : 4
         * receive job 1 : 5
         * receive job 1 : 6
         * receive job 1 : 7
         * receive job 1 : 8
         * receive job 1 : 9
         * receive job 2 : 0
         * receive job 2 : 1
         * receive job 2 : 2
         * receive job 2 : 3
         * receive job 2 : 4
         * receive job 2 : 5
         * receive job 2 : 6
         * receive job 2 : 7
         * receive job 2 : 8
         * receive job 2 : 9
         */

    }

}