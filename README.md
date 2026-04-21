# 📡 Telecom Billing System

## 📖 Description

Telecom Billing System is a backend application that simulates how a telecom operator processes user activity and charges for services.

The system supports:

* Customer registration
* SIM card management
* Tariff activation and change
* Usage processing (SMS, Calls, Internet)
* Billing calculation and balance deduction

---

## 🏗 Architecture

The project follows a layered architecture:

```
Controller → UseCase → Service → Repository → Database
```

### Layers:

* **Controller** – handles HTTP requests
* **UseCase** – orchestrates business flows
* **Service** – contains business logic
* **Repository** – database access (Spring Data JPA)
* **Entity** – domain models

---

## 📦 Project Structure

```
telecom_system.com
├── controller
├── entity
├── exception
├── repository
├── request
├── service
├── usecase

```

---

## ⚙️ Installation
### 1. Clone the repository

```
git clone https://github.com/your-username/telecom-system.git
```

### 2. Open the project

Open in IntelliJ IDEA or any Java IDE

### 3. Configure database

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/telecom
    username: your_user
    password: your_password
```

### 4. Run the application

```
mvn spring-boot:run
```

---

## 🚀 Usage

### 📌 Process Usage

**Endpoint:**

```
POST /usage/process
```

**Request:**

```json
{
  "customerId": 1,
  "type": "INTERNET",
  "amount": 500,
  "requestId": "abc-123"
}
```

---

## 💸 Billing Logic

Usage is divided into:

* Free usage (included in tariff)
* Paid usage (charged by global rate)

### Example:

```
Available: 5GB
Used: 7GB

→ 5GB free
→ 2GB paid
```

---

## 🔁 Usage Lifecycle

```
NEW → PROCESSED
NEW → FAILED
```

This allows:

* Retry processing
* Error tracking
* System stability

---

## 🛡 Idempotency

Each request contains a `requestId`.

If the same request is sent multiple times:
→ it will be processed only once

---

## 📊 Technologies

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Lombok

---

## 🤝 Contributing

Contributions are welcome!

Steps:

1. Fork the repository
2. Create a new branch
3. Make changes
4. Submit a pull request





## 📬 Contact

For questions or support, contact:

* Email: [your-email@example.com](mailto:your-email@example.com)
* GitHub: https://github.com/your-username
