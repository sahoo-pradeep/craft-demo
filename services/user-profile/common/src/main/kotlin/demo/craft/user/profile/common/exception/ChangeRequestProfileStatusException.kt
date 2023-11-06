package demo.craft.user.profile.common.exception

class InvalidProductStatusException(errorMessage: String) :
    UserProfileException(
        reason = "invalidParameter",
        message = "Invalid product status. Error: $errorMessage"
    )

class ProductStatusAlreadyExistsException(requestId: String) :
    UserProfileException(
        reason = "duplicate",
        message = "Product Status is already created with requestId $requestId"
    )

class ProductStatusNotFoundException(requestId: String, product: String) :
    UserProfileException(
        reason = "notFound",
        message = "Product Status not found for requestId: $requestId, product: $product ",
    )

class ProductStatusIllegalStateException(
    requestId: String,
    currentStatus: String,
    updateStatus: String
) : UserProfileException(
    reason = "internalError",
    message = "Illegal status update of product status from $currentStatus to $updateStatus. requestId: $requestId"
)