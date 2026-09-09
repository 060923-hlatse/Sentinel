# Sentinel — Hospital Security Monitor

## Overview

**Sentinel** is a cybersecurity monitoring system designed to detect and respond to suspicious activity within a hospital information system.

Hospitals handle sensitive patient and operational information, making them an important target for cyber attacks. Sentinel demonstrates how a hospital system can detect common security threats such as:

* Brute-force login attacks
* Unauthorized access
* Privilege violations
* Blocked account access attempts

The system records security events in a database so that suspicious activity can be audited and investigated.

---

## Project Goal

The goal of Sentinel is to demonstrate practical cybersecurity concepts using a Java-based REST application.

The main security flow is:

```text
Attack
  ↓
Security Event
  ↓
Detection
  ↓
Alert
  ↓
Response
```

For example:

```text
5 failed login attempts
        ↓
Brute-force detected
        ↓
HIGH severity event
        ↓
Account blocked
        ↓
Future login attempts rejected
```

---

# Features

## 1. User Authentication

Sentinel allows users to authenticate using a username and password.

Passwords are **not stored as plaintext**.

Instead, Sentinel uses **BCrypt hashing**:

```text
User password
     ↓
BCrypt
     ↓
Hashed password
     ↓
Stored in SQLite
```

During login, the supplied password is compared against the stored BCrypt hash.

---

## 2. Brute-Force Detection

Sentinel monitors failed login attempts.

If a user has **5 or more failed login attempts within 5 minutes**, Sentinel detects a possible brute-force attack.

The system then:

1. Creates a `BRUTE_FORCE_DETECTED` security event.
2. Assigns the event `HIGH` severity.
3. Blocks the user's account.
4. Prevents further login attempts.

Example:

```text
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
       ↓
BRUTE_FORCE_DETECTED
       ↓
ACCOUNT BLOCKED
```

---

## 3. Account Blocking

Blocked users cannot authenticate even when they provide the correct password.

This provides a response mechanism after a suspected brute-force attack.

The database stores the account state:

```text
blocked = 0
```

means the account is active.

```text
blocked = 1
```

means the account is blocked.

---

## 4. Role-Based Access Control

Sentinel implements **Role-Based Access Control (RBAC)**.

Users have roles such as:

* `ADMIN`
* `DOCTOR`
* `NURSE`
* `RECEPTIONIST`

Different roles have different permissions.

### Example

| Role         | Admin Area | Medical Area | Reception Area |
| ------------ | ---------- | ------------ | -------------- |
| ADMIN        | ✅          | ✅            | ✅              |
| DOCTOR       | ❌          | ✅            | ❌              |
| NURSE        | ❌          | ✅            | ❌              |
| RECEPTIONIST | ❌          | ❌            | ✅              |

For example, a doctor attempting to access:

```text
/admin/dashboard
```

receives:

```text
HTTP 403 Forbidden
```

An administrator receives:

```text
HTTP 200 OK
```

---

## 5. Unauthorized Access Detection

When a user attempts to access an area they do not have permission to use, Sentinel records an:

```text
UNAUTHORIZED_ACCESS
```

security event.

The event is assigned:

```text
severity = HIGH
```

Example:

```text
DOCTOR
  ↓
Attempts ADMIN dashboard
  ↓
Authorization denied
  ↓
HTTP 403
  ↓
UNAUTHORIZED_ACCESS
  ↓
HIGH severity
  ↓
SQLite audit log
```

---

## 6. Security Audit Trail

Security events are stored in SQLite.

Examples of recorded events include:

```text
LOGIN_FAILED
BRUTE_FORCE_DETECTED
BLOCKED_LOGIN_ATTEMPT
UNAUTHORIZED_ACCESS
```

Each event contains information such as:

* Username
* Event type
* IP address
* Timestamp
* Severity

Example:

```text
dr_smith | UNAUTHORIZED_ACCESS | 127.0.0.1 | HIGH
```

This creates an audit trail that can be used to investigate suspicious activity.

---

# Architecture

Sentinel follows a layered structure.

```text
                    REST API
                       │
                       ▼
              SentinelApplication
                       │
          ┌────────────┴────────────┐
          │                         │
          ▼                         ▼
 AuthenticationService       AuthorizationService
          │                         │
          └────────────┬────────────┘
                       │
                       ▼
                  Repositories
                  /          \
                 /            \
                ▼              ▼
        UserRepository   SecurityEventRepository
                │              │
                └──────┬───────┘
                       ▼
                    SQLite
```

---

# Separation of Concerns

Sentinel separates different responsibilities into different classes.

### `SentinelApplication`

Responsible for:

* Starting the Javalin server
* Defining REST endpoints
* Handling HTTP requests and responses

### `AuthenticationService`

Responsible for:

* Login verification
* Password verification
* Failed login detection
* Brute-force detection
* Account blocking

### `AuthorizationService`

Responsible for:

* Checking user roles
* Allowing or denying access
* Recording unauthorized access attempts

### `PasswordHasher`

Responsible for:

* BCrypt password hashing
* Password verification

