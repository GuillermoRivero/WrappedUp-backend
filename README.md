# WrappedUp Backend

A robust Spring Boot application that serves as the backend for the WrappedUp reading tracking platform. This service provides RESTful APIs for managing user reading habits, book tracking, and analytics.

## 🚀 Features

- 👤 User authentication and authorization with JWT
- 📚 Book management and tracking
- 📊 Reading statistics and analytics
- 🔒 Secure API endpoints
- 🗃️ MySQL database integration
- 📝 Integration with OpenLibrary API 
- ⚡ High-performance data processing

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.4.2
- **Language**: Java 23
- **Database**: MySQL
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Spring Test
- **Build Tool**: Maven
- **Container**: Docker

## 🏗️ Prerequisites

Before you begin, ensure you have the following installed:
- Java 23 or higher
- Maven 3.8+
- MySQL 8.0+
- Docker (for containerized deployment)

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WrappedUp-backend
   ```

2. **Configure MySQL**
   ```sql
   CREATE DATABASE wrappedup;
   CREATE USER 'wrappedup'@'%' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON wrappedup.* TO 'wrappedup'@'%';
   FLUSH PRIVILEGES;
   ```

3. **Configure application properties**
   Create `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/wrappedup
   spring.datasource.username=wrappedup
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000
   ```

4. **Build the application**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The server will start on port 8080.

## 🐳 Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t wrappedup-backend:latest .
   ```

2. **Run with Docker Compose**
   ```yaml
   version: '3.8'
   services:
     backend:
       image: wrappedup-backend:latest
       ports:
         - "8080:8080"
       environment:
         - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/wrappedup
         - SPRING_DATASOURCE_USERNAME=wrappedup
         - SPRING_DATASOURCE_PASSWORD=your_password
         - JWT_SECRET=your_jwt_secret_key
       depends_on:
         - db
     db:
       image: mysql:8.0
       environment:
         - MYSQL_DATABASE=wrappedup
         - MYSQL_USER=wrappedup
         - MYSQL_PASSWORD=your_password
         - MYSQL_ROOT_PASSWORD=root_password
   ```

## 📁 Project Structure

```
WrappedUp-backend/
├── src/
│   ├── main/
│   │   ├── java/com/wrappedup/backend/
│   │   │   ├── config/         # Configuration classes
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── entity/        # JPA entities
│   │   │   ├── repository/    # Data access layer
│   │   │   ├── service/       # Business logic
│   │   │   └── security/      # Security configuration
│   │   └── resources/         # Application properties
│   └── test/                  # Test classes
└── pom.xml                    # Maven configuration
```

## 🔧 Available Maven Commands

- `mvn clean install` - Clean and build the project
- `mvn test` - Run tests
- `mvn spring-boot:run` - Run the application
- `mvn package` - Create JAR file

## 🌐 Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| SPRING_DATASOURCE_URL | MySQL connection URL | Yes | - |
| SPRING_DATASOURCE_USERNAME | Database username | Yes | - |
| SPRING_DATASOURCE_PASSWORD | Database password | Yes | - |
| JWT_SECRET | JWT signing key | Yes | - |
| JWT_EXPIRATION | Token expiration (ms) | No | 86400000 |
| SERVER_PORT | Application port | No | 8080 |

## 🔒 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- CORS configuration
- HTTPS in production
- SQL injection prevention
- XSS protection

## 📚 API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🧪 Testing

The project includes:
- Unit tests
- Integration tests
- Security tests
- API tests

Run tests with:
```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 Related Projects

- [WrappedUp Frontend](../WrappedUp-frontend) - Next.js frontend application
- [WrappedUp Mobile](../WrappedUp-mobile) - Mobile application

## 🔐 Configuration Management

The application uses a secure configuration approach:

1. **Template File**
   - `application.properties.template` is committed to Git
   - Contains placeholder values and documentation
   - Serves as a reference for required properties

2. **Local Development**
   - Copy `application.properties.template` to `application.properties`
   - Fill in your local values
   - Never commit `application.properties` to Git

3. **Production Deployment**
   - Use environment variables in production
   - Configure through Kubernetes secrets
   - Never store sensitive values in Git

Example of secure configuration:
```bash
# Local development (application.properties)
jwt.secret=your_secure_secret_here
jwt.expiration=86400000

# Production (Kubernetes secret)
kubectl create secret generic jwt-secret \
  --from-literal=JWT_SECRET=$(openssl rand -base64 32) \
  --namespace wrappedup-prod
```

4. **Sensitive Information**
   The following should NEVER be committed to Git:
   - JWT secrets
   - Database credentials
   - API keys
   - Private keys
   - Production configurations 