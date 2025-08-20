# Salary Range Estimator Backend

This project is a backend service that estimates salary ranges for IT professionals in Finland using Google's Gemini AI.

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

## Getting a Google Gemini API Key

1.  Go to [Google AI Studio](https://aistudio.google.com/).
2.  Sign in with your Google account.
3.  Click on the "Get API key" button.
4.  Create a new project or select an existing one.
5.  Your API key will be generated. Copy the key.

## How to Run

### Prerequisites

*   Java 21
*   Maven

### Steps

1.  Clone the repository:
    ```bash
    git clone https://github.com/petrirh1/salary-range-estimator-backend.git
    ```
2.  Set up the Gemini API key in `application-dev.yml`:
    ```yaml
    gemini:
       api-key: your-api-key
    ```
3. Run the application with dev-profile:
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```
