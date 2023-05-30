package uk.co.claritysoftware.onetimecode.app.rest.exception

import java.util.UUID

class OneTimeCodeNotFoundException(val domainId: UUID) : RuntimeException("One Time Code not found")
