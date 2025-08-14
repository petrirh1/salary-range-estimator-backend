# Salary Range Estimator Backend

This project is a backend service that estimates salary ranges for software developers in Finland using Google's Gemini AI.

## Features

*   Estimates salary ranges based on job title, experience, education, industry, location, and technologies.
*   Provides a summary of the factors affecting the salary.

## Technologies Used

*   Java 21
*   Spring Boot 3
*   Maven
*   Ehcache
*   Lombok
*   Google Gemini API

## How to Run

### Prerequisites

*   Java 21
*   Maven

### Steps

1.  Clone the repository:
    ```bash
    git clone https://github.com/your-username/salary-range-estimator-backend.git
    ```
2.  Set up the Gemini API key in `application-dev.yml`:
    ```yaml
    gemini:
       api-key: your-api-key
    ```
3.  Set the active Spring profile in `application.yml`:
    ```yaml
    spring:
      profiles:
        active: dev
    ```
4.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
