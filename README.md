# Mock Banking Transaction Dashboard

A full-stack banking application built with Java, MySQL, HTML, CSS, and JavaScript.

## Features
- Secure user authentication
- Real-time transaction dashboard
- Relational database management with MySQL via JDBC
- Rule-based suspicious activity detection (flags high-value transactions)

## Tech Stack
Java · MySQL · JDBC · HTML · CSS · JavaScript · REST API

## Setup

1. Install MySQL and create the database:
```
CREATE DATABASE banking_dashboard;
USE banking_dashboard;
CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, full_name VARCHAR(100) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE transactions (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT, type VARCHAR(20) NOT NULL, amount DECIMAL(10,2) NOT NULL, description VARCHAR(255), status VARCHAR(20) DEFAULT 'normal', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id));
```

2. Download MySQL Connector/J and place the `.jar` in the `lib/` folder

3. Update `src/DatabaseConnection.java` with your MySQL credentials

4. Compile:
```
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d . src/DatabaseConnection.java src/AuthHandler.java src/TransactionHandler.java src/BankingServer.java
```

5. Run:
```
java -cp ".;lib/mysql-connector-j-9.6.0.jar" BankingServer
```

6. Open `http://localhost:8080`

## Default Login
- Username: `admin`
- Password: `admin123`
---