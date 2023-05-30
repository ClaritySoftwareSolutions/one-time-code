package uk.co.claritysoftware.onetimecode.app.rest.models

fun adValidateOneTimeCodeRequest(
    code: String = "ABCDEF"
) = ValidateOneTimeCodeRequest(
    code = code
)
