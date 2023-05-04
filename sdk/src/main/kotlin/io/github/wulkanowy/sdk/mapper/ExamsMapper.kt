package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.scrapper.exams.Exam as ScrapperExam
import io.github.wulkanowy.sdk.hebe.models.Exam as HebeExam

@JvmName("mapScrapperExams")
internal fun List<ScrapperExam>.mapExams() = map {
    Exam(
        date = it.date.toLocalDate(),
        entryDate = it.entryDate.toLocalDate(),
        description = it.description,
        teacherSymbol = it.teacherSymbol,
        teacher = it.teacher,
        subject = it.subject,
        type = it.typeName,
    )
}

@JvmName("mapHebeExams")
internal fun List<HebeExam>.mapExams() = map {
    Exam(
        date = it.dateCreated.date,
        entryDate = it.deadline.date,
        description = it.content,
        subject = it.subject.name,
        teacher = it.creator.displayName,
        teacherSymbol = "",
        type = it.type,
    )
}
