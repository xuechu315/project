# 多银龄守护——独居老人健康监护与应急响应系统

一个基于Spring Boot + MyBatis的养老照护系统，提供健康监护、异常事件检测、SOS应急响应等功能。

**课题编号：** SE2026-10

## 项目结构

```
elderly-care-system/
├── backend/                    # 后端模块 (Spring Boot + MyBatis)
│   ├── src/main/java/com/elderly/care/
│   │   ├── controller/         # 控制层 (9个Controller)
│   │   ├── service/            # 业务层 (11个Service)
│   │   ├── mapper/             # 数据访问层 (12个Mapper)
│   │   ├── entity/             # 实体类 (12个Entity)
│   │   ├── dto/                # 数据传输对象 (13个DTO)
│   │   ├── config/             # 配置类
│   │   ├── exception/          # 异常处理
│   │   ├── enums/              # 枚举类型
│   │   ├── utils/              # 工具类
│   │   └── agent/              # 智能体模块 (待开发)
│   ├── src/main/resources/
│   │   ├── application.yaml    # 应用配置
│   │   └── db/                 # 数据库脚本
│   └── pom.xml
├── frontend/                   # 前端模块
│   ├── family/                 # 家属端 (Flutter)
│   ├── elder/                  # 老人端 (Flutter)
│   ├── admin/                  # 管理员端 (HTML)
│   └── doctor/                 # 医生端 (HTML)
├── pom.xml                     # Maven父级配置
└── README.md
```

## 技术栈

### 后端
- **框架：** Spring Boot 4.0.6
- **ORM：** MyBatis 3.0.3（注解方式）
- **数据库：** MySQL 8.0
- **连接池：** HikariCP
- **工具库：** Lombok
- **API文档：** Swagger/OpenAPI 3.0
- **验证：** Spring Validation
- **Java版本：** 21

### 前端
- **移动端：** Flutter（家属端、老人端）
- **Web端：** HTML/CSS/JavaScript（管理员端、医生端）

## 核心功能

### 已实现功能 ✅

1. **用户认证** - 多角色登录（老人/家属/医生/管理员）
2. **健康监护** - 心率、血压、步数等健康数据上报与查询
3. **异常事件检测** - 自动检测并记录异常事件
4. **SOS应急响应** - SOS报警、响应调度、状态跟踪
5. **用药管理** - 药品信息、用药记录跟踪
6. **病史管理** - 老人医疗病史记录
7. **家属管理** - 家属成员信息管理
8. **医生管理** - 医生信息及关联管理
9. **应急响应** - 救护车调度、响应状态管理

### 待开发功能 ⏳

- **多智能体协同** - 健康监护智能体、行为分析智能体、应急响应智能体、通知推送智能体

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE elderly_guard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入数据表
mysql -u root -p elderly_guard < backend/src/main/resources/db/elderly_care_db.sql
```

### 后端启动

```bash
# 1. 进入backend目录
cd backend

# 2. 修改数据库配置（如需要）
# 编辑 src/main/resources/application.yaml

# 3. 构建项目
mvn clean package

# 4. 运行应用
mvn spring-boot:run

# 或直接运行jar包
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

应用启动后访问：
- **API地址：** http://localhost:8080/api
- **Swagger文档：** http://localhost:8080/api/swagger-ui.html

### 前端启动

根据各前端模块的README说明进行启动。

## API接口概览

| 模块 | 路径前缀 | 说明 |
|------|---------|------|
| 认证 | `/api/auth` | 用户登录 |
| 健康数据 | `/api/health` | 健康数据上报/查询 |
| 异常事件 | `/api/events` | 异常事件管理 |
| SOS报警 | `/api/sos` | SOS记录管理 |
| 医生 | `/api/doctors` | 医生信息管理 |
| 家属 | `/api/family-members` | 家属成员管理 |
| 病史 | `/api/medical-history` | 病史管理 |
| 用药 | `/api/medications` | 用药管理 |
| 应急响应 | `/api/emergency-response` | 应急响应调度 |

## 架构设计

### 三层架构

```
Controller层 → Service层 → Mapper层 → MySQL
     ↓              ↓           ↓
   DTO封装      业务逻辑     SQL注解
```

### 技术特点

- ✅ **MyBatis注解方式** - 无需XML配置文件
- ✅ **DTO模式** - Entity与API响应解耦
- ✅ **统一响应格式** - Result<T>封装
- ✅ **全局异常处理** - @RestControllerAdvice
- ✅ **RESTful API** - 标准HTTP方法
- ✅ **Swagger文档** - API自动生成
- ✅ **事务管理** - @Transactional

## 开发规范

- **Mapper命名：** findAll, findById, insert, update, deleteById
- **条件查询：** findBy + 字段名（驼峰命名）
- **枚举处理：** Service层调用`.name()`转换为字符串
- **参数传递：** 多参数使用@Param注解
- **依赖注入：** 构造函数注入（@RequiredArgsConstructor）

## 项目进度

- ✅ 后端基础框架完成
- ✅ 数据库设计与实现
- ✅ 所有CRUD接口完成
- ✅ DTO层与异常处理
- ✅ API文档生成
- ⏳ 智能体模块开发（进行中）

## 常见问题

**Q: 为什么没有impl文件夹？**  
A: Spring Boot允许Service直接实现，无需接口+实现类分离，代码更简洁。

**Q: 为什么保留DTO文件夹？**  
A: DTO用于解耦Entity和API响应，隐藏敏感字段，是企业级开发的标准实践。

**Q: MyBatis为什么没有XML文件？**  
A: 本项目使用MyBatis注解方式（@Select/@Insert/@Update/@Delete），无需XML配置。

## 许可证

本项目仅供学习和研究使用。
