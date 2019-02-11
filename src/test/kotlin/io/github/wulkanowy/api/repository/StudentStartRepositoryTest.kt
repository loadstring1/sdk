package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.register.RegisterTest
import io.github.wulkanowy.api.register.Semester
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentStartRepositoryTest : BaseLocalTest() {

    private val api by lazy {
        Api().apply {
            ssl = false
            loginType = Api.LoginType.STANDARD
            host = "fakelog.localhost:3000" //
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolSymbol = "123456"
            diaryId = 101
            useNewStudent = true
        }
    }

    @Test
    fun getSemesters() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        server.start(3000) //

        api.studentId = 1

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234568, items[0].semesterId)
        assertEquals(1234567, items[1].semesterId)
        assertEquals(1234566, items[2].semesterId)
        assertTrue(items.single { it.current }.current)
    }

    @Test
    fun getSemesters_graduate() {
        server.enqueue(MockResponse().setBody(RegisterTest::class.java.getResource("UczenDziennik.json").readText()))
        server.start(3000) //

        api.studentId = 2

        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val items = semestersObserver.values()[0]

        assertEquals(6, items.size)

        assertEquals(1234568, items[0].semesterId)
        assertEquals(1234568, items.single { it.current }.semesterId)
        assertEquals(1234567, items[1].semesterId)
        assertTrue(items.single { it.current }.current)
        assertTrue(items[0].current)
    }
}
