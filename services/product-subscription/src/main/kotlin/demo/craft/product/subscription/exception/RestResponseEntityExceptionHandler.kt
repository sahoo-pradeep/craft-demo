package demo.craft.product.subscription.exception

import demo.craft.product.subscription.common.exception.ProductSubscriptionAlreadyExistsException
import demo.craft.product.subscription.common.exception.ProductSubscriptionNotFoundException
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
        ProductSubscriptionNotFoundException::class to ApiErrorResponse(
            status = HttpStatus.NOT_FOUND,
            body = ErrorMessage("NOT_FOUND", "Product subscription could not be found")
        ),

        ProductSubscriptionAlreadyExistsException::class to ApiErrorResponse(
            status = HttpStatus.CONFLICT,
            body = ErrorMessage("DUPLICATE", "Product subscription already exists")
        ),
    )

    @ExceptionHandler
    protected fun handleException(e: RuntimeException, request: WebRequest): ResponseEntity<Any?>? {
        val apiErrorResponse = exceptionMapping[e::class]
            ?: ApiErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                body = ErrorMessage(
                    "INTERNAL_SERVER_ERROR",
                    "Request failed due to an internal server error"
                )
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
    )
}
