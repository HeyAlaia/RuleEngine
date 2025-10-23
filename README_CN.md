# 🚀 Eport 守护进程规则引擎

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

## 🌐 语言 / Language

[English](README.md) | **中文**

## 📋 项目概述

Eport 守护进程规则引擎是一个为港口管理运营而构建的企业级规则引擎系统。它提供了一个统一的框架，同时支持 **Drools** 和 **LiteFlow** 规则引擎，在分布式微服务环境中实现灵活的业务规则管理和执行。

该系统旨在处理港口和物流管理系统中常见的复杂业务逻辑验证、数据处理工作流和自动化决策过程。

## ✨ 核心特性

- 🔄 **双规则引擎支持**：原生支持 Drools 和 LiteFlow 规则引擎
- 🏗️ **微服务架构**：基于 Spring Cloud 和 Nacos 服务发现构建
- 🔌 **灵活规则加载**：支持本地文件系统和基于 Redis 的规则存储
- 📊 **企业级集成**：与现有 Eport 生态系统无缝集成
- 🛡️ **安全认证**：内置 OAuth2 和 JWT 安全机制
- 📈 **性能监控**：全面的日志记录和监控能力
- 🐳 **容器化就绪**：Docker 支持，便于部署
- 🔧 **热重载**：动态规则重新加载，无需重启服务

## 🛠️ 技术栈

### 核心框架
- **Java 21**：最新 LTS 版本，支持现代语言特性
- **Spring Boot 3.x**：企业级应用框架
- **Spring Cloud 2023.x**：微服务基础设施
- **Nacos**：服务发现和配置管理

### 规则引擎
- **Drools 10.1.0**：业务规则管理系统
- **LiteFlow 2.15.0**：轻量级规则编排引擎

### 数据存储
- **MyBatis Plus**：增强型 ORM 框架
- **MySQL/MSSQL/Oracle/PostgreSQL**：多数据库支持
- **Druid**：高性能连接池
- **Redis**：缓存和规则存储

### 基础设施
- **Docker**：容器化
- **Undertow**：高性能 Web 服务器
- **Maven**：构建和依赖管理

## 🚀 快速开始

### 环境要求

- **JDK 21+**：编译和运行环境必需
- **Maven 3.6+**：项目构建管理
- **Docker**：可选，用于容器化部署
- **Nacos 服务器**：用于服务注册和配置管理

### 安装步骤

1. **克隆仓库**

```bash
git clone https://github.com/your-org/eport-rule-engine.git
cd eport-rule-engine
```

2. **构建项目**

```bash
# 构建所有模块
mvn clean package

# 构建特定配置
mvn clean package -P cloud
```

3. **配置服务**

更新 starter 模块中的 `application.yml`：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

4. **运行应用**

```bash
# 从 starter 模块运行
cd eport-rule-engine-starter
java -jar target/eport-rule-engine-starter-5.9.0.jar
```

### Docker 部署

```bash
# 构建 Docker 镜像
cd eport-rule-engine-starter
docker build -t eport-rule-engine:latest .

# 运行容器
docker run -d \
  --name eport-rule-engine \
  -p 19992:19992 \
  -e NACOS_HOST=your-nacos-server \
  eport-rule-engine:latest
```

## 📚 项目结构

```
eport-rule-engine/
├── eport-rule-engine-core/          # 核心规则引擎实现
│   ├── src/main/java/
│   │   └── com/eport/daemon/rule/
│   │       ├── common/              # 通用常量和枚举
│   │       ├── engine/              # 规则引擎抽象
│   │       │   ├── impl/            # Drools 和 LiteFlow 实现
│   │       │   ├── AbstractRuleEngine.java
│   │       │   ├── RuleEngine.java
│   │       │   ├── RuleEngineBuilder.java
│   │       │   └── RuleEngineFactory.java
│   │       ├── pojo/                # 数据模型
│   │       ├── storage/             # 规则存储和加载
│   │       │   └── impl/            # 本地和 Redis 实现
│   │       └── utils/               # 工具类
│   └── pom.xml
├── eport-rule-engine-starter/       # Spring Boot 应用启动器
│   ├── src/main/java/
│   │   └── com/eport/daemon/rule/
│   │       ├── EportDaemonRuleEngineApplication.java
│   │       └── liteflow/            # LiteFlow 组件
│   ├── src/test/                    # 测试用例和示例
│   │   ├── java/                    # 单元测试
│   │   └── resources/rules/         # 示例规则
│   │       ├── drools/              # Drools 规则文件 (.drl)
│   │       └── liteflow/            # LiteFlow 规则文件 (.xml)
│   ├── Dockerfile
│   └── pom.xml
├── pom.xml                          # 父 POM
├── LICENSE
├── README.md
└── README_CN.md
```

