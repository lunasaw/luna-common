# luna-common

[![Maven Central](https://img.shields.io/maven-central/v/io.github.lunasaw/luna-common)](https://mvnrepository.com/artifact/io.github.lunasaw/luna-common)
[![GitHub license](https://img.shields.io/badge/Apache_2.0-blue.svg)](https://raw.githubusercontent.com/lunasaw/luna-common/master/LICENSE)
[![Build Status](https://github.com/lunasaw/luna-common/actions/workflows/maven-publish.yml/badge.svg?branch=master)](https://github.com/lunasaw/luna-common/actions)
[![Java Version](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)

[www.isluna.ml](http://www.isluna.ml) 

## 简介

luna-common 是一个功能丰富的 Java 工具库，灵感来源于 Hutool 和 Apache Commons 等优秀开源项目。它提供了一系列实用的工具类和组件，旨在简化日常开发工作，提高开发效率。

项目不仅包含基础工具类，还提供了异步工作流引擎、任务队列处理、责任链模式等高级功能，适用于各种 Java 开发场景。

## 主要特性

- **🛠️ 丰富的工具类库** - 涵盖文本处理、文件操作、网络请求、加密解密等常用功能
- **🔗 HTTP 客户端** - 基于 Apache HttpClient 5.x 的高性能 HTTP 工具，支持同步/异步请求、SSE、文件下载等
- **⚙️ 流程引擎** - 灵活的工作流引擎，支持节点链式处理和并行执行
- **🔐 安全工具** - 提供 AES、BCrypt、Base64 等多种加密算法和安全工具
- **📁 文件处理** - 完善的文件操作工具，支持压缩/解压、路径处理、文件访问者模式等
- **📅 日期日历** - 强大的日期时间处理工具，支持农历转换、节气计算等
- **💻 系统信息** - 基于 OSHI 的系统硬件信息获取工具
- **🧵 线程管理** - 线程池工具和异步执行器
- **📄 响应封装** - 标准化的 API 响应对象和结果处理

## 更新日志

- **2.6.7** - 最新版本，持续优化和功能增强
- **2024-05-29** - 流程引擎重构和优化
- **2023-09-24** - 增加断点续传功能

## 快速开始

### Maven 依赖

在项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>luna-common</artifactId>
    <version>2.6.7</version>
</dependency>
```

### 基础使用示例

#### HTTP 请求

```java
// 简单 GET 请求
String response = HttpUtils.doGet("https://api.example.com/data");

// POST 请求
Map<String, Object> params = new HashMap<>();
params.put("key", "value");
String response = HttpUtils.doPost("https://api.example.com/submit", params);

// 异步请求
HttpUtils.doGetAsync("https://api.example.com/data", new FutureCallback<String>() {
    @Override
    public void completed(String result) {
        System.out.println("请求成功: " + result);
    }
    
    @Override
    public void failed(Exception ex) {
        System.out.println("请求失败: " + ex.getMessage());
    }
    
    @Override
    public void cancelled() {
        System.out.println("请求被取消");
    }
});
```

#### 加密解密

```java
// AES 加密/解密
String key = "your-secret-key";
String plainText = "Hello World";
String encrypted = AesUtil.encrypt(plainText, key);
String decrypted = AesUtil.decrypt(encrypted, key);

// BCrypt 密码加密
String password = "mypassword";
String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
boolean isValid = BCrypt.checkpw(password, hashed);

// Base64 编码/解码
String encoded = Base64Util.encode("Hello World");
String decoded = Base64Util.decodeStr(encoded);
```

#### 文件操作

```java
// 文件压缩
ZipUtil.zip("source/path", "target.zip");

// 文件解压
ZipUtil.unzip("source.zip", "target/path");

// 文件复制
FileTools.copyFile("source.txt", "target.txt");
```

#### 日期处理

```java
// 日期格式化
String formatted = DateUtils.formatDateTime(new Date());

// 日期计算
Date tomorrow = DateUtils.addDays(new Date(), 1);
Date lastWeek = DateUtils.addWeeks(new Date(), -1);

// 农历转换
Calendarist calendarist = new Calendarist(new Date());
LunarDate lunarDate = calendarist.getLunarDate();
```

#### 流程引擎

```java
// 创建引擎上下文
EngineContext context = new EngineContext();

// 创建节点链
NodeChain nodeChain = new NodeChain();
nodeChain.addNode(new YourCustomNode());

// 执行流程
AbstractEngineExecute executor = new YourEngineExecutor();
executor.execute(context, nodeChain);
```

#### 标准响应格式

```java
// 成功响应
ResultDTO<String> success = ResultDTOUtils.success("操作成功", data);

// 失败响应
ResultDTO<Void> error = ResultDTOUtils.error("操作失败");

// 带错误码的响应
ResultDTO<Void> errorWithCode = ResultDTOUtils.error(ResultCode.PARAM_ERROR);
```

## 核心模块详解

### 📁 核心包结构

| 模块          | 包路径                       | 功能描述                        |
|-------------|---------------------------|-----------------------------|
| **DTO**     | `com.luna.common.dto`     | 标准响应对象和结果码定义                |
| **Engine**  | `com.luna.common.engine`  | 流程引擎，支持节点链式处理和并行执行          |
| **Net**     | `com.luna.common.net`     | HTTP 客户端，支持同步/异步请求、SSE、文件下载 |
| **Encrypt** | `com.luna.common.encrypt` | 加密解密工具（AES、BCrypt、Hash 等）   |
| **File**    | `com.luna.common.file`    | 文件操作、压缩解压、路径处理              |
| **Date**    | `com.luna.common.date`    | 日期时间处理和日历工具                 |
| **Text**    | `com.luna.common.text`    | 字符串处理、格式化、序列化               |
| **Math**    | `com.luna.common.math`    | 数学运算、金额处理、数字工具              |
| **OS**      | `com.luna.common.os`      | 系统信息获取、硬件检测                 |
| **Thread**  | `com.luna.common.thread`  | 线程池工具和异步执行器                 |
| **Cache**   | `com.luna.common.cache`   | 基于 Guava 的简单缓存实现            |

### 🔗 HTTP 客户端特性

- **多种请求方式**: GET、POST、PUT、DELETE、PATCH
- **异步支持**: 基于 Apache HttpClient 5.x 的异步实现
- **SSE 支持**: Server-Sent Events 实时数据流
- **文件上传下载**: 支持多文件上传和断点续传
- **连接池管理**: 自动连接池管理和复用
- **SSL/TLS**: 完整的 HTTPS 支持
- **代理支持**: HTTP/HTTPS 代理配置
- **Cookie 管理**: 自动 Cookie 存储和管理

### ⚙️ 流程引擎特性

- **节点链模式**: 支持节点链式处理
- **并行执行**: 支持节点并行处理
- **上下文管理**: 统一的执行上下文
- **可扩展性**: 易于扩展自定义节点
- **异常处理**: 完善的异常处理机制

## 文档链接

更详细的使用文档请访问：<a href="https://lunasaw.github.io/luna-common/docs/" target="_blank">📖 完整文档</a>

## 贡献者

- **Luna** - 项目创建者和主要维护者

## 技术栈

| 技术                    | 版本      | 说明         |
|-----------------------|---------|------------|
| **Java**              | 17+     | 基础开发语言     |
| **Maven**             | 3.6+    | 项目构建工具     |
| **Apache HttpClient** | 5.4.4   | HTTP 客户端框架 |
| **Guava**             | 33.4.8  | Google 核心库 |
| **FastJSON2**         | 2.0.57  | JSON 处理库   |
| **OSHI**              | 6.8.2   | 系统信息获取库    |
| **Apache Commons**    | 最新版     | 基础工具库      |
| **Spring Context**    | 6.2.8   | 依赖注入支持     |
| **Lombok**            | 1.18.38 | 代码生成工具     |

## 构建部署

### 本地构建

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn clean package

# 安装到本地仓库
mvn clean install
```

### 发布到中央仓库

```bash
# 发布到 GitHub Packages（默认）
mvn clean deploy

# 发布到 Maven Central
mvn clean deploy -P ossrh
```

## 开发规范

### 代码格式化

- 使用项目提供的 `ali-code-style.xml` 格式化模板
- Eclipse：直接导入 XML 文件
- IntelliJ IDEA：使用 Eclipse Code Formatter 插件配置

### 编码规范

- 遵循阿里巴巴 P3C 编码规范
- 代码注释要完整清晰
- 提交前必须格式化代码
- XML 文件使用 TAB 缩进

### 提交规范

- 提交信息使用中文，简洁明确
- 重要功能变更需要更新文档
- 确保单元测试通过

## 许可证

本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

## 联系方式

- **项目主页**: [GitHub](https://github.com/lunasaw/luna-common)
- **问题反馈**: [Issues](https://github.com/lunasaw/luna-common/issues)
- **邮箱**: iszychen@gmail.com
- **个人网站**: [www.isluna.ml](http://www.isluna.ml)
