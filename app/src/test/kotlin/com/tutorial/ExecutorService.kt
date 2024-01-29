package com.tutorial

import org.junit.jupiter.api.Test
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExecutorService {

    /**
     * Executor Service
     *   ExecutorService adalah sebuah interface, untuk membuat objectnya, kita bisa menggunakan class
     *   Executors, terdapat banyak helper method di class Executors.
     *
     *   Executors Method
     * Method                        Keterangan
     * newSingleThreadExecutor       Membuat ExecutorService dengan 1 thread
     * newFixedThreadPool(int)       Membuat ExecutorService dengan n thread
     * newCachedThreadPool()         Membuat ExecutorService dengan thread akan meningkat sesuai kebutuhan
     *
     * Threadpool
     * ● Implementasi ExecutorService yang terdapat di class Executors adalah class ThreadPoolExecutor
     * ● Di dalam ThreadPool, terdapat data queue (antrian) tempat menyimpan semua proses sebelum di eksekusi oleh Thread yang tersedia di ThreadPool
     * ● Hal ini jadi kita bisa mengeksekusi sebanyak-banyaknya Runnable walaupun Thread tidak cukup untuk mengeksekusi semua Runnable
     * ● Runnable yang tidak dieksekusi akan menunggu di queue sampai Thread sudah selesai mengeksekusi Runnable yang lain
     */

    @Test
    fun testExecutorsServiceSingleThread(){

        // // Executors implement dari kontrak ExecutorService
        val executor: ExecutorService = Executors.newSingleThreadExecutor() // newSingleThreadExecutor()
        (1..10).forEach {
            executor.execute{
                Thread.sleep(1_000)
                println("Done $it ${Date()} in ${Thread.currentThread().name}")
            } // execute(Runnable)
        }
        Thread.sleep(11_000) // menunggu sampai proses Thread selesai
        println("Done Program ${Date()}")
    }

    @Test
    fun testExecutorsServicenewFixedThreadPool(){

        // // Executors implement dari kontrak ExecutorService
        val executor: ExecutorService = Executors.newFixedThreadPool(5) // newFixedThreadPool()
        (1..100).forEach {
            executor.execute{
                Thread.sleep(500)
                println("Done $it ${Date()} in ${Thread.currentThread().name}")
            } // execute(Runnable)
        }
        Thread.sleep(11_000) // menunggu sampai proses Thread selesai
        println("Done Program ${Date()}")
    }

    @Test
    fun testExecutorsServicenewCachedThreadPool(){

        // // Executors implement dari kontrak ExecutorService
        val executor: ExecutorService = Executors.newCachedThreadPool() // newCachedThreadPool() // Thread yang di eksekusi langsung mengikuti sebesar counter
        (1..1_000).forEach {
            executor.execute{
                Thread.sleep(500)
                println("Done $it ${Date()} in ${Thread.currentThread().name}")
            } // execute(Runnable)
        }
        Thread.sleep(5_000) // menunggu sampai proses Thread selesai
        println("Done Program ${Date()}")
    }


}