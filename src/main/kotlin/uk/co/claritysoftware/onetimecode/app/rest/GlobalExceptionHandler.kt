package uk.co.claritysoftware.onetimecode.app.rest

import jakarta.servlet.RequestDispatcher.ERROR_MESSAGE
import jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.GONE
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeExpiredException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeValidationNotMatchedException

/**
 * Global Exception Handler. Handles specific exceptions thrown by the application by returning a suitable [ErrorResponse]
 * response entity.
 *
 * Our standard pattern here is to return an [ErrorResponse]. Please think carefully about writing a response handler
 * method that does not follow this pattern. Please try not to use [handleExceptionInternal] as this returns a response
 * body of a simple string rather than a structured response body.
 *
 * Our preferred approach is to use the method [populateErrorResponseAndHandleExceptionInternal] which builds and returns
 * the [ErrorResponse] complete with correctly populated status code field. This method also populates the message field
 * of [ErrorResponse] from the exception. In the case that the exception message is not suitable for exposing through
 * the REST API, this can be overridden by manually setting the message on the request attribute. eg:
 *
 * ```
 *     request.setAttribute(ERROR_MESSAGE, "A simpler error message that does not expose internal detail", SCOPE_REQUEST)
 * ```
 *
 */
@ControllerAdvice
class GlobalExceptionHandler(
    private var errorAttributes: ApiRequestErrorAttributes
) : ResponseEntityExceptionHandler() {

    /**
     * Exception handler to return a 404 Not Found ErrorResponse
     */
    @ExceptionHandler(
        value = [
            OneTimeCodeNotFoundException::class,
        ]
    )
    fun handleExceptionReturnNotFoundErrorResponse(
        e: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, NOT_FOUND, request)
    }

    /**
     * Exception handler to return a 400 Bad Request ErrorResponse in response to a OneTimeCodeValidationNotMatchedException
     */
    @ExceptionHandler(
        value = [
            OneTimeCodeValidationNotMatchedException::class,
        ]
    )
    fun handleOneTimeCodeValidationNotMatchedException(
        e: OneTimeCodeValidationNotMatchedException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_MESSAGE, "One Time Code validation failed", SCOPE_REQUEST)
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    /**
     * Exception handler to return a 410 Gone ErrorResponse in response to a OneTimeCodeTooManyAttemptsException
     */
    @ExceptionHandler(
        value = [
            OneTimeCodeTooManyAttemptsException::class,
        ]
    )
    fun handleOneTimeCodeTooManyAttemptsException(
        e: OneTimeCodeTooManyAttemptsException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_MESSAGE, "One Time Code has failed validation and has been removed", SCOPE_REQUEST)
        return populateErrorResponseAndHandleExceptionInternal(e, GONE, request)
    }

    /**
     * Exception handler to return a 410 Gone ErrorResponse in response to a OneTimeCodeExpiredException
     */
    @ExceptionHandler(
        value = [
            OneTimeCodeExpiredException::class,
        ]
    )
    fun handleOneTimeCodeExpiredException(
        e: OneTimeCodeExpiredException,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_MESSAGE, "One Time Code has expired and has been removed", SCOPE_REQUEST)
        return populateErrorResponseAndHandleExceptionInternal(e, GONE, request)
    }

    /**
     * Overrides the HttpMessageNotReadableException exception handler to return a 400 Bad Request ErrorResponse
     */
    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    /**
     * Overrides the MethodArgumentNotValidException exception handler to return a 400 Bad Request ErrorResponse
     */
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    private fun populateErrorResponseAndHandleExceptionInternal(
        exception: Exception,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_STATUS_CODE, status.value(), RequestAttributes.SCOPE_REQUEST)
        val body = errorAttributes.getErrorResponse(request)
        return handleExceptionInternal(exception, body, HttpHeaders(), status, request)
    }
}
