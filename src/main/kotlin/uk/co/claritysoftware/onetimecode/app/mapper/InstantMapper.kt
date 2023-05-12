package uk.co.claritysoftware.onetimecode.app.mapper

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class InstantMapper(private val clock: Clock) {
    fun toInstant(offsetDateTime: OffsetDateTime): Instant = offsetDateTime.toInstant()

    fun toOffsetDateTime(instant: Instant): OffsetDateTime = instant.atOffset(ZoneOffset.of(clock.zone.id))
}