## 🔧 配置说明

### 规则引擎配置

系统通过 `EngineType` 枚举支持多种规则引擎：

- `DROOLS`：传统规则引擎，适用于复杂业务逻辑
- `LITEFLOW`：轻量级编排引擎，适用于工作流管理

### 规则存储选项

在应用中配置规则加载策略：

- **本地文件系统**：从类路径或文件系统加载规则
- **Redis**：集中式规则存储，支持热重载

### 数据库支持

系统支持多种数据库类型：
- MySQL
- Microsoft SQL Server
- Oracle Database
- PostgreSQL

## 📝 规则示例

### Drools 规则 (.drl)

```drl
package rules.engine.set.ruleset1
import com.alibaba.fastjson.JSONObject

rule "vehicle_weight_check"
when
    $fact:JSONObject(getInteger("weight") != null && getInteger("weight") >= 60)
then
    $fact.put("overweight", 1);
    System.out.println("车辆超重");
end

rule "cargo_check"
    salience 10
    no-loop true
when
    $fact:JSONObject(getString("hasGood") != null && getString("hasGood").equals("0"))
then
    $fact.put("not_good", 1);
    $fact.put("passd", "0");
    System.out.println("该车无货");
    update($fact);
end
```

### LiteFlow 规则 (.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<flow>
    <chain name="validation_chain">
        THEN(weight_check, cargo_check, WHEN(special_check, standard_check));
    </chain>
    
    <chain name="weight_check">
        THEN(weight_validation);
    </chain>
    
    <chain name="cargo_check">
        THEN(cargo_validation);
    </chain>
</flow>
```

## 🧪 测试

运行测试套件验证功能：

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=SimpleLiteFlowTest
mvn test -Dtest=SimpleServiceTest
```

## 🚀 API 接口

服务在 `/admin` 上下文路径下暴露 RESTful API：

- **基础 URL**：`http://localhost:19992/admin`
- **API 文档**：通过 `/admin/swagger-ui.html` 访问 Swagger UI

## 📈 监控与可观测性

### 健康检查
- Spring Boot Actuator 端点
- 规则引擎自定义健康指示器

### 日志记录
- 使用 Logback 的结构化日志
- 按组件配置日志级别
- 与集中式日志系统集成

### 指标监控
- 应用性能指标
- 规则执行统计
- 系统资源监控

## 🔒 安全特性

- **OAuth2 认证**：安全的 API 访问
- **JWT 令牌支持**：无状态认证
- **基于角色的授权**：细粒度访问控制
- **API 加密**：请求/响应加密支持

## 🤝 贡献指南

我们欢迎为 Eport 规则引擎贡献代码！请遵循以下指导原则：

1. **Fork 仓库**
2. **创建功能分支**：`git checkout -b feature/amazing-feature`
3. **提交更改**：`git commit -m 'Add amazing feature'`
4. **推送到分支**：`git push origin feature/amazing-feature`
5. **创建 Pull Request**

### 开发指南

- 遵循 Java 编码标准和最佳实践
- 为新功能编写全面的单元测试
- 更新 API 变更的相关文档
- 尽可能确保向后兼容性

## 📄 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 📞 支持与联系

**项目维护者**：[@HeyAlaia](https://github.com/HeyAlaia)
- **邮箱**：alaia@eport.com
- **博客**：[alaiablog.pages.dev](https://alaiablog.pages.dev/)

**企业支持**：如需商业支持和定制服务，请联系我们的企业团队。

## 🔄 版本历史

- **v5.9.0**：当前版本
  - 支持 Java 21
  - 升级到 Spring Boot 3.x
  - 集成 Drools 10.1.0 和 LiteFlow 2.15.0
  - 增强安全性和监控功能

## 🗺️ 路线图

- [ ] 支持更多规则引擎（如 Easy Rules）
- [ ] 图形化规则编辑器
- [ ] 规则版本管理和回滚
- [ ] 更丰富的监控指标和告警
- [ ] 规则性能分析工具

---

<div align="center">
  <sub>用 ❤️ 为 Eport 生态系统构建</sub>
</div>