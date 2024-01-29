package com.tutorial

import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class FutureTest {

    /**
     * Callable
     * ● Sebelumnya kita sudah tau, bahwa Thread akan mengeksekusi isi method run yang ada di interface
     *    Runnable, hanya saja masalahnya, return value dari Runnable adalah void (unit), artinya tidak mengembalikan data
     * ● Jika kita ingin mengeksekusi sebuah kode yang mengembalikan data, kita bisa menggunakan
     *    interface Callable, dimana terdapat method call dan return value nya adalah generic
     * ● Kita bisa menggunakan ExecutorService.submit(callable) untuk mengeksekusi Callable, dan hasilnya adalah sebuah Future<T>
     *
     * Future
     * ● Future merupakan return value untuk eksekusi Callable
     * ● Dengan Future, kita bisa mengecek status apakah proses telah selesai, atau bisa mendapatkan data
     *    hasil return callable, atau bahkan membatalkan proses callable yang sedang berjalan
     *
     */

    val executorService = Executors.newFixedThreadPool(10)

    fun getFoo(): Int {

        Thread.sleep(1000)
        return 10
    }

    fun getBar(): Int {

        Thread.sleep(1000)
        return 10
    }

    @Test
    fun testNonParallel() {

        val time = measureTimeMillis {
            val foo = getFoo()
            val bar = getBar()
            val total = foo + bar
            println("Total $total") // Total 20
        } // mengukur waktu milidetik
        println("Total Time: $time") // Total Time: 2016

    }

    @Test
    fun testFutureGet() {

        val time = measureTimeMillis {
            val foo = executorService.submit(Callable { getFoo() }) // <T> Future<T> submit(Callable<T> task)
            val bar = executorService.submit(Callable { getBar() })

            val total = foo.get() + bar.get() // V get() // Returns: the computed result
            println("Total is $total") // Total is 20
        }
        println("Time: $time") // Time: 1021

    }

}
