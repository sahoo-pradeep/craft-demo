rootProject.name = "craft-demo"
include("services:user-profile")
include("services:user-profile:api:client")
include("services:user-profile:api:server-stub")
include("services:user-profile:common")
include("services:user-profile:dao")
include("services:user-profile:integration")
include("services:user-profile:domain")
include("common:communication")
include("common:domain")

include("services:product-subscription")
include("services:product-subscription:api:client")
include("services:product-subscription:api:server-stub")
include("services:product-subscription:common")
include("services:product-subscription:dao")
include("services:product-subscription:domain")

include("services:quickbooks-accounting")
include("services:quickbooks-payroll")

