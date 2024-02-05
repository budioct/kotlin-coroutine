package com.tutorial.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test

class AsynchronousFlowTest {

    /**
     * Asynchronous Flow
     * ● Sampai saat ini kita hanya membahas tentang coroutine yang tidak mengembalikan value (launch)
     *    dan mengembalikan satu value (async), bagaimana jika kita butuh sebuah coroutine yang
     *    mengembalikan data berkali-kali seperti layaknya collection?
     * ● Kotlin mendukung hal tersebut dengan nama Flow.
     * ● Flow mirip dengan sequence di Kotlin Collection, yang membedakan adalah flow berjalan sebagai
     *    coroutine dan kita bisa menggunakan suspend function di flow.
     * ● Flow adalah collection cold atau lazy, artinya jika tidak diminta datanya, flow tidak akan berjalan
     *   (kode nya tidak akan dieksekusi)
     *
     *   Membuat Flow
     * ● Untuk membuat flow, kita bisa menggunakan function flow()
     * ● Di dalam flow untuk mengirim data kita bisa menggunakan function emit()
     * ● Untuk mengakses data yang ada di flow, kita bisa menggunakan function collect()
     */

    @Test
    internal fun testCreateFlow() {

//        val flow = flow<Int> {
        val flow: Flow<Int> = flow {
            println("flow started")
            repeat(10) {
                delay(1000)
                emit(it) // emit() // mengirim data flow
            } // flow() //  membuat flow
        }

        runBlocking {
            flow.collect { println(it) } // collect() // mengakses data flow
        }
        /**
         * result: setiap counter akan di print setiap 1 detik
         * flow started
         * 0
         * 1
         * 2
         * 3
         * 4
         * 5
         * 6
         * 7
         * 8
         * 9
         */

    }

    /**
     * Flow Operator
     * ● Flow mirip dengan Kotlin Collection, memiliki banyak operator
     * ● Hampir semua operator yang ada di Kotlin Collection ada juga di Flow, seperti map, flatMap, filter,
     *    reduce, dan lain-lain
     * ● Yang membedakan dengan operator yang ada di Kotlin Collection adalah, operator di Flow
     *    mendukung suspend function
     */


    suspend fun numberFlow(): Flow<Int> = flow {
        repeat(100) {
            emit(it)
        }
    }

    suspend fun changeToString(number: Int): String {
        delay(100)
        return "Number $number"
    }

    @Test
    fun testFlowOperator() {

        // it di kotling adalah. object saat ini seperti this
        // filter() seperti condition
        // map() merubah return value yang berbeda
        // collect() mengakses value nya. mengambil datanya

        runBlocking {

            val flow = numberFlow() // method suspend
            flow.filter { it % 2 == 0 }
                .map { changeToString(it) }
                .collect { println(it) }
        }

        /**
         * result:
         * Number 0
         * Number 2
         * Number 4
         * Number 6
         * n ~ n 100
         */

    }

    /**
     * Flow Exception
     * ● Saat terjadi exception pada flow, di bagian operator apapun, maka flow akan berhenti, lalu
     *    exception akan di throw oleh flow
     * ● Untuk menangkap exception tersebut, kita bisa menggunakan block try-catch
     * ● Namun flow juga menyediakan operator untuk menangkap exception tersebut, nama functionnya adalah catch()
     * ● Dan untuk finally, flow juga sudah menyediakan operatornya, nama function nya adalah onCompletion()
     * ● Ingat, jika terjadi error di flow, flow akan dihentikan, jika kita ingin flow tidak berhenti saat terjadi
     *    error, pastikan kita selalu melakukan try catch di kode flow nya
     */

    @Test
    fun testFlowException() {

        // onEach() // iterasi data dan mengirim data flow
        // catch() // untuk menangkap Exception di flow
        // onCompletion() // untuk block Finally di flow
        // collect // mengakses flow

        runBlocking {

            val flow = numberFlow() // method suspend
            flow.map { check(it < 10); it }
                .onEach { println("onEach() $it") }
                .catch { println("catch() Error ${it.message}") }
                .onCompletion { println("onCompletion Done") }
                .collect()
        }
        /**
         * result:
         * onEach() 0
         * onEach() 1
         * onEach() 2
         * onEach() 3
         * onEach() 4
         * n ~ n 10
         */

    }


}