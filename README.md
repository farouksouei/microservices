
# Kripton Talent Microservices

A comprehensive microservices-based application for talent management and recruitment, built with Spring Boot and modern cloud-native technologies.

## 🚀 Project Overview

This project consists of multiple microservices that work together to provide a complete talent management solution:

- **API Gateway Service**: Routes and manages incoming requests  
- **Configuration Service**: Centralizes configuration management  
- **Registry Service**: Handles service discovery and registration  
- **User Service**: Manages user accounts and authentication  
- **Candidate Service**: Handles candidate profiles and data  
- **Job Service**: Manages job postings and requirements  
- **Qualification Service**: Handles candidate qualifications  
- **Communication Service**: Manages notifications and messaging  
- **Matcher Parser Service**: Processes and matches candidate data  

## 🛠 Prerequisites

- Java 17  
- Docker Desktop  
- WSL 2 (for Windows users)  
- Maven  
- Git  

## 🔧 Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/farouksouei/microservices.git
cd microservices
```

### 2. Start Core Services
```bash
docker-compose up -d nexus keycloak postgres sonarqube
```

### 3. Build Services
```bash
# For each service directory:
cd [service-name]
mvn clean package -DskipTests
```

### 4. Deploy Services
```bash
docker-compose up -d
```

## 🌐 Service Ports

- API Gateway: `8082`  
- Configuration Service: `8090`  
- Registry Service: `8081`  
- Candidate Service: `6001`  
- Job Service: `6002`  
- Qualification Service: `6003`  
- User Service: `6004`  
- Communication Service: `6005`  
- Matcher Parser: `6000`  
- Frontend: `4200`  

## 📊 Monitoring

The project includes comprehensive monitoring setup:

- **Prometheus**: Available on port `9090`  
- **Grafana**: Available on port `3000`  
- **SonarQube**: Available on port `9000`  

## 🔐 Security

Authentication and authorization are handled by **Keycloak**:

- **Default credentials:**  
  - Username: `admin`  
  - Password: `admin`  
  - Realm: `spring-boot-microservices-realm`  

## 📦 Docker Registry

Local Nexus registry is available:

- **Registry URL**: `127.0.0.1:8087`  
- **Nexus UI**: [http://localhost:8085](http://localhost:8085)  

## 🏗 Project Structure

```
microservices/
├── api-gateway-service/
├── configuration-service/
├── registry-service/
├── user-service/
├── candidate-service/
├── job-service/
├── qualification-service/
├── communication-service/
├── matcher_parser/
└── docker-compose.yml
```

## 🔍 Service Details

### Configuration Service
- Centralizes configuration management  
- Provides externalized configuration  

### Registry Service
- Service discovery using Spring Cloud Netflix Eureka  
- Load balancing and failover support  

### API Gateway
- Request routing  
- Cross-cutting concerns  
- Security implementation  

### Business Services
- **User Service**: User management and authentication  
- **Candidate Service**: Candidate profile management  
- **Job Service**: Job posting and management  
- **Qualification Service**: Skills and certifications  
- **Communication Service**: Messaging and notifications  

## 📝 Contributing

1. Fork the repository  
2. Create your feature branch  
```bash
git checkout -b feature/AmazingFeature
```
3. Commit your changes  
```bash
git commit -m 'Add some AmazingFeature'
```
4. Push to the branch  
```bash
git push origin feature/AmazingFeature
```
5. Open a Pull Request  

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Farouk Souei** 
- **Jihed Saguer** 
- **Aymen Ghazouani** 
- **Okba Bedhiafi** 
- **Islem Touati** 
