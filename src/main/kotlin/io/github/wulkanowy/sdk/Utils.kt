package io.github.wulkanowy.sdk

import io.github.wulkanowy.api.toFormat
import org.threeten.bp.*
import java.sql.Timestamp
import java.util.*

fun Long.toLocalDate(): LocalDate = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this * 1000L)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

fun Date.toLocalDateTime(): LocalDateTime = DateTimeUtils.toLocalDateTime(Timestamp(time))


fun LocalDate.toFormat() = toFormat("yyyy-MM-dd")

fun LocalDateTime.toFormat() = toFormat("yyyy-MM-dd")

fun String.normalizeRecipient() = substringBeforeLast("-").substringBefore(" [").substringBeforeLast(" (").trim()