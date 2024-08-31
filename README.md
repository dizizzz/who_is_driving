# Car Sharing Service
Welcome to the Car Sharing Service project! This project is designed to automate and enhance the management of car rentals, user management, and payment processing in a car-sharing service.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Domain Model](#domain-model)
- [User Roles](#user-roles)
- [User Actions](#user-actions)
  - [For Customer](#for-customer)
  - [For Managers](#for-managers)
- [Project Structure](#project-structure)
- [Running the Project](#running-the-project)
- [Additional Information](#additional-information)

## Technologies Used
- Spring Boot
- Spring Security
- Spring Web
- Spring Data JPA
- Maven
- Docker
-	Lombok
-	MySQL
-	Liquibase
-	Mapstruct
- Swagger
- Stripe API
- Telegram API.

## Domain Model
- User: Represents a registered user of the service with details such as email, name, password, and role.
- Role: Represents the role of a user in the system, for example, MANAGER or CUSTOMER.
- Car: Represents a car available for rent in the service, including model, brand, type, inventory, and daily fee.
- Rental: Represents a rental transaction, including rental date, return date, actual return date, car ID, and user ID.
- Payment:  Manages payment details for a rental, including payment status, type (PAYMENT or FINE), rental ID, session URL, session ID, and amount to pay.

### User Roles
1. Customer:  Can browse cars, rent them, and make payments.
2. Manager: Can manage cars, view all rentals, and manage payments.

### User Actions
#### For Customer:
| Action                                      | Description                                                                  |
|---------------------------------------------|------------------------------------------------------------------------------|
| Register and Sign in:                           | Register a new account and sign in to access the car-sharing service.                      |
| Browse and Search for Cars:               | View all available cars and search for specific cars. |
| Rent a Car:                 | Rent a car for a specific period and pay the rental fee.                   |
| Manage Rentals:                             | View current rentals. |
| Make Payments:                               | Make payments for rentals using the Stripe payment gateway.                     |

#### For Managers

| Action                                      | Description                                                                  |
|---------------------------------------------|------------------------------------------------------------------------------|
| Manage Cars:                              | Add, update, or delete cars. |
| View and Manage Rentals:                | View all rentals and manage rental statuses. |
| Manage Payments:               | View all payments.               |

## Project Structure
```plaintext
src/main/java/car/sharing
├── config
├── controller
├── dto
├── exeption
├── mapper
├── model
├── repository
├── security
├── service
└── validation

src/main/resources
├── db.changelog
 ├──changes
 └──db.changelog-master.yaml
├── application.properties
└── liquibase.properties

src/test/java/car/sharing
├── config
├── controller
├── repository
└── service

src/test/resources
├── database
└── application.properties
```

# Running the Project
1. Clone the repository to your computer.
2. Open the project in IntelliJ IDEA or another preferred IDE.
3. Use Maven to build the project.
4. Database Setup:

Open the application.properties file in the root directory of the project.
```plaintext
//Replace with your own database settings
 spring.datasource.url=jdbc:mysql://localhost:3306/database_name
 spring.datasource.username=your_name
 spring.datasource.password=your_password
 spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
Ensure that a MySQL database is created with the specified database name in the configuration file.

5. Run the application.

## Additional Information
In this section, you can find additional resources and guidance for working with the project:
### API Documentation
This project uses Swagger for API documentation. Access the documentation [here](http://ec2-52-87-202-79.compute-1.amazonaws.com/swagger-ui/index.html#/
).

**You can view the endpoints and test the application.**

### Docker
The project is Dockerized for easy deployment. Build the Docker container using the following commands:
```plaintext
 docker build -t posts-service .
 docker run -p 8081:8080 posts-service
```
## Running Tests
Ensure that the project is built and use Maven to run the tests:
```plaintext
 mvn test
```
### Payment Integration with Stripe
This project integrates with the Stripe API for payment processing. Make sure to set up a Stripe account in test mode, as real payments are not required. Use the provided test keys for the integration.

### Notifications with Telegram
The project uses the Telegram API to send notifications about new rentals, overdue rentals, and successful payments to service administrators. Ensure that your Telegram bot is set up correctly.

#### Known Issues
Ensure all sensitive information is stored in environment variables and never pushed to the GitHub repository. Be careful with port configurations, especially when running with Docker.
**I advise paying close attention to this aspect.**
