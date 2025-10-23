# DineSync

![Dine Sync](https://img.shields.io/badge/Smart%20Order-v1.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-orange)

## Overview

**Dine Sync** (Smart Order System) is designed to support in-house dining operations at restaurants, focusing on key functionalities for four types of users: waitstaff, kitchen staff, managers, and diners. Each user group is responsible for specific tasks, such as creating and managing orders, preparing dishes, and summarizing payment records.

**Home** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875541/lvpxoj4flxp0iqnlvb2t.png "Giao diện trang chủ")
**Featured Dishes** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875567/i7i5anenr1x6k3cztkmi.png "Món nổi bật")
**MENU** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875635/unshynjqeqixitx8e7a9.png "Danh sách món")
**Add to cart** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744876049/mrv2vlaz1cqqia6gb4ur.png "Thêm vào giỏ hàng")
**Cart** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875667/eku9syg3ymunn4p9xdj0.png "Giỏ hàng")
**Order successfully** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875716/d5hprttf4nsaibgaspxv.png "Đặt hàng thành công")
**Kitchen dashboard for update Order status** 
![alt text](https://res.cloudinary.com/decz34g1a/image/upload/v1744875768/fiuhsuktgt6rzn300akf.png "Kiểm tra món")

---

## System Architecture

The system includes 3 main microservices:

### Domain-1: Authentication Service

- User authentication and authorization
- User information management

### Domain-2: Order & Menu Management Service

- Menu: categories, dishes, options
- Orders, tables, carts, payments, and bills

### Kitchen-Domain: Kitchen Service

- Order processing
- Dish status and availability updates

---

## Technologies Used

### Backend

- **Spring Boot** – Java backend framework
- **Spring Security** – Authentication & authorization
- **Spring Data JPA** – ORM for DB interaction
- **PostgreSQL** – Relational database
- **RabbitMQ** – Messaging system
- **Redis** – Caching layer
- **JWT** – Token-based authentication
- **Lombok** – Boilerplate code reduction
- **Cloudinary** – Image storage

### DevOps & Infrastructure

- **Docker** – Containerization
- **Docker Compose** – Orchestration
- **Traefik** – API Gateway & reverse proxy
- **Maven** – Dependency management
- **Spring Actuator** – Monitoring

---

## Service Details

### 1. Authentication Service (Domain-1)

**Features**:
- User signup/login
- Role management: `ROLE_MANAGER`, `ROLE_WAITER`, `ROLE_KITCHEN_STAFF`
- JWT token handling
- Profile management

**Main Endpoints**:
- `/auth/signup`
- `/auth/signin`
- `/auth/refresh-token`
- `/auth/forgot-password`

**Technologies**:
- Spring Security + JWT
- BCrypt password encoding
- Role-based access control

---

### 2. Order & Menu Management Service (Domain-2)

**Features**:

**Menu Management**:
- Categories, dish info, images, options

**Order Management**:
- Orders, tables, carts, payments

**Main Endpoints**:
- `/categories/**`
- `/menuItems/**`
- `/orders/**`
- `/tables/**`
- `/carts/**`
- `/bills/**`
- `/optionChoices/**`

**Technologies**:
- Spring Data JPA + Transactions
- RabbitMQ for kitchen communication
- Cloudinary for dish images
- Menu caching

---

### 3. Kitchen Service (Kitchen-Domain)

**Features**:
- Handle incoming orders
- Manage workflow: confirm, start, complete, reject
- Update dish status & availability

**Main Endpoints**:
- `/kitchen/orders/pending`
- `/kitchen/orders/in-progress`
- `/kitchen/orders/confirmed`
- `/kitchen/orders/rejected`
- `/kitchen/orders/{orderId}/start`
- `/kitchen/orders/{orderId}/confirm`
- `/kitchen/orders/{orderId}/complete`
- `/kitchen/orders/{orderId}/reject`
- `/kitchen/menu/availability`

**Technologies**:
- RabbitMQ (consumer/publisher)
- Custom exception handling

---

## Business Processes

### 🧾 Order Workflow

**Creation**:
- Waiter creates order
- Sent to Kitchen via RabbitMQ

**Processing**:
- Chef confirms and prepares
- Updates each dish status
- Completes order

**Payment**:
- Waiter creates bill
- Customer pays
- Table marked as "AVAILABLE"

---

### 🍽 Menu Workflow

**Category Management**:
- Add/edit/delete categories
- Arrange display order

**Dish Management**:
- Add/edit/delete dish
- Price, description, image, options

**Availability**:
- Chef updates availability
- Synced via RabbitMQ

---

## Database

Each service uses its **own PostgreSQL schema**:

- **Authentication Service**: `User`, `Role`, `UserRole`
- **Order Service**: `MenuItem`, `Category`, `Order`, `OrderItem`, `Table`, `Cart`, `CartItem`, `Bill`
- **Kitchen Service**: `Order`, `OrderItem`, `MenuItem`, `Table`

---

## Inter-Service Communication

**RabbitMQ Exchanges/Queues**:
- `restaurant.exchange`: Main exchange
- `kitchen-order-queue`: Orders to kitchen
- `order.status.updates.queue`: Status updates
- Routing keys:
  - `kitchen.orders`
  - `kitchen.order.updates`

---

## Security

- JWT Authentication
- Role-based access control
- Secure endpoints
- Input validation
- Exception handling

---

## Installation & Running

### Configuration

Each service has `application.yml`:

- Database
- RabbitMQ
- Redis
- Server port & path

### Startup Steps

1. Start RabbitMQ & PostgreSQL
2. Start Authentication Service
3. Start Order & Menu Management
4. Start Kitchen Service

---

## 🐳 Docker Deployment

The whole system is **Dockerized**.

### Docker Components

#### Traefik
- API Gateway
- Dashboard: `localhost:8090`
- Routes: `/identity`, `/domain2`, `/kitchen`

#### Databases
- **db1**: Auth Service (port `5434`)
- **db2**: Order & Kitchen (port `5433`)

#### RabbitMQ
- AMQP: `5672`
- UI: `15672`

#### Redis
- Port: `6379`

#### Microservices
- `domain-1`: Port `8081`, `/identity`
- `domain-2`: Port `8082`, `/domain2`
- `kitchen-domain`: Port `8083`, `/kitchen`

---

### Volumes & Persistence

- `pgdata1`: PostgreSQL Auth
- `pgdata2`: PostgreSQL Order/Kitchen
- `rabbitmqdata`: RabbitMQ
- `redisdata`: Redis

---

### Network

All containers share `app-net` bridge for inter-service communication.

---

### Health Checks

Each service has health checks:

- PostgreSQL: readiness
- RabbitMQ: broker check
- Microservices: Spring Actuator endpoints
- Redis: ping test

---

## 🐳  Running with Docker

1. Make sure Docker & Docker Compose are installed
2. Clone the repo
3. Create or edit `.env` file
4. Run the system:

```bash
docker-compose up --build
