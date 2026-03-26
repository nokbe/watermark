# 照片水印应用编译指南

## 方法一：使用Android Studio（推荐）

### 前提条件
1. 安装Android Studio Hedgehog (2023.1.1) 或更高版本
   - 下载地址: https://developer.android.com/studio
   
2. 安装JDK 17或更高版本
   - 下载地址: https://adoptium.net/
   - 或使用Android Studio自带的JDK

### 编译步骤

#### 1. 打开项目
```bash
# 在Android Studio中
File → Open → 选择项目目录
```

#### 2. 同步Gradle
- Android Studio会自动提示同步Gradle
- 点击 "Sync Now"
- 等待依赖下载完成（首次可能需要10-20分钟）

#### 3. 编译APK
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

#### 4. 获取APK
编译完成后，点击通知栏的 "locate" 链接，或在以下位置找到APK：
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 方法二：使用命令行编译

### 前提条件

#### 1. 安装JDK 17+
```bash
# Windows: 下载并安装
https://adoptium.net/

# 设置环境变量
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.0.8.101-hotspot
Path添加: %JAVA_HOME%\bin

# 验证
java -version
javac -version
```

#### 2. 配置Android SDK
```bash
# 方法1: 使用Android Studio安装
Android Studio → SDK Manager

# 方法2: 单独下载
https://developer.android.com/studio#command-tools

# 设置环境变量
ANDROID_HOME = C:\Users\[用户名]\AppData\Local\Android\Sdk
Path添加: %ANDROID_HOME%\platform-tools
Path添加: %ANDROID_HOME%\tools
```

### 编译步骤

#### 1. 下载gradle-wrapper.jar
```powershell
# 使用PowerShell
cd c:\Users\remote\CodeBuddy\20260312103313
Invoke-WebRequest -Uri "https://github.com/nickmcdowall/gradle-wrapper-jar/blob/master/gradle-wrapper.jar?raw=true" -OutFile "gradle\wrapper\gradle-wrapper.jar"
```

或手动下载:
- 地址: https://github.com/nickmcdowall/gradle-wrapper-jar/blob/master/gradle-wrapper.jar?raw=true
- 保存到: `gradle\wrapper\gradle-wrapper.jar`

#### 2. 运行编译脚本
```bash
# Windows
compile.bat

# 或手动执行
gradlew.bat assembleDebug
```

#### 3. 获取APK
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## 方法三：使用在线编译服务

### 使用JitPack（需要GitHub仓库）
1. 将项目上传到GitHub
2. 访问 https://jitpack.io/
3. 输入GitHub仓库地址
4. 点击 "Get it" 生成APK

### 使用GitHub Actions
创建 `.github/workflows/build.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build with Gradle
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

---

## 常见问题解决

### 问题1: Gradle sync failed
**解决方案:**
```
1. 检查网络连接
2. 配置国内镜像源（如果在国内）
   在build.gradle中添加:
   maven { url 'https://maven.aliyun.com/repository/google' }
   maven { url 'https://maven.aliyun.com/repository/public' }
```

### 问题2: SDK location not found
**解决方案:**
```bash
# 创建local.properties文件
sdk.dir=C\:\\Users\\[用户名]\\AppData\\Local\\Android\\Sdk
```

### 问题3: Java version mismatch
**解决方案:**
```
确保使用JDK 17或更高版本
在Android Studio中: File → Project Structure → SDK Location → JDK location
```

### 问题4: Gradle下载慢
**解决方案:**
```properties
# 修改 gradle-wrapper.properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
```

---

## 项目信息

- **应用名称**: 照片水印工具
- **包名**: com.example.watermarkapp
- **版本**: 1.0.0
- **最低SDK**: Android 7.0 (API 24)
- **目标SDK**: Android 14 (API 34)

---

## 技术支持

如遇到编译问题，请检查：
1. JDK版本是否正确（需要17+）
2. Android SDK是否正确安装
3. 网络连接是否正常
4. Gradle wrapper jar文件是否存在

编译成功后，APK文件位于：
```
app/build/outputs/apk/debug/app-debug.apk
```
