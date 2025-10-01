# 💬 Real-Time Chat Application Backend

![Java](https://img.shields.io/badge/Java-17+-red?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?logo=postgresql)
![Socket.IO](https://img.shields.io/badge/Socket.IO-Real--Time%20Chat-black?logo=socketdotio)
![JWT](https://img.shields.io/badge/Authentication-JWT-orange?logo=jsonwebtokens)

---

## 🧠 Overview

A **real-time chat application backend** built using **Spring Boot** that enables **secure user authentication with JWT**, **email verification**, and **real-time messaging** using **Socket.IO**.  
The application follows an **event-driven architecture** for efficient message handling and uses a **custom Spring Security filter chain** for enhanced API protection.  
Data persistence is handled using **PostgreSQL** with **JPA/Hibernate** ORM.

---

## 🚀 Features

- 🔐 **JWT Authentication** using Spring Security  
- 📧 **Email Verification** via a **Custom Mail Sender**  
- 🧩 **Custom Security Filter Chain** for secured API access  
- ⚡ **Real-Time Messaging** using Socket.IO  
- 🧠 **Event-Driven Message Logic** for message delivery  
- 🗄️ **PostgreSQL Integration** with JPA/Hibernate  
- 👥 **User Registration, Login, and Verification Flow**  
- 📨 **Instant Message Send/Receive System**  
- 🔄 **Scalable Modular Architecture** for easy extension  

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Backend Framework** | Spring Boot |
| **Programming Language** | Java 17+ |
| **Security** | Spring Security + JWT |
| **Database** | PostgreSQL |
| **ORM** | Hibernate / JPA |
| **Real-Time Communication** | Socket.IO |
| **Email Service** | Java Mail Sender (SMTP) |
| **Build Tool** | Maven / Gradle |

---
## 🔑 API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------:|----------|-------------|--------------:|
| **POST** | `/api/auth/register` | Register a new user | ❌ |
| **POST** | `/api/auth/login` | Authenticate user & return JWT | ❌ |
| **GET** | `/api/auth/verify` | Verify user email via token | ❌ |
| **GET** | `/api/users` | Fetch all users | ✅ |
| **POST** | `/api/messages/send` | Send a new message | ✅ |
| **GET** | `/api/messages/{id}` | Fetch messages by user ID | ✅ |

---

## ⚡ Socket.IO Events

| Event | Description |
|------:|--------------:|
| `message:send` | Triggered when a user sends a message |
| `message:receive` | Broadcasts the message to the receiver |
| `user:connected` | Triggered when a user joins the chat |
| `user:disconnected` | Triggered when a user leaves the chat |

---

## ⚙️ Installation & Setup

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/chat-application-backend.git
cd chat-application-backend
```

### 2️⃣ Configure Environment Variables

Create a .env file or configure application.properties with the following:
```bash
DB_URL=jdbc:postgresql://localhost:5432/chatdb
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your_jwt_secret_key
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```
### 3️⃣ Run the Application

Using Maven:
```
./mvnw spring-boot:run
```


Using Gradle:
```
./gradlew bootRun
```

Server runs on 👉 http://localhost:8080


### 🧩 How It Works

1. 👤 User registers with email and password

2. 📧 Verification mail is automatically sent via the Custom Mail Sender

3. ✅ User clicks the verification link → verified successfully

4. 🔑 User logs in using /api/auth/login to get a JWT token

5. 💬 After authentication, user connects to Socket.IO for real-time messaging

6. ⚡ Messages are broadcast and handled using event-driven logic

### 🧮 Example Email Verification Flow

1️⃣ User registers via /api/auth/register.

2️⃣ A verification email like this is sent:

Subject: Verify your Chat App Account
Body: Click here
 to verify your email.

3️⃣ User clicks → account gets verified and activated 🎉

### 🔮 Future Enhancements

🧾 Message delivery & read receipts

🟢 User online/offline status tracking

👥 Group chats and media attachments

☁️ Docker deployment & CI/CD pipeline

🔔 Push notifications for new messages
