package com.tutorial

import org.junit.jupiter.api.Test
import java.lang.Thread
import java.util.Date

class ThreadTest {

    /**
     * Thread
     * pembuatan thread di kotlin sama seperti Thread di java
     * perlu interface Runnable dan Callback
     * kotin punya helper function thread() untuk membuat thread lebih singkat dan mudah
     */

    @Test
    fun testThread() {

        val runnable = Runnable {
            println(Date()) // Mon Jan 29 14:31:16 WIB 2024
            Thread.sleep(2_0000) // 2 detik
            println("Hello world")
        }

        // mutli thread
        val thread = Thread(runnable) // instance worker(Runnable) to Thread
        val thread2 = Thread(runnable)

        thread.start() // start() // menjalankan thread
        Thread.sleep(3_000) // tunggu worker sampai selesai secara manual
        println(Date()) // Mon Jan 29 14:31:16 WIB 2024

        thread2.start()
        Thread.sleep(3_000)
        println(Date())

    }

}