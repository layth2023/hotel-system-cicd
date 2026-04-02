# Brain404 Hotel Management System

A comprehensive hotel management REST API built with Spring Boot 3.2.5, featuring JWT authentication, booking management, payment processing, room management, reviews, notifications, and analytics.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Modules](#modules)
- [Prerequisites](#prerequisites)
- [Environment Configuration](#environment-configuration)
- [Installation](#installation)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Error Handling](#error-handling)
- [Pagination](#pagination)
- [Contributing](#contributing)
- [License](#license)

## Overview

Brain404 Hotel is a full-featured hotel management backend system that provides RESTful APIs for managing hotels, rooms, bookings, payments, reviews, and user accounts. The system implements role-based access control with JWT authentication, supports multiple payment methods, and includes real-time notification capabilities.

## Features

### Authentication & Authorization
- JWT-based authentication with access and refresh tokens
- Role-based access control (ADMIN, MANAGER, RECEPTIONIST, USER)
- Account lockout mechanism after failed login attempts
- BCrypt password encryption
- Token refresh functionality

### Hotel Management
- CRUD operations for hotels
- Star rating system (1-5)
- Amenity management (pool, spa, gym, wifi, etc.)
- Hotel search by city, country, and star rating
- Image management for hotels

### Room Management
- Room CRUD operations with room types
- Room availability tracking
- Capacity and pricing configuration
- Advanced room search with filters:
  - Hotel ID
  - Check-in/Check-out dates
  - Number of guests
  - Price range (min/max)
  - Room type name
  - Amenities (WiFi, Pool, etc.)

### Booking System
- Complete booking lifecycle management
- Status tracking (PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW)
- Guest information management
- Date range validation
- Booking cancellation with policy enforcement
- Check-in and check-out processing

### Payment Processing
- Multiple payment method support
- Payment status tracking (PENDING, PAID, REFUNDED, CANCELLED)
- Partial payment support
- Refund processing
- Payment transaction history

### Reviews & Ratings
- User review submission
- Rating system (1-5 stars)
- Review approval workflow
- Hotel response to reviews
- Average rating calculation

### Notifications
- User notification system
- Read/unread status tracking
- Automatic notifications for booking events

### Analytics & Statistics
- Hotel performance statistics
- Booking analytics
- Revenue tracking

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming Language |
| Spring Boot | 3.2.5 | Application Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Data Persistence |
| MySQL | 8.x | Production Database |
| H2 Database | 2.x | Test Database |
| JWT (jjwt) | 0.12.5 | Token Management |
| Lombok | 1.18.x | Boilerplate Reduction |
| MapStruct | 1.5.5 | Object Mapping |
| Springdoc OpenAPI | 2.5.0 | API Documentation |
| JUnit 5 | 5.x | Unit Testing |
| Mockito | 5.x | Mocking Framework |

## Architecture

The application follows a layered architecture pattern:

```
+-------------------------------------------------------------+
|                      Controllers                             |
|  (REST API endpoints, request/response handling)            |
+-------------------------------------------------------------+
|                       Services                               |
|  (Business logic, transaction management)                   |
+-------------------------------------------------------------+
|                      Repositories                            |
|  (Data access, JPA queries)                                 |
+-------------------------------------------------------------+
|                       Entities                               |
|  (Domain models, JPA entities)                              |
+-------------------------------------------------------------+
|                       Database                               |
|  (MySQL / H2)                                               |
+-------------------------------------------------------------+
```

### Cross-Cutting Concerns
- **Security**: JWT filter chain, authentication/authorization
- **Exception Handling**: Global exception handler with standardized error responses
- **Validation**: Custom validators for passwords, date ranges, etc.
- **Mapping**: DTO to Entity conversion using MapStruct

## Modules

| Module | Description |
|--------|-------------|
| **User** | User registration, profile management, role assignment |
| **Security** | JWT authentication, token generation/validation, refresh tokens |
| **Role** | Role management (ADMIN, MANAGER, RECEPTIONIST, USER) |
| **Hotel** | Hotel CRUD, amenity management, search functionality |
| **Room** | Room management, availability tracking |
| **RoomType** | Room type configuration (capacity, pricing) |
| **RoomAvailability** | Room availability scheduling |
| **Booking** | Booking lifecycle, status management, guest handling |
| **BookingGuest** | Guest information for bookings |
| **Payment** | Payment processing, status tracking |
| **PaymentTransaction** | Transaction history and records |
| **Review** | User reviews, ratings, approval workflow |
| **Notification** | User notifications, read status |
| **Amenity** | Hotel and room amenities |
| **Image** | Image management for hotels/rooms |
| **Address** | Address handling for hotels |
| **Stats** | Analytics and statistics |

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **MySQL 8.0+** (for production)
- **Git**

## Environment Configuration

Create environment variables or update `application.properties`:

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_PASSWORD` | MySQL database password | `your_secure_password` |
| `JWT_SECRET` | JWT signing key (Base64 encoded, min 256 bits) | `YWJjZGVmZ2hpamts...` |

### Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/brain404hotel
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=900000
jwt.refresh-expiration=604800000

# Server Configuration
server.port=8080
```

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/brain404hotel.git
cd brain404hotel
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE brain404hotel;
CREATE USER 'hotel_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON brain404hotel.* TO 'hotel_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Set Environment Variables

**Linux/macOS:**
```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your_base64_encoded_secret_key
```

**Windows:**
```cmd
set DB_PASSWORD=your_password
set JWT_SECRET=your_base64_encoded_secret_key
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger UI

Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

| Controller | Base Path | Description |
|------------|-----------|-------------|
| AuthController | `/api/auth` | Authentication (login, register, refresh) |
| UserController | `/api/users` | User management |
| HotelController | `/api/hotels` | Hotel CRUD and search |
| RoomController | `/api/rooms` | Room management |
| RoomTypeController | `/api/room-types` | Room type configuration |
| BookingController | `/api/bookings` | Booking management |
| PaymentController | `/api/payments` | Payment processing |
| ReviewController | `/api/reviews` | Review management |
| NotificationController | `/api/notifications` | User notifications |
| AmenityController | `/api/amenities` | Amenity management |
| RoleController | `/api/roles` | Role management |
| StatsController | `/api/stats` | Analytics endpoints |
| AddressController | `/api/addresses` | Address management |
| ImageController | `/api/images` | Image management |
| RoomAvailabilityController | `/api/room-availability` | Availability management |
| PaymentTransactionController | `/api/payment-transactions` | Transaction history |

### Authentication Endpoints

```
POST /api/auth/login          - User login
POST /api/auth/register       - User registration
POST /api/auth/refresh        - Refresh access token
POST /api/auth/logout         - User logout
```

### Sample API Requests

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user@example.com", "password": "Password123!"}'
```

**Create Hotel (Admin):**
```bash
curl -X POST http://localhost:8080/api/hotels \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Grand Hotel",
    "city": "New York",
    "country": "USA",
    "starRating": 5
  }'
```

**Search Hotels:**
```bash
curl "http://localhost:8080/api/hotels/search?city=New%20York&minStars=4&page=0&size=10"
```

**Search Rooms with Filters:**
```bash
curl "http://localhost:8080/rooms/search?hotelId=1&checkInDate=2024-06-01&checkOutDate=2024-06-05&guests=2&minPrice=100&maxPrice=300&roomTypeName=Deluxe&amenity=WiFi"
```

**Create Booking:**
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "checkInDate": "2024-06-01",
    "checkOutDate": "2024-06-05",
    "numberOfGuests": 2,
    "numberOfAdults": 2
  }'
```

## Security

### Authentication Flow

1. User submits credentials to `/api/auth/login`
2. Server validates credentials and returns JWT access token and refresh token
3. Client includes access token in `Authorization: Bearer <token>` header
4. Server validates token on each request via `JwtAuthFilter`
5. When access token expires, client uses refresh token to obtain new access token

### Public Endpoints

The following endpoints are accessible without authentication:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`
- `GET /api/hotels/**` (public hotel browsing)
- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

### Role Hierarchy

| Role | Permissions |
|------|-------------|
| ADMIN | Full system access |
| MANAGER | Hotel and staff management |
| RECEPTIONIST | Booking and guest management |
| USER | Personal bookings and reviews |

### Password Requirements

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=HotelServiceTest

# Run with coverage report
mvn test jacoco:report
```

### Test Configuration

Tests use H2 in-memory database configured in `application-test.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Test Categories

| Type | Annotation | Purpose |
|------|------------|---------|
| Unit Tests | `@ExtendWith(MockitoExtension.class)` | Service layer testing |
| Controller Tests | `@WebMvcTest` | REST endpoint testing |
| Repository Tests | `@DataJpaTest` | JPA query testing |
| Integration Tests | `@SpringBootTest` | Full context testing |

### Test Coverage

The test suite covers:
- Service layer business logic
- Controller endpoints with MockMvc
- Repository custom queries
- Entity calculations and methods
- Custom validators
- Exception handling
- Security filters

## Project Structure

```
src/
├── main/
│   ├── java/com/
│   │   ├── Address/           # Address entity and services
│   │   ├── Amenity/           # Amenity management
│   │   ├── Booking/           # Booking system
│   │   ├── BookingGuest/      # Guest information
│   │   ├── Common/            # Shared utilities
│   │   ├── Config/            # Application configuration
│   │   ├── Hotel/             # Hotel management
│   │   ├── Image/             # Image handling
│   │   ├── Notification/      # Notification system
│   │   ├── Payment/           # Payment processing
│   │   ├── PaymentTransaction/# Transaction records
│   │   ├── Review/            # Review system
│   │   ├── Role/              # Role management
│   │   ├── Room/              # Room management
│   │   ├── RoomAvailability/  # Availability tracking
│   │   ├── RoomType/          # Room type config
│   │   ├── Security/          # JWT and auth
│   │   ├── Stats/             # Analytics
│   │   ├── User/              # User management
│   │   ├── Validation/        # Custom validators
│   │   ├── ApiError.java      # Error response model
│   │   ├── GlobalExceptionHandler.java
│   │   └── Main.java          # Application entry point
│   └── resources/
│       ├── application.properties
│       └── application-test.properties
└── test/
    └── java/com/
        ├── Booking/           # Booking tests
        ├── Hotel/             # Hotel tests
        ├── Security/          # Security tests
        ├── User/              # User tests
        ├── Validation/        # Validator tests
        ├── config/            # Test configuration
        └── GlobalExceptionHandlerTest.java
```

## Error Handling

The application uses a global exception handler that returns standardized error responses.

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Hotel not found with id: 1",
  "path": "/api/hotels/1"
}
```

### HTTP Status Codes

| Status | Description | Example |
|--------|-------------|---------|
| 200 | Success | Resource retrieved |
| 201 | Created | Resource created |
| 400 | Bad Request | Validation error |
| 401 | Unauthorized | Invalid credentials |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource |
| 423 | Locked | Account locked |
| 500 | Internal Error | Server error |

### Custom Exceptions

- `UserNotFoundException`, `HotelNotFoundException`, `BookingNotFoundException`, etc.
- `UserAlreadyExistsException`, `HotelAlreadyExistsException`, etc.
- `BookingBadRequestException`, `RoleBadRequestException`, etc.
- `RefreshTokenException`
- `AccessDeniedException`

## Pagination

All list endpoints support pagination:

### Request Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | 0 | Page number (0-indexed) |
| `size` | 10 | Items per page |
| `sort` | varies | Sort field and direction |

### Response Format

```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true
}
```

### Example

```bash
GET /api/hotels?page=0&size=20&sort=name,asc
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Brain404 Hotel Management System** - Built with Spring Boot
