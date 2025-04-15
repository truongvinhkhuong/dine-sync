# Smart Order

![Smart Order System](https://img.shields.io/badge/Smart%20Order-v1.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-orange)

## Overview

Smart Order System is designed to support in-house dining operations at the restaurant, focusing on key functionalities for four types of users: waitstaff, kitchen staff, managers, and diners. Each user group is responsible for specific tasks, such as creating and managing orders, preparing dishes, and summarizing payment records.

This subsystem is limited to handling dine-in orders only and operates independently from other modules within the restaurant management system.

## System Architecture

The system includes 3 main microservices:

1. **Domain-1 (Authentication Service)**
   - User authentication and authorization management
   - User information management

2. **Domain-2 (Order & Menu Management Service)**
   - Menu management: categories, dishes, options
   - Order management
   - Table management
   - Cart management
   - Payment and bill processing

3. **Kitchen-Domain (Kitchen Service)**
   - Order processing in kitchen
   - Food item status management
   - Dish availability updates

## Technologies Used

- **Spring Boot, Spring Security, Spring Data JPA**
- **PostgreSQL** for persistent storage
- **RabbitMQ** for inter-service communication
- **JWT** for authentication
- **Redis** for caching
- **Cloudinary** for image storage
- **Maven** for dependency management
- **Docker** for containerization
- **Docker Compose** for container orchestration  
- **Traefik** as API Gateway and reverse proxy

### Backend
- **Spring Boot**: Java application development framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database interaction
- **PostgreSQL**: Relational database
- **RabbitMQ**: Messaging system for inter-service communication
- **Redis**: Caching for performance optimization
- **JWT**: Token-based authentication
- **Lombok**: Reducing boilerplate code
- **Cloudinary**: Image storage and management

### Tools and CI/CD
- **Maven**: Dependency management and build
- **Spring Actuator**: Application monitoring and management

## Service Details

### 1. Authentication Service (Domain-1)

**Features:**
- User registration and login
- User role management (ROLE_MANAGER, ROLE_WAITER, ROLE_KITCHEN_STAFF)
- JWT token generation and validation
- User profile management

**Main Endpoints:**
- `/auth/signup`: Register new users
- `/auth/signin`: Login
- `/auth/refresh-token`: Refresh token
- `/auth/forgot-password`: Handle password recovery

**Special Technologies:**
- Spring Security with JWT authentication
- Password encoding with BCrypt
- Role-based access control

### 2. Order & Menu Management Service (Domain-2)

**Features:**

*Menu Management:*
- Food category management
- Dish management with details (price, description, images)
- Dish option management (e.g., size, ingredient options)

*Order Management:*
- Order creation and management
- Table management and status
- Cart processing
- Bill creation and payment

**Main Endpoints:**
- `/categories/**`: Category management
- `/menuItems/**`: Dish management
- `/orders/**`: Order management
- `/tables/**`: Table management
- `/carts/**`: Cart management
- `/bills/**`: Bill management
- `/optionChoices/**`: Dish option management

**Special Technologies:**
- Spring Data JPA and Transaction management
- RabbitMQ for sending order information to Kitchen Service
- Cloudinary for dish image management
- Menu caching

### 3. Kitchen Service (Kitchen-Domain)

**Features:**
- Receiving and processing orders from Order Service
- Managing kitchen order workflow (confirm, start, complete, reject)
- Updating order and dish statuses
- Managing dish availability

**Main Endpoints:**
- `/kitchen/orders/pending`: View pending orders
- `/kitchen/orders/in-progress`: View in-progress orders
- `/kitchen/orders/confirmed`: View confirmed orders
- `/kitchen/orders/rejected`: View rejected orders
- `/kitchen/orders/{orderId}/start`: Start processing an order
- `/kitchen/orders/{orderId}/confirm`: Confirm an order
- `/kitchen/orders/{orderId}/complete`: Complete an order
- `/kitchen/orders/{orderId}/reject`: Reject an order
- `/kitchen/menu/availability`: Update dish availability

**Special Technologies:**
- RabbitMQ consumer for receiving orders
- RabbitMQ publisher for status update notifications
- Custom exception handling

## Business Processes

### Order and Order Processing Workflow

1. **Order Creation**
   - Waiter creates an order for a table
   - System records the order and sends information to Kitchen Service via RabbitMQ

2. **Kitchen Order Processing**
   - Chef receives and confirms the order
   - Chef starts processing the order
   - Updates status of each dish in the order
   - Completes the order

3. **Payment**
   - Waiter creates a bill for the table
   - Customer pays
   - System updates table status to "AVAILABLE"

### Menu Management Workflow

1. **Category Management**
   - Add/edit/delete food categories
   - Arrange display categories

2. **Dish Management**
   - Add/edit/delete dish information
   - Update price, description, images
   - Add options for dishes

3. **Availability Management**
   - Chef updates dish availability
   - Information is synchronized between services via RabbitMQ

## Database

Each service uses its own PostgreSQL database with a schema corresponding to its domain. Main entities include:

- **Authentication Service**: User, Role, UserRole
- **Order Service**: MenuItem, Category, Order, OrderItem, Table, Cart, CartItem, Bill
- **Kitchen Service**: Order, OrderItem, MenuItem, Table

## Inter-Service Communication

- **RabbitMQ Exchanges and Queues**
  - `restaurant.exchange`: Main exchange for the system
  - `kitchen-order-queue`: Queue for orders sent to the kitchen
  - `order.status.updates.queue`: Queue for order status updates
  - `kitchen.orders`: Routing key for orders to kitchen
  - `kitchen.order.updates`: Routing key for updates from kitchen

## Security

- JWT Authentication
- Role-based access control
- Secure endpoints
- Input data validation
- Exception handling

## Installation and Running

### Requirements
- Java 17+
- PostgreSQL 12+
- RabbitMQ
- Redis (optional)

### Configuration

Each service has its own `application.yml` configuration file with parameters such as:
- Database connection
- RabbitMQ configuration
- Redis configuration
- Server port and context path

### Starting the System

1. Start RabbitMQ and PostgreSQL
2. Start Authentication Service
3. Start Order & Menu Management Service
4. Start Kitchen Service

## Docker Deployment

The entire system is containerized using Docker for easy deployment and management. The docker-compose.yml defines the complete infrastructure.

### Docker Components

- **Traefik**: API Gateway and load balancer
  - Routes traffic to appropriate microservices
  - Exposes dashboard on port 8090
  - Handles path-based routing (e.g., `/identity`, `/domain2`, `/kitchen`)

- **Database Services**:
  - `db1`: PostgreSQL instance for Authentication Service (domain-1)
    - Port: 5434:5432
    - Database: midterm-domain1
  - `db2`: PostgreSQL instance for Order Service and Kitchen Service
    - Port: 5433:5432
    - Database: midterm-domain2

- **Message Broker**:
  - `rabbitmq`: RabbitMQ with management console
    - AMQP Port: 5672
    - Management UI: 15672

- **Cache Service**:
  - `redis`: Redis cache server
    - Port: 6379

- **Microservices**:
  - `domain-1`: Authentication Service
    - Port: 8081
    - Context path: `/identity`
  - `domain-2`: Order & Menu Management Service
    - Port: 8082
    - Context path: `/domain2`
  - `kitchen-domain`: Kitchen Service
    - Port: 8083
    - Context path: `/kitchen`

### Volumes and Persistence

All stateful services use Docker volumes for data persistence:
- `pgdata1`: PostgreSQL data for Authentication Service
- `pgdata2`: PostgreSQL data for Order and Kitchen Services
- `rabbitmqdata`: RabbitMQ data and messages
- `redisdata`: Redis cache data

### Network Configuration

All services are connected through the `app-net` bridge network, allowing seamless communication between containers.

### Health Checks

Each service includes health checks to ensure proper startup sequence and monitoring:
- Database services check PostgreSQL readiness
- RabbitMQ checks broker availability
- Microservices check Spring Boot actuator health endpoints
- Redis checks server responsiveness

### Running with Docker

1. Make sure Docker and Docker Compose are installed on your system
2. Clone the repository
3. Create or modify the `.env` file with required environment variables
4. Run the system.

