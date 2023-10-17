package demo.craft.user.profile.exception

import demo.craft.user.profile.common.domain.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.domain.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.common.domain.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.common.domain.exception.InvalidBusinessProfileException
import demo.craft.user.profile.common.domain.exception.UserProfileException
import kotlin.reflect.KClass
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler(
) : ResponseEntityExceptionHandler() {
    private val log = KotlinLogging.logger {}

    val exceptionMapping: Map<KClass<out java.lang.RuntimeException>, ApiErrorResponse> = mapOf(
        BusinessProfileNotFoundException::class to ApiErrorResponse(
            status = HttpStatus.NOT_FOUND,
            body = ErrorMessage("NOT_FOUND", "Business profile could not be found")
        ),

        BusinessProfileAlreadyExistsException::class to ApiErrorResponse(
            status = HttpStatus.CONFLICT,
            body = ErrorMessage("DUPLICATE", "Business profile is already exists")
        ),

        InvalidBusinessProfileException::class to ApiErrorResponse(
            status = HttpStatus.CONFLICT,
            body = ErrorMessage("INVALID", "The request has invalid value")
        ),

        BusinessProfileUpdateAlreadyInProgressException::class to ApiErrorResponse(
            status = HttpStatus.CONFLICT,
            body = ErrorMessage("DUPLICATE", "Business profile update request is already in progress")
        ),
    )

    @ExceptionHandler
    protected fun handleException(e: RuntimeException, request: WebRequest): ResponseEntity<Any?>? {
        val apiErrorResponse = exceptionMapping[e::class]?.let {
            when (e) {
                is UserProfileException -> it.copy(body = it.body.copy(additionalInfo = e.additionalInfo))
                else -> it
            }
        }
            ?: ApiErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                body = ErrorMessage("INTERNAL_SERVER_ERROR", "Request failed due to an internal server error")
            )

        if (apiErrorResponse.status == HttpStatus.INTERNAL_SERVER_ERROR) {
            // print stack trace in case of internal server error
            log.error(e) { "Internal server error: ${e.message}" }
        } else {
            log.error { e.message }
        }

        return handleExceptionInternal(e, apiErrorResponse.body, HttpHeaders(), apiErrorResponse.status, request)
    }

    data class ApiErrorResponse(
        val status: HttpStatus,
        val body: ErrorMessage,
    )

    data class ErrorMessage(
        val errorCode: String? = null,
        val errorMessage: String? = null,
        val additionalInfo: Any? = null
    )
}
