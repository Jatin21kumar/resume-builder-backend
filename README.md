# Resume Builder – Backend

Backend service for the Resume Builder application built using **Spring Boot**, **MongoDB Atlas**, and **JWT-based authentication**.

## 🚀 Live API
https://resume-builder-backend-61u0.onrender.com

## 🧩 Features
- User authentication (Register / Login)
- JWT-based security
- Resume CRUD APIs
- MongoDB Atlas integration
- Email verification (temporarily disabled in production)
- Cloudinary image upload support
- Swagger / OpenAPI documentation

## 🛠 Tech Stack
- Java 17
- Spring Boot 3
- Spring Security
- MongoDB Atlas
- JWT
- Maven
- Render (Deployment)


## ⚙️ Environment Variables

```env
MONGO_URI=<MongoDB Atlas URI>
JWT_SECRET=<secret>
JWT_EXPIRATION=604800000

MAIL_USERNAME=<brevo username>
MAIL_PASSWORD=<brevo password>
MAIL_FROM=<email>

CLOUDINARY_NAME=<cloud name>
CLOUDINARY_KEY=<key>
CLOUDINARY_SECRET=<secret>

APP_BASE_URL=https://resume-builder-backend-61u0.onrender.com

## 📦 API Documentation
Swagger UI:  

## Run Locally
git clone https://github.com/Jatin2ikumar/resume-builder-backend.git
cd resume-builder-backend
mvn spring-boot:run

## Server runs on:
http://localhost:8080