### `UserRepository`

Responsible for:

* Saving users
* Finding users
* Deleting users
* Blocking accounts

### `SecurityEventRepository`

Responsible for:

* Saving security events
* Counting failed login attempts
* Finding security activity

### `Database`

Responsible for establishing the SQLite database connection.

---

# Database

Sentinel uses **SQLite** as its relational database.

The database file is:

```text
sentinel.db
```

## Users Table

```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    blocked INTEGER NOT NULL DEFAULT 0
);
```

The table stores:

| Column     | Purpose                 |
| ---------- | ----------------------- |
| `id`       | Unique user identifier  |
| `username` | User's login name       |
| `password` | BCrypt password hash    |
| `role`     | User's RBAC role        |
| `blocked`  | Account blocking status |

---

## Security Events Table

```sql
CREATE TABLE security_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    event_type TEXT NOT NULL,
    ip_address TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    severity TEXT NOT NULL
);
```

The table stores:

| Column       | Purpose                    |
| ------------ | -------------------------- |
| `id`         | Unique event identifier    |
| `username`   | User involved in the event |
| `event_type` | Type of security event     |
| `ip_address` | Source IP address          |
| `timestamp`  | Time of event              |
| `severity`   | Security severity          |

---

# REST API

Sentinel runs on:

```text
http://localhost:7000
```

## Health Check

### Request

```bash
curl http://localhost:7000/health
```

### Response

```text
Sentinel is running
```

---

# Login

## Successful Login

### Request

```bash
curl -i -X POST http://localhost:7000/login \
  -d "username=dr_smith" \
  -d "password=test123"
```

### Response

```text
HTTP/1.1 200 OK
```

```text
Login successful
```

---

## Failed Login

### Request

```bash
curl -i -X POST http://localhost:7000/login \
  -d "username=dr_smith" \
  -d "password=wrongpassword"
```

### Response

```text
HTTP/1.1 401 Unauthorized
```

```text
Login failed
```

The failed attempt is recorded in the security event database.

---

# Admin Dashboard

The admin dashboard demonstrates RBAC.

## Unauthorized User

A doctor attempting to access the admin dashboard:

```bash
curl -i "http://localhost:7000/admin/dashboard?username=dr_smith"
```

returns:

```text
HTTP/1.1 403 Forbidden
```

```text
Access denied
```

The attempt is also recorded as:

```text
UNAUTHORIZED_ACCESS
```

with:

```text
HIGH
```

severity.

---

## Authorized Admin

An administrator can access the dashboard:

```bash
curl -i "http://localhost:7000/admin/dashboard?username=admin_sarah"
```

returns:

```text
HTTP/1.1 200 OK
```

```text
Welcome to the Sentinel admin dashboard
```

---

# Project Structure

```text
Sentinel/
│
├── pom.xml
├── sentinel.db
├── README.md
│
└── src/
    ├── main/
    │   └── java/
    │       └── co/
    │           └── wethinkcode/
    │               └── sentinel/
    │                   │
    │                   ├── SentinelApplication.java
    │                   ├── CreateAdmin.java
    │                   │
    │                   ├── database/
    │                   │   ├── Database.java
    │                   │   └── DatabaseInitializer.java
    │                   │
    │                   ├── model/
    │                   │   ├── User.java
    │                   │   └── SecurityEvent.java
    │                   │
    │                   ├── repository/
    │                   │   ├── UserRepository.java
    │                   │   └── SecurityEventRepository.java
    │                   │
    │                   └── security/
    │                       ├── AuthenticationService.java
    │                       ├── AuthorizationService.java
    │                       └── PasswordHasher.java
    │
    └── test/
        └── java/
            └── co/
                └── wethinkcode/
                    └── sentinel/
                        ├── AuthenticationServiceTest.java
                        └── AuthorizationServiceTest.java
```

---

# Technologies

Sentinel is built using:

* **Java 21**
* **Maven**
* **Javalin**
* **SQLite**
* **JDBC**
* **BCrypt**
* **JUnit 5**
* **Git/GitLab**

---

# Dependencies

### Javalin

Used to create the REST API and HTTP server.

### SQLite JDBC

Used to connect the Java application to the SQLite database.

### BCrypt

Used for secure password hashing.

### JUnit

Used for automated unit testing.

---

# Running the Application

## 1. Clone the project

```bash
git clone <repository-url>
cd Sentinel
```

## 2. Compile the project

```bash
mvn compile
```

## 3. Run the tests

```bash
mvn test
```

Expected result:

