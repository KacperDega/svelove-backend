# Svelove Backend

<p align="center">
  <img src="docs/svelove-logo.svg" alt="Svelove Logo" width="400"/>
</p>

## 🚀 Overview

This is the backend for Svelove, a dating application. It is a RESTful API built with Spring Boot that provides services for user authentication, matching, messaging, and more.
This application is specifically designed for the Polish market, utilizing a database of Polish cities for user locations and offering hobbies translated into Polish.
## 🛠️ Tech Stack

### Core
- **Java 22**
- **Spring Boot 3.4.4**
- **Spring Security**
- **PostgreSQL**
- **Maven**

### API & Communication
- **RESTful API**
- **WebSockets** for real-time messaging
- **SpringDoc (OpenAPI)** for API documentation

### Cloud & Storage
- **Supabase** for file storage

### Testing
- **JUnit 5**
- **Mockito**

### Other
- **Lombok** for reducing boilerplate code
- **Flyway** for database migrations
- **JWT** for authentication

## ✨ Key Features

- User registration and authentication
- User profile management
- Real-time chat using WebSockets
- Matching algorithm
- Photo upload and management
- Notifications
- User stats

---

## Developer Guide: Getting Started

### 💻 Prerequisites

- **Java 22**
- **Maven** or an IDE with built-in Maven support like **IntelliJ IDEA**
- A **PostgreSQL** database connection (e.g., local installation, Docker, or a cloud provider like Supabase)
- A **Supabase** project with a storage bucket for file storage.

### ⚙️ Setup Guide

1.  **Cloning the Repository:**
    ```bash
    git clone https://github.com/KacperDega/svelove-backend.git
    cd svelove-backend
    ```
2.  **Environment Variables:**
    Create a .env file in the root directory of the project using the structure provided in the .env.example file, and then fill in the following environment variables:

    - `DATABASE_URL`: The JDBC URL of your PostgreSQL database.
    - `DATABASE_USER`: The username for your PostgreSQL database.
    - `DATABASE_PASSWORD`: The password for your PostgreSQL database.
    - `JWT_SECRET`: A secret key for signing JWT tokens.
    - `SUPABASE_PROJECT_URL`: The URL of your Supabase project (used for file storage).
        - for example: `https://[YOUR_PROJECT_ID].supabase.co`
    - `SUPABASE_SERVICE_ROLE_KEY`: The service role key for your Supabase project.
    - `SUPABASE_IMAGE_BUCKET_NAME`: The name of the image bucket in your Supabase project.

3.  **Opening in IntelliJ IDEA:**
    - Open IntelliJ IDEA and select `File > Open...`.
    - Navigate to the cloned `svelove-backend` directory and open it as a project.
    - IntelliJ will automatically detect the `pom.xml` file and start downloading Maven dependencies. Wait for this process to complete (you can track the progress in the bottom-right corner of the editor).

4.  **Running the Project:**
    - After the dependencies are downloaded, find the main application class: `com.team.backend.BackendApplication`.
    - Right-click inside the editor for this class and select the **Run 'BackendApplication.main()'** option.
    - The application will start, and the server logs will appear in the *Run* console in IntelliJ.
    - **Important:** To make the application use the `.env` file, you need to edit the run configuration. In IntelliJ, go to `Run > Edit Configurations...`, select `BackendApplication`, and in the `Environment variables` field, click the icon on the right and add a path to your `.env` file.
    - After configuring, you can run the application.

---

## Additional Information

### 📑 API Documentation

 **Note:** The API documentation (Swagger UI) is available here:
 https://kacperdega.github.io/svelove-backend/

 If you run the backend locally, the Swagger UI will also be available under the address where the backend is served (by default): http://localhost:8080/swagger-ui.html

### ✅ Running Tests

```bash
./mvnw test
```
