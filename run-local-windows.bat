@echo off
setlocal
echo Starting AutoSphere PostgreSQL...
docker compose up -d db
if errorlevel 1 (
  echo Docker Compose failed. Start Docker Desktop and retry.
  pause
  exit /b 1
)
start "AutoSphere Backend" cmd /k "cd /d %~dp0backend && set DB_URL=jdbc:postgresql://localhost:5432/autosphere && set DB_USERNAME=autosphere && set DB_PASSWORD=autosphere_local && set JWT_SECRET=local-development-secret-change-this-to-at-least-32-characters && mvn spring-boot:run"
start "AutoSphere Frontend" cmd /k "cd /d %~dp0frontend && npm ci && npm run dev"
echo Backend and frontend terminals opened. Wait for startup, then visit http://localhost:5173
