package io.github.wulkanowy.api

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.Summary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.register.StudentAndParentResponse
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.student.StudentInfo
import io.reactivex.observers.TestObserver
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ApiTest : BaseTest() {

    private var api =  Api()

    @Before fun setUp() {
        api.apply {
            logLevel = HttpLoggingInterceptor.Level.BASIC
            holdSession = false
            ssl = true
            host = "fakelog.cf"
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolId = "123456"
            studentId = "1"
            diaryId = "101"
            notifyDataChanged() // unnecessary in this case
        }
    }

    @Test fun schoolInfoTest() {
        val info = api.getSchoolInfo()
        val infoObserver = TestObserver<StudentAndParentResponse>()
        info.subscribe(infoObserver)
        infoObserver.assertComplete()

        val values = infoObserver.values()[0]

        assertEquals("Publiczny dziennik Wulkanowego nr 1 w fakelog.cf", values.schoolName)
        assertEquals("III 2017", values.diaries[0].name)
        assertEquals("Jan Kowalski", values.students[0].name)
    }

    @Test fun pupilsTest() {
        val pupils = api.getPupils()
        val pupilsObserver = TestObserver<List<Pupil>>()
        pupils.subscribe(pupilsObserver)
        pupilsObserver.assertComplete()

        val values = pupilsObserver.values()[0]

        assertEquals("Default", values[0].symbol)
        assertEquals("jan@fakelog.cf", values[0].email)
        assertEquals("Jan Kowalski", values[0].studentName)
        assertEquals("123456", values[0].schoolId)
        assertEquals("1", values[0].studentId)
        assertEquals("Publiczny dziennik Wulkanowego nr 1 w fakelog.cf", values[0].schoolName)
    }

    @Test fun semestersTest() {
        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val values = semestersObserver.values()[0]

        assertEquals("303", values[0].diaryId)
        assertEquals("III 2017", values[0].diaryName)
        assertEquals(1234567, values[0].semesterId)
        assertEquals(1, values[0].semesterNumber)

        assertEquals("303", values[0].diaryId)
        assertEquals("III 2017", values[1].diaryName)
        assertEquals(1234568, values[1].semesterId)
        assertEquals(2, values[1].semesterNumber)

        assertEquals("202", values[2].diaryId)
        assertEquals("II 2016", values[2].diaryName)

        assertEquals("101", values[4].diaryId)
        assertEquals("1A 2015", values[4].diaryName)
    }

    @Test fun attendanceTest() {
        val attendance = api.getAttendance("636648768000000000")
        val attendanceObserver = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        assertEquals(0, values[0].number)
        assertEquals("Fizyka", values[0].subject)
        assertEquals(getDate(2018, 6, 18), values[0].date)

        assertEquals(Attendance.Types.PRESENCE, values[0].type)
        assertEquals(Attendance.Types.EXCUSED_LATENESS, values[1].type)
        assertEquals(Attendance.Types.ABSENCE_UNEXCUSED, values[3].type)
        assertEquals(Attendance.Types.EXEMPTION, values[4].type)
        assertEquals(Attendance.Types.ABSENCE_EXCUSED, values[6].type)
        assertEquals(Attendance.Types.ABSENCE_FOR_SCHOOL_REASONS, values[9].type)
        assertEquals(Attendance.Types.UNEXCUSED_LATENESS, values[12].type)

        assertEquals(1, values[1].number)
    }

    @Test fun examsTest() {
        val exams = api.getExams("636630624000000000")
        val examsObserver = TestObserver<List<Exam>>()
        exams.subscribe(examsObserver)
        examsObserver.assertComplete()

        val values = examsObserver.values()[0]

        assertEquals(getDate(2018, 5, 9), values[0].date)
        assertEquals(getDate(2018, 4, 1), values[0].entryDate)
        assertEquals("Język angielski 1Ti|J1", values[0].subject)
        assertEquals("Sprawdzian", values[0].type)
        assertEquals("słownictwo(kultura)", values[0].description)
        assertEquals("Anyż Zofia [AZ]", values[0].teacher)
    }

    @Test fun homeworkTest() {
        val homework = api.getHomework("636443136000000000")
        val homeworkObserver = TestObserver<List<Homework>>()
        homework.subscribe(homeworkObserver)
        homeworkObserver.assertComplete()

        val values = homeworkObserver.values()[0]

        assertEquals(getDate(2017, 10, 23), values[0].date)
        assertEquals(getDate(2017, 10, 18), values[0].entryDate)
        assertEquals("Metodologia programowania", values[0].subject)
        assertEquals("Wszystkie instrukcje warunkowe, pętle (budowa, zasada działania, schemat blokowy)", values[0].content)
        assertEquals("Janusz Tracz", values[0].teacher)
    }

    @Test fun notesTest() {
        val notes = api.getNotes()
        val notesObserver = TestObserver<List<Note>>()
        notes.subscribe(notesObserver)
        notesObserver.assertComplete()

        val values = notesObserver.values()[0]

        assertEquals(getDate(2018, 3, 26), values[0].date)
        assertEquals("Janusz Tracz", values[0].teacher)
        assertEquals("Udział w konkursie szkolnym +20 pkt", values[0].category)
        assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", values[0].content)
    }

    @Test fun gradesTest() {
        val grades = api.getGrades(864)
        val gradesObserver = TestObserver<List<Grade>>()
        grades.subscribe(gradesObserver)
        gradesObserver.assertComplete()

        val values = gradesObserver.values()[0]

        assertEquals("Zajęcia z wychowawcą", values[0].subject)
        assertEquals("5", values[0].value)
        assertEquals("000000", values[0].color)
        assertEquals("A1", values[0].symbol)
        assertEquals("Dzień Kobiet w naszej klasie", values[0].description)
        assertEquals("1.00", values[0].weight)
        assertEquals(getDate(2017, 3, 21), values[0].date)
        assertEquals("Patryk Maciejewski", values[0].teacher)

        assertEquals("STR", values[4].symbol)
        assertEquals("", values[4].description)
    }

    @Test fun gradesSummaryTest() {
        val summary = api.getGradesSummary(864)
        val summaryObserver = TestObserver<List<Summary>>()
        summary.subscribe(summaryObserver)
        summaryObserver.assertComplete()

        val values = summaryObserver.values()[0]

        assertEquals("Zachowanie", values[0].name)
        assertEquals("bardzo dobre", values[0].predicted)
        assertEquals("bardzo dobre", values[0].final)
        assertEquals("Język polski", values[1].name)
        assertEquals("", values[1].predicted)
        assertEquals("4", values[1].final)

        assertEquals("Wiedza o społeczeństwie", values[4].name)
        assertEquals("", values[4].predicted)
        assertEquals("", values[4].final)
    }

    @Test fun teachersTest() {
        val teachers = api.getTeachers()
        val teachersObserver = TestObserver<List<Teacher>>()
        teachers.subscribe(teachersObserver)
        teachersObserver.assertComplete()

        val values = teachersObserver.values()[0]

        assertEquals("Religia", values[0].subject)
        assertEquals("Janusz Tracz [TJ]", values[0].name)
    }

    @Test fun studentInfoTest() {
        val student = api.getStudentInfo()
        val studentObserver = TestObserver<StudentInfo>()
        student.subscribe(studentObserver)
        studentObserver.assertComplete()

        val values = studentObserver.values()[0]

        assertEquals("Jan Marek Kowalski", values.student.fullName)
        assertEquals("Jan", values.student.firstName)
        assertEquals("Marek", values.student.secondName)
        assertEquals("Kowalski", values.student.surname)
        assertEquals(getDate(1970, 1, 1), values.student.birthDate)
        assertEquals("Warszawa", values.student.birthPlace)
        assertEquals("12345678900", values.student.pesel)
        assertEquals("Mężczyzna", values.student.gender)
        assertEquals("1", values.student.polishCitizenship)
        assertEquals("Nowak", values.student.familyName)
        assertEquals("Monika, Kamil", values.student.parentsNames)

        assertEquals("", values.student.address)
        assertEquals("", values.student.registeredAddress)
        assertEquals("", values.student.correspondenceAddress)

        assertEquals("", values.student.phoneNumber)
        assertEquals("-", values.student.cellPhoneNumber)
        assertEquals("jan@fakelog.cf", values.student.email)

        assertEquals("Monika Nowak", values.family[0].fullName)
        assertEquals("-", values.family[0].email)
        assertEquals("-", values.family[1].email)
    }
}