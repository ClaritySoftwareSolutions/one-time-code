package uk.co.claritysoftware.onetimecode

import uk.co.claritysoftware.onetimecode.models.OneTimeCodeResponse
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

fun aOneTimeCodeResponse(
    id: UUID = UUID.randomUUID(),
    code: String = "ABCDEF",
    expiry: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
) = OneTimeCodeResponse(
    id = id,
    code = code,
    expiry = expiry
)
