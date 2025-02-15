# 🛠️ Development Commands

## 📋 Prerequisites

### Java Requirements

This project requires Java 17 to run Gradle commands. If you haven't installed it yet:
1. Download [Temurin Java 17](https://adoptium.net/temurin/releases/?version=17)
2. Install it following your OS instructions
3. Verify installation with `java -version`

### Environment Variables

In order to build this project, you need to set up all required environment variables. The following variables are required:

| Variable         | Required | Values              | Default | Description           |
|------------------|----------|---------------------|---------|-----------------------|
| `TARGET_BROWSER` | Yes      | `firefox`, `chrome` | -       | Browser to build for  |
| `IS_DEVELOPMENT` | No       | `true`, `false`     | false   | Development mode flag |

## 🚀 Development

### Build Extension Files
For manual loading in browser:
```bash
./gradlew extension:buildExtension
```

### Clean Build
Fix Gradle/Kotlin-JS issues with:
```bash
./gradlew clean
```

## 📦 Production Build

### Package Extension
Creates a browser-specific package (`.zip` for Chrome, `.xpi` for Firefox):
```bash
./gradlew clean extension:packageExtension
```

Output location: `extension/build/distributions/{browserName}.{browserExtension}`

> 💡 **Tip**: If you encounter any issues, try running `clean` before other commands
