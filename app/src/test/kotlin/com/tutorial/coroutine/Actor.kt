package com.tutorial.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class Actor {

    /**
     * Actor
     * ● Saat kita menggunakan produce() function, kita membuat coroutine sekaligus sebagai channel sender nya
     * ● Untuk membuat coroutine sekaligus channel receiver, kita bisa menggunakan actor() function
     * ● Konsep seperti dikenal dengan konsep Actor Model
     *
     * note: jadi di dalam Actor adalah mailBox(Channel) dan receiver(coroutine)
     * mekanisme Actor: Sender akan kirim ke Actor, di dalam Actor  [MailBox akan received channel buffer lalu otomatis di terima Receiver berupa coroutine]
     */

    @Test
    fun testActor(){

        val scope = CoroutineScope(Dispatchers.IO)

        // membuat actor dari CoroutineScope.actor: SendChannel<Int>
        // SendChannel untuk mengirim data nya ke mailbox(Channel)
        val sendChannel: SendChannel<Int> = scope.actor<Int>(capacity = 10) {

            repeat(10) {
                println("Actor received data ${receive()}") // received dari mailbox(channel) berupa coroutine
            }
        }

        val job = scope.launch {

            repeat(10) {
                sendChannel.send(it) // send() kirim data ke actor->mailbox
            }
        }

        runBlocking {

            job.join()
        }
        /**
         * result:
         * Actor received data 0
         * Actor received data 1
         * Actor received data 2
         * Actor received data 3
         * Actor received data 4
         * Actor received data 5
         * Actor received data 6
         * Actor received data 7
         * Actor received data 8
         * Actor received data 9
         */

    }

}