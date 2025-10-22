@echo off
echo Starting User Service with Swagger UI...
echo.
echo After startup, you can access:
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - OpenAPI JSON: http://localhost:8080/api-docs
echo - API Base: http://localhost:8080/api/users
echo.
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run
