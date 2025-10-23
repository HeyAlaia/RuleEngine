# 🚀 Eport Daemon Rule Engine

<div align="center">

  ![Version](https://img.shields.io/badge/version-5.9.0-blue.svg?style=for-the-badge)
  ![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=java)
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green.svg?style=for-the-badge&logo=spring-boot)
  ![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.x-brightgreen.svg?style=for-the-badge&logo=spring)
  ![License](https://img.shields.io/badge/license-MIT-purple.svg?style=for-the-badge)

</div>

<p align="center">
  <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Gear.png" width="100" />
</p>

## 🌐 Language / 语言

**English** | [中文](README_CN.md)

## 📋 Overview

Eport Daemon Rule Engine is an enterprise-grade rule engine system built for port management operations. It provides a unified framework supporting both **Drools** and **LiteFlow** rule engines, enabling flexible business rule management and execution in distributed microservice environments.

The system is designed to handle complex business logic validation, data processing workflows, and automated decision-making processes commonly found in port and logistics management systems.

## ✨ Key Features

- 🔄 **Dual Rule Engine Support**: Native support for both Drools and LiteFlow rule engines
- 🏗️ **Microservice Architecture**: Built on Spring Cloud with Nacos service discovery
- 🔌 **Flexible Rule Loading**: Support for local file system and Redis-based rule storage
- 📊 **Enterprise Integration**: Seamless integration with existing Eport ecosystem
- 🛡️ **Security & Authentication**: Built-in OAuth2 and JWT security mechanisms
- 📈 **Performance Monitoring**: Comprehensive logging and monitoring capabilities
- 🐳 **Container Ready**: Docker support for easy deployment
- 🔧 **Hot Reload**: Dynamic rule reloading without service restart

## 🛠️ Technology Stack

### Core Framework
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.x**: Enterprise application framework
- **Spring Cloud 2023.x**: Microservice infrastructure
- **Nacos**: Service discovery and configuration management

### Rule Engines
- **Drools 10.1.0**: Business rule management system
- **LiteFlow 2.15.0**: Lightweight rule orchestration engine

### Data & Storage
- **MyBatis Plus**: Enhanced ORM framework
- **MySQL/MSSQL/Oracle/PostgreSQL**: Multi-database support
- **Druid**: High-performance connection pool
- **Redis**: Caching and rule storage

### Infrastructure
- **Docker**: Containerization
- **Undertow**: High-performance web server
- **Maven**: Build and dependency management

## 🚀 Quick Start

### Prerequisites

- **JDK 21+**: Required for compilation and runtime
- **Maven 3.6+**: For project build management
- **Docker**: Optional, for containerized deployment
- **Nacos Server**: For service registration and configuration

### Installation Steps

1. **Clone Repository**

```bash
git clone https://github.com/your-org/eport-rule-engine.git
cd eport-rule-engine
```

2. **Build Project**

```bash
# Build all modules
mvn clean package

# Build specific profile
mvn clean package -P cloud
```

3. **Configure Services**

Update `application.yml` in the starter module:

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

4. **Run Application**

```bash
# Run from starter module
cd eport-rule-engine-starter
java -jar target/eport-rule-engine-starter-5.9.0.jar
```

### Docker Deployment

```bash
# Build Docker image
cd eport-rule-engine-starter
docker build -t eport-rule-engine:latest .

# Run container
docker run -d \
  --name eport-rule-engine \
  -p 19992:19992 \
  -e NACOS_HOST=your-nacos-server \
  eport-rule-engine:latest
```

## 📚 Project Structure

```
eport-rule-engine/
├── eport-rule-engine-core/          # Core rule engine implementation
│   ├── src/main/java/
│   │   └── com/eport/daemon/rule/
│   │       ├── common/              # Common constants and enums
│   │       ├── engine/              # Rule engine abstractions
│   │       │   ├── impl/            # Drools and LiteFlow implementations
│   │       │   ├── AbstractRuleEngine.java
│   │       │   ├── RuleEngine.java
│   │       │   ├── RuleEngineBuilder.java
│   │       │   └── RuleEngineFactory.java
│   │       ├── pojo/                # Data models
│   │       ├── storage/             # Rule storage and loading
│   │       │   └── impl/            # Local and Redis implementations
│   │       └── utils/               # Utility classes
│   └── pom.xml
├── eport-rule-engine-starter/       # Spring Boot application starter
│   ├── src/main/java/
│   │   └── com/eport/daemon/rule/
│   │       ├── EportDaemonRuleEngineApplication.java
│   │       └── liteflow/            # LiteFlow components
│   ├── src/test/                    # Test cases and examples
│   │   ├── java/                    # Unit tests
│   │   └── resources/rules/         # Sample rules
│   │       ├── drools/              # Drools rule files (.drl)
│   │       └── liteflow/            # LiteFlow rule files (.xml)
│   ├── Dockerfile
│   └── pom.xml
├── pom.xml                          # Parent POM
├── LICENSE
├── README.md
└── README_CN.md
```

## 🔧 Configuration

### Rule Engine Configuration

The system supports multiple rule engines through the `EngineType` enumeration:

- `DROOLS`: Traditional rule-based engine for complex business logic
- `LITEFLOW`: Lightweight orchestration engine for workflow management

### Rule Storage Options

Configure rule loading strategy in your application:

- **Local File System**: Load rules from classpath or file system
- **Redis**: Centralized rule storage with hot reload capabilities

### Database Support

The system supports multiple database types:
- MySQL
- Microsoft SQL Server  
- Oracle Database
- PostgreSQL

## 📝 Rule Examples

### Drools Rules (.drl)

```drl
package rules.engine.set.ruleset1
import com.alibaba.fastjson.JSONObject

rule "vehicle_weight_check"
when
    $fact:JSONObject(getInteger("weight") != null && getInteger("weight") >= 60)
then
    $fact.put("overweight", 1);
    System.out.println("Vehicle is overweight");
end
```

### LiteFlow Rules (.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<flow>
    <chain name="validation_chain">
        THEN(weight_check, cargo_check, WHEN(special_check, standard_check));
    </chain>
</flow>
```

## 🧪 Testing

Run the test suite to verify functionality:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=SimpleLiteFlowTest
mvn test -Dtest=SimpleServiceTest
```

## 🚀 API Endpoints

The service exposes RESTful APIs under the `/admin` context path:

- **Base URL**: `http://localhost:19992/admin`
- **API Documentation**: Access Swagger UI at `/admin/swagger-ui.html`

## 📈 Monitoring & Observability

### Health Checks
- Spring Boot Actuator endpoints
- Custom health indicators for rule engines

### Logging
- Structured logging with Logback
- Configurable log levels per component
- Integration with centralized logging systems

### Metrics
- Application performance metrics
- Rule execution statistics
- System resource monitoring

## 🔒 Security

- **OAuth2 Authentication**: Secure API access
- **JWT Token Support**: Stateless authentication
- **Role-based Authorization**: Fine-grained access control
- **API Encryption**: Request/response encryption support

## 🤝 Contributing

We welcome contributions to improve the Eport Rule Engine! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines

- Follow Java coding standards and best practices
- Write comprehensive unit tests for new features
- Update documentation for API changes
- Ensure backward compatibility when possible

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support & Contact

**Project Maintainer**: [@HeyAlaia](https://github.com/HeyAlaia)
- **Email**: alaia@eport.com
- **Blog**: [alaiablog.pages.dev](https://alaiablog.pages.dev/)

**Enterprise Support**: Contact our enterprise team for commercial support and customization services.

---

<div align="center">
  <sub>Built with ❤️ for the Eport ecosystem</sub>
</div>