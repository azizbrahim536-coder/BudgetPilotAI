# BudgetPilot AI

BudgetPilot AI is a full-stack personal finance management application built with Angular, Spring Boot, MySQL, Flask, and Google Gemini AI.

It allows users to manage income, expenses, categories, monthly budgets, statistics, and AI-generated financial recommendations.

## Features

- Add, update, delete, and filter transactions
- Manage income and expense categories
- Create monthly budgets by expense category
- View total income, expenses, balance, and remaining budget
- Track budget progress and detect exceeded limits
- Display recent transactions and expense breakdowns
- Generate an AI financial health score
- Receive personalized recommendations
- Detect saving opportunities
- AI responses in French and Arabic
- Responsive interface

## Technologies

### Frontend
- Angular
- TypeScript
- HTML5
- CSS3
- Reactive Forms
- HttpClient
- RxJS

### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Validation
- MySQL
- Maven

### AI Service
- Python
- Flask
- Flask-CORS
- Google Gemini API
- Google GenAI SDK
- Pydantic
- Requests

## Project Structure

```text
BudgetPilotAI/
├── budgetpilotai/
│   ├── src/
│   ├── pom.xml
│   └── mvnw.cmd
├── BudgetPilotFront/
│   ├── src/
│   ├── angular.json
│   └── package.json
├── service.ia/
│   ├── app.py
│   ├── requirements.txt
│   ├── .env
│   └── .gitignore
└── README.md
```

## Architecture

```text
Angular Frontend
        |
        | HTTP
        v
Spring Boot REST API
        |
        | JPA / Hibernate
        v
MySQL Database
```

AI workflow:

```text
Angular Frontend
        |
        | POST /api/ai/analyze
        v
Flask AI Service
        |
        | Reads financial data
        v
Spring Boot REST API
        |
        | Sends structured context
        v
Google Gemini API
        |
        | Returns analysis
        v
Angular Dashboard
```

## Prerequisites

- Java 17
- Maven
- Node.js
- Angular CLI
- Python 3
- MySQL
- Git

## Database Configuration

Open:

```text
budgetpilotai/src/main/resources/application.properties
```

Example:

```properties
spring.application.name=BudgetPilotAI

spring.datasource.url=jdbc:mysql://localhost:3306/BudgetPilotAI?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8081
server.servlet.context-path=/api
```

## Run the Backend

```bash
cd budgetpilotai
mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8081/api
```

## Run the Frontend

```bash
cd BudgetPilotFront
npm install
ng serve
```

Frontend URL:

```text
http://localhost:4200
```

## Run the AI Service

```bash
cd service.ia
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python app.py
```

Create a `.env` file:

```env
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
GEMINI_MODEL=YOUR_GEMINI_MODEL
SPRING_API=http://localhost:8081/api
AI_PORT=5003
```

AI service URL:

```text
http://localhost:5003
```

Health check:

```text
GET http://localhost:5003/health
```

## API Endpoints

### Categories

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

### Transactions

```text
GET    /api/transactions
GET    /api/transactions/{id}
POST   /api/transactions
PUT    /api/transactions/{id}
DELETE /api/transactions/{id}
```

Filters example:

```text
GET /api/transactions?search=food
GET /api/transactions?type=EXPENSE
GET /api/transactions?categoryId=1
GET /api/transactions?dateFrom=2026-07-01&dateTo=2026-07-31
```

### Budgets

```text
GET    /api/budgets
GET    /api/budgets/{id}
POST   /api/budgets
PUT    /api/budgets/{id}
DELETE /api/budgets/{id}
```

### Statistics

```text
GET /api/statistics/dashboard?year=2026&month=7
```

### AI Analysis

```text
POST http://localhost:5003/api/ai/analyze
```

Request example:

```json
{
  "year": 2026,
  "month": 7,
  "language": "fr"
}
```

The AI response includes:

- Financial health status
- Score from 0 to 100
- Summary
- Key observations
- Recommendations
- Saving opportunities
- Next-month target
- Disclaimer

## Run the Complete Application

Use three terminals.

### Terminal 1

```bash
cd budgetpilotai
mvnw.cmd spring-boot:run
```

### Terminal 2

```bash
cd service.ia
venv\Scripts\activate
python app.py
```

### Terminal 3

```bash
cd BudgetPilotFront
ng serve
```

Open:

```text
http://localhost:4200
```

## Security

- Never commit the `.env` file
- Never expose the Gemini API key
- Store database passwords in environment variables
- Restrict CORS origins before deployment
- Add authentication before using real financial data
- Do not send confidential banking information to the AI service

## Future Improvements

- Spring Security and JWT authentication
- Separate data for each user
- Recurring transactions
- Savings goals
- Monthly and yearly charts
- PDF or Excel export
- Budget alerts and notifications
- Dark mode
- Docker
- Automated tests
- Cloud deployment

## Author

**Mohamed Aziz Brahim**

## License

This project is intended for educational and portfolio purposes.
