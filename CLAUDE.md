# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

luna-common is a Java utility library inspired by Hutool and Apache Commons, providing common utilities and tools for
Java development. The project includes asynchronous workflow capabilities, task queues, and responsibility chain
patterns.

## Build System

- **Build Tool**: Maven
- **Java Version**: 17 (source/target)
- **Project Type**: Maven library project
- **Artifact**: `io.github.lunasaw:luna-common`

## Essential Commands

### Building and Testing

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package the project
mvn clean package

# Install to local repository
mvn clean install
```

### Publishing (requires proper credentials)

```bash
# Deploy to GitHub Packages (default profile)
mvn clean deploy

# Deploy to OSSRH/Maven Central
mvn clean deploy -P ossrh
```

## Architecture Overview

The project is organized into functional modules under `com.luna.common`:

### Core Modules

- **dto**: Standard response objects (`ResultDTO`, `ResultCode`) for API responses
- **engine**: Workflow/process engine with context management and node chains
- **net**: HTTP utilities including async HTTP clients, SSE support, and download capabilities
- **encrypt**: Cryptographic utilities (AES, BCrypt, Base64, Hash functions)
- **file**: File operations, compression (ZIP, GZIP), and path utilities
- **date**: Date/time utilities and calendar operations
- **text**: String processing, formatting, serialization, and text utilities
- **math**: Mathematical operations, money handling, and number utilities
- **os**: System information, hardware detection using OSHI library
- **cache**: Simple caching implementations using Guava
- **thread**: Thread pool utilities and async execution

### Key Dependencies

- **Apache Commons**: lang3, collections4, codec, io
- **HTTP Client**: Apache HttpClient 5.x
- **JSON**: Alibaba FastJSON2
- **Caching**: Google Guava
- **System Info**: OSHI for hardware/OS detection
- **Validation**: Jakarta Validation API
- **Spring**: Context support for bean management

### Design Patterns

- **Engine Pattern**: Process/workflow engine with node-based execution
- **Chain Pattern**: Responsibility chain implementations
- **DTO Pattern**: Standardized response objects with success/error states
- **Utility Pattern**: Static utility classes for common operations

## Development Notes

### Code Style

- Follow Alibaba P3C coding standards
- Use provided `ali-code-style.xml` for formatting
- TAB indentation for XML files
- Complete and clear comments required

### Testing Framework

- Uses JUnit 4.13.2
- Test files located in `src/test/java/com/luna/common/utils/`
- SLF4J Simple for test logging

### Package Structure

Each module follows standard patterns:

- Core classes in module root
- Constants in `constant/` subpackages
- DTOs in `dto/` or `pojo/` subpackages
- Utility classes suffixed with `Util` or `Utils`