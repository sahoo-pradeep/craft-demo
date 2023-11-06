# User Profile Service

Project: Building a User Profile service which can serve the creation and updation of common Business Profile of a user across multiple products. This service can be extended to include other common user profiles as well.

## Terminologies
1. **Business Profile**: A business profile is composed of company name and address, legal name and address, pan, ein, email, website etc.

## Modules
This is a mono repo and modular project, which means all the services are part of the same repository for the purpose of demonstration. But, different modules are created basis separation of concern and reusability. In real projects, separate repository can be created.

1. **Common**: It contains independent modules which can be used across multiple services.
    1. **cache**: It contains a generic cache interface with Redis implementation.
    2. **communication**: It contains Kafka Publisher
    3. **domain**: It should have all the common domains used across different products. Such as Product Name
    4. **lock**: It contains LockManager to provide exclusive access to a shared resource, such as Database. Postgres Lock is implemented.

2. **Services**
    1. **user-profile**: This is the primary service implemented as part of this project. It serves the purpose of managing business profile of the user
        1. **api**: Open API specs
            1. **client**: generated feign interface for the clients
            2. **server-stub**: generated controllers interface
        2. **common**: common code to be used across modules
        3. **dao**: interacts with database and redis
        4. **domain**: user profile domain models
        5. **integration**: integration with other services
    2. **product-subscription**: manage user-product subscriptions
    3. **quickbooks-accounting**: product service
    4. **quickbooks-payroll**: product service
