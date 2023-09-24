# luna-common

[![Maven Central](https://img.shields.io/maven-central/v/io.github.lunasaw/luna-common)](https://mvnrepository.com/artifact/io.github.lunasaw/luna-common)
[![GitHub license](https://img.shields.io/badge/MIT_License-blue.svg)](https://raw.githubusercontent.com/lunasaw/luna-common/master/LICENSE)
[![Build Status](https://github.com/lunasaw/luna-common/actions/workflows/maven-publish.yml/badge.svg?branch=master)](https://github.com/lunasaw/luna-common/actions)


[www.isluna.ml](http://www.isluna.ml) 


个人使用开发工具基础包,借鉴Hutool,Apache Commons等优秀开源项目，提供一些常用的工具类，方便开发。

另外提供异步工作流，比如任务队列处理，责任链基类等常用工具。

更新日志

- 2023-09-24 断点续传


# Contributor

- Luna

# how to use

<a href="https://lunasaw.github.io/luna-common/docs/" target="_blank">文档链接</a>

```xml

<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>luna-common</artifactId>
    <version>${last.version}</version>
</dependency>

```

# 代码规范

- 后端使用同一份代码格式化膜模板ali-code-style.xml，ecplise直接导入使用，idea使用Eclipse Code Formatter插件配置xml后使用。
- 前端代码使用vs插件的Beautify格式化，缩进使用TAB
- 后端代码非特殊情况准守P3C插件规范
- 注释要尽可能完整明晰，提交的代码必须要先格式化
- xml文件和前端一样，使用TAB缩进
