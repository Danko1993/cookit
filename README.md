# CookIt Project

## Overview
The **CookIt** project is a Spring Boot application designed to manage meal planning, ingredient tracking, and user accounts.

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

## Key Components
- Spring Boot Starters: Provides essential components for building web applications, including MVC support, security, and data access.
- PostgreSQL Driver: Enables the application to connect to a PostgreSQL database for data persistence.
- Spring Data JPA: Simplifies database access and allows you to manage your data using repositories.
- Spring Boot Starter Mail: Facilitates sending emails through a mail service (like SendGrid).
- Lombok: Reduces boilerplate code in Java classes by generating getters, setters, and other common methods at compile time.
- JWT (JSON Web Tokens): Provides a way to secure APIs by creating and validating tokens for user authentication.
- MapStruct: A code generator for mapping between Java beans, allowing for easy conversion between DTOs and entity classes.
- JUnit: A testing framework for unit testing your application components.
- H2 Database: An in-memory database for development and testing purposes.

### Installation
-  Clone the repository:
   ```bash
   git clone https://github.com/yourusername/cookit.git
- Navigate to project directory
  ```bash
  cd cookit
- Install dependencies
  ```bash
  mvn install
- Run the project
  ```bash
  mvn spring-boot:run

## Configuration 

### Database Configuration
Make sure to configure your PostgreSQL database connection properties in the application.properties file:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/Cookit
spring.datasource.username=your_username
spring.datasource.password=your_password
```
### Mail Configuration
If You use SendGrid:
```
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=your_sendgrid_api_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
Note: The SendGrid API key is set as the email password (spring.mail.password). Ensure its security!
If You use other SMTP provider make sure Your configuration is correct.

### Other properties 
```
jwt.secret=your_jwt_secret
```
Important: It is recommended to set jwt.secret to a long, unique key in production environments.

## Author
- Daniel Kosk - [Danko1993](https://github.com/Danko1993)
