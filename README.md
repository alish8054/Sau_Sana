# Sau Sana

Sau Sana is a psychotherapy and behavioral-health web application for clients and psychologists. It combines journaling, mood tracking, dream analysis, assignment management, scheduling, chat, and a guided AI Socratic questioning feature designed to help users reflect on feelings and thoughts without giving medical advice.

## Overview

The project contains two main parts:

- Java/Spring Boot backend for authentication, user roles, therapy workflows, diaries, chat, reminders, and AI interactions
- React + Vite frontend for the client and psychologist experience

## Main Features

- Client and psychologist roles with secure login and registration
- Mood and diary tracking for personal reflection
- Dream analysis and sleep-related entries
- Therapy assignments and progress tracking
- Calendar and event management
- Internal messaging between clients and psychologists
- AI-powered Socratic questions for reflective journaling
- Theme support and multilingual UI

## Tech Stack

- Backend: Java 17, Spring Boot 3.3, Spring Data JPA, PostgreSQL
- AI: Spring AI + OpenAI integration
- Frontend: React 18, Vite, React Router, FullCalendar, Recharts, Phaser
- Build tools: Gradle, npm

## Repository Structure

```text
.
├── build.gradle                 # Spring Boot backend build config
├── gradlew / gradlew.bat        # Gradle wrapper
├── settings.gradle              # Gradle settings
├── src/
│   └── main/
│       ├── java/com/diploma/   # Backend source code
│       └── resources/          # App config and properties
├── frontend/
│   ├── App.jsx                 # React app root and routes
│   ├── components/             # UI screens and reusable components
│   ├── src/                   # Frontend helpers, API setup, i18n
│   ├── package.json            # Frontend dependencies and scripts
│   ├── vite.config.js          # Vite config
│   └── public/                 # Static assets
├── railway.json                # Railway deployment config
├── railway.env.example         # Example deployment environment file
├── system.properties           # Heroku/Railway runtime config
└── .gitignore
```

## How It Works

The backend exposes REST endpoints under `/api` for authentication, user management, diary entries, assignments, chat, dashboard stats, and AI requests. The frontend uses React Router to present different screens depending on whether the user is a client or psychologist.

The AI experience is implemented through `AiController` and `AiChatService`, which send prompts to OpenAI when a valid API key is configured, and fall back to a Socratic-style question set when the key is missing or not set.

## Prerequisites

Before running the app, make sure you have:

- Java 17+
- Node.js 18+
- npm
- PostgreSQL 14+ (default config expects a local database named `diploma`)
- An OpenAI API key optional, but recommended for the AI feature

## Backend Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE diploma;
```

2. Update environment variables or runtime config if needed.

3. Run the backend:

```bash
./gradlew bootRun
```

The default Spring configuration is in `src/main/resources/application.properties` and expects:

- PostgreSQL on `localhost:5432`
- username: `postgres`
- password: `postgres`

## Frontend Setup

From the project root:

```bash
cd frontend
npm install
npm run dev
```

This runs the Vite dev server, usually on:

- http://localhost:5173

## Environment Variables

The backend reads the following values from the environment or default properties:

```bash
OPENAI_API_KEY=your_api_key
OPENAI_MODEL=gpt-5.2
AI_SOCRATIC_QUESTION_COUNT=5
JWT_SECRET=change_me
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

If `OPENAI_API_KEY` is not set, the app still works in a fallback mode using scripted Socratic questions.

## Useful Commands

### Build backend

```bash
./gradlew build
```

### Build frontend

```bash
cd frontend
npm run build
```

### Run frontend preview

```bash
cd frontend
npm run preview
```

## Notes

- The app is designed around `CLIENT` and `PSYCHOLOGIST` roles.
- The UI is intentionally dark-mode-first, with light theme support via `html[data-theme='light']`.
- The backend is structured around controllers, services, repositories, and security utilities.

## License

No explicit license file is present in the repository, so usage and distribution are governed by the repository owner unless otherwise stated.

## Getting Started Summary

```bash
git clone https://github.com/alish8054/Sau_Sana.git
cd Sau_Sana
./gradlew bootRun
# In another terminal
cd frontend
npm install
npm run dev
```

This should give you a working full-stack setup for the app.
