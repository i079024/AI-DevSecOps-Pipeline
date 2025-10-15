# AI DevSecOps Pipeline - Spring Boot Application

A sample Spring Boot application with Java backend and JavaScript frontend demonstrating a complete full-stack web application with user management functionality.

## 🚀 Features

- **Backend (Java Spring Boot)**:
  - RESTful API with CRUD operations
  - JPA/Hibernate with H2 database
  - Input validation
  - Error handling
  - Actuator endpoints for monitoring

- **Frontend (JavaScript)**:
  - Modern vanilla JavaScript ES6+
  - Responsive design with CSS Grid/Flexbox
  - AJAX API calls with Fetch API
  - Form validation
  - Dynamic table rendering

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation
- Spring Boot Actuator

### Frontend
- HTML5
- CSS3 (Grid, Flexbox, Animations)
- Vanilla JavaScript (ES6+)
- Fetch API for HTTP requests

### Build Tool
- Maven 3.6+

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## 🚦 Getting Started

### 1. Clone the repository
```bash
git clone <repository-url>
cd AI-DevSecOps-Pipeline
```

### 2. Build the application
```bash
mvn clean compile
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the application
- **Web Interface**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

### 5. H2 Database Console
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## 🌐 API Endpoints

### Users API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Example API Usage

#### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

#### Get All Users
```bash
curl http://localhost:8080/api/users
```

## 🧪 Running Tests

```bash
mvn test
```

## 📦 Building for Production

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory.

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/example/aidevops/
│   │   ├── AiDevSecOpsApplication.java     # Main application class
│   │   ├── controller/
│   │   │   ├── HomeController.java         # Web controller
│   │   │   └── UserController.java         # REST API controller
│   │   ├── model/
│   │   │   └── User.java                   # User entity
│   │   ├── repository/
│   │   │   └── UserRepository.java         # Data access layer
│   │   └── service/
│   │       └── UserService.java            # Business logic layer
│   └── resources/
│       ├── static/
│       │   ├── index.html                  # Main HTML page
│       │   ├── css/style.css               # Stylesheets
│       │   └── js/app.js                   # JavaScript application
│       └── config.properties               # Application configuration
└── test/
    └── java/com/example/aidevops/
        ├── AiDevSecOpsApplicationTests.java
        └── controller/
            └── UserControllerTest.java
```

## 🔧 Configuration

The application uses H2 in-memory database by default. Configuration can be modified in `src/main/resources/config.properties`.

Key configurations:
- Server port: 8080
- Database: H2 in-memory
- JPA: Auto-create schema
- H2 Console: Enabled for development

## 🚀 Deployment

### Docker (Optional)
You can containerize this application:

```dockerfile
FROM openjdk:17-jre-slim
COPY target/ai-devsecops-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Build and run:
```bash
docker build -t ai-devsecops-app .
docker run -p 8080:8080 ai-devsecops-app
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 🆘 Support

For support, please open an issue in the GitHub repository or contact the development team.

---

**Happy Coding! 🎉**