```text
Tests run: 9
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## 4. Start Sentinel

```bash
mvn exec:java
```

The application runs on:

```text
http://localhost:7000
```

---

# Creating an Admin User

The project contains a temporary `CreateAdmin` class for creating an administrator with a BCrypt-hashed password.

Compile first:

```bash
mvn compile
```

Then run:

```bash
java -cp "target/classes:$HOME/.m2/repository/org/xerial/sqlite-jdbc/3.50.3.0/sqlite-jdbc-3.50.3.0.jar:$HOME/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" co.wethinkcode.sentinel.CreateAdmin
```

The password is hashed before being stored in the database.

---

# Testing the Database

Open SQLite:

```bash
sqlite3 sentinel.db
```

View users:

```sql
SELECT username, role, blocked
FROM users;
```

View security events:

```sql
SELECT username, event_type, ip_address, severity
FROM security_events;
```

View unauthorized access attempts:

```sql
SELECT username, event_type, ip_address, severity
FROM security_events
WHERE event_type = 'UNAUTHORIZED_ACCESS';
```

Exit SQLite:

```sql
.quit
```

---

# Testing Brute-Force Detection

A brute-force attack can be simulated by repeatedly sending an incorrect password.

For example:

```bash
curl -X POST http://localhost:7000/login \
  -d "username=dr_smith" \
  -d "password=wrongpassword"
```

Repeat the request until the threshold is reached.

After five failed attempts within five minutes, Sentinel detects:

```text
BRUTE_FORCE_DETECTED
```

and blocks the account.

The database can then be checked:

```sql
SELECT username, role, blocked
FROM users;
```

A blocked account will show:

```text
blocked = 1
```

---

# Testing

Sentinel uses JUnit 5 for automated testing.

Current tests cover:

### Authentication

* Correct password allows login
* Incorrect password is rejected
* Five failed attempts trigger account blocking

### Authorization

* Admin can access the admin area
* Nurse cannot access the admin area
* Doctor can access the medical area
* Receptionist can access the reception area
* Blocked users cannot access protected areas
* Unauthorized access attempts are recorded

Run all tests:

```bash
mvn test
```

---

# Security Events

Sentinel currently detects and records the following events:

| Event                   | Meaning                            | Severity |
| ----------------------- | ---------------------------------- | -------- |
| `LOGIN_FAILED`          | Incorrect login credentials        | LOW      |
| `BRUTE_FORCE_DETECTED`  | Five failed attempts detected      | HIGH     |
| `BLOCKED_LOGIN_ATTEMPT` | Blocked user attempted login       | HIGH     |
| `UNAUTHORIZED_ACCESS`   | User attempted restricted resource | HIGH     |

---

# Security Design

Sentinel uses multiple layers of security.

```text
                 SENTINEL
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
 Authentication  Authorization  Auditing
        │           │           │
        ▼           ▼           ▼
     BCrypt       RBAC       SQLite
        │           │           │
        └───────────┼───────────┘
                    ▼
              Security Response
```

This demonstrates the principle of **defence in depth**, where multiple security controls work together instead of relying on a single security mechanism.

---

# Example Security Scenario

Imagine an attacker attempts to compromise a doctor's account.

### Stage 1 — Attack

The attacker repeatedly submits incorrect passwords.

### Stage 2 — Detection

Sentinel records:

```text
LOGIN_FAILED
```

for every failed attempt.

### Stage 3 — Threshold

After five failed attempts:

```text
BRUTE_FORCE_DETECTED
```

is generated.

### Stage 4 — Response

The account is automatically blocked.

### Stage 5 — Continued Attack

If the attacker tries to log in again:

```text
BLOCKED_LOGIN_ATTEMPT
```

is recorded.

### Stage 6 — Audit

All security events remain available in the SQLite database for investigation.

---

# Current Security Limitation

The current demonstration version identifies the user accessing `/admin/dashboard` through a query parameter:

```text
/admin/dashboard?username=admin_sarah
```

This is useful for demonstrating the RBAC logic, but it is **not suitable for production authentication** because a user could change the username in the URL.

A production implementation should use an authenticated session or token.

The planned security flow is:

```text
Login
  ↓
Authentication
  ↓
Session / Token
  ↓
Authenticated Request
  ↓
Identify User
  ↓
Check Role
  ↓
Allow / Deny
```

This will prevent users from simply changing a username in the request.

---

# Future Improvements

Potential future improvements include:

* JWT or session-based authentication
* Secure logout
* Token expiration
* Password reset
* Account lockout management
* Admin security dashboard
* Real-time security alerts
* Security event filtering
* IP-based attack detection
* Rate limiting
* Docker containerisation
* GitLab CI/CD pipeline
* Security monitoring dashboard
* More comprehensive integration tests

---

# Learning Outcomes Demonstrated

Sentinel demonstrates practical knowledge of:

### Object-Oriented Programming

Classes are separated according to their responsibilities.

### Testing

JUnit tests verify authentication and authorization behaviour.

### Relational Databases

SQLite stores users and security events using relational tables and SQL queries.

### REST APIs

Javalin provides HTTP endpoints for interacting with the system.

### Cybersecurity

The system demonstrates:

* Authentication
* Authorization
* RBAC
* Password hashing
* Brute-force detection
* Account blocking
* Security monitoring
* Audit logging
* Security event severity

### Software Design

The application demonstrates:

* Separation of concerns
* Layered architecture
* Repository pattern
* Service classes
* Security boundaries

---

# Author

**Sentinel — Hospital Security Monitor**

Cybersecurity Elective Project

WeThinkCode_

---

# License

This project was created for educational purposes..
