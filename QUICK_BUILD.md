# 快速编译指南

## 🚀 最快方法：使用Android Studio（推荐）

### 步骤：

1. **安装Android Studio**
   - 下载：https://developer.android.com/studio
   - 安装时选择标准安装（会自动安装JDK和Android SDK）

2. **打开项目**
   ```
   Android Studio → File → Open → 选择项目文件夹
   ```

3. **等待Gradle同步**
   - 首次打开会自动下载依赖（约5-15分钟）
   - 点击底部的"Build"标签查看进度

4. **编译APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

5. **获取APK**
   - 编译成功后点击通知的"locate"链接
   - APK位置：`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 方法二：使用GitHub Actions（无需本地环境）

### 步骤：

1. **创建GitHub仓库**
   - 登录 https://github.com
   - 创建新仓库

2. **上传项目代码**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/你的用户名/仓库名.git
   git push -u origin main
   ```

3. **触发自动编译**
   - 上传代码后会自动触发GitHub Actions
   - 或在Actions页面手动触发

4. **下载APK**
   - Actions页面 → 最新的workflow → Artifacts
   - 下载 `app-debug` 或 `app-release`

---

## 🛠️ 方法三：命令行编译（需要配置环境）

### 环境要求：
- JDK 17+
- Android SDK
- Gradle 8.2+

### 步骤：

#### 1. 安装JDK 17
```bash
# Windows: 下载并安装
https://adoptium.net/

# 设置环境变量
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17-hotspot
Path += %JAVA_HOME%\bin
```

#### 2. 安装Android SDK
```bash
# 方法1: 使用Android Studio（推荐）
安装Android Studio会自动安装SDK

# 方法2: 命令行工具
https://developer.android.com/studio#command-tools
```

#### 3. 配置local.properties
```properties
sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

#### 4. 编译
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

---

## ❓ 常见问题

### Q: Gradle sync失败怎么办？
**A:**
1. 检查网络连接
2. 使用国内镜像（编辑build.gradle）：
```gradle
repositories {
    maven { url 'https://maven.aliyun.com/repository/google' }
    maven { url 'https://maven.aliyun.com/repository/public' }
    google()
    mavenCentral()
}
```

### Q: 找不到Android SDK？
**A:**
1. 打开Android Studio
2. File → Project Structure → SDK Location
3. 设置SDK路径

### Q: 编译慢怎么办？
**A:**
1. 使用国内Gradle镜像：
```properties
# gradle-wrapper.properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
```

2. 启用Gradle守护进程：
```properties
# gradle.properties
org.gradle.daemon=true
org.gradle.parallel=true
```

---

## ✅ 验证编译成功

编译成功后，检查以下文件：
```
app/build/outputs/apk/debug/app-debug.apk        # Debug版本
app/build/outputs/apk/release/app-release.apk    # Release版本
```

APK文件大小约：5-10MB

---

## 📱 安装APK

### 方法1: ADB安装
```bash
adb install app-debug.apk
```

### 方法2: 手机直接安装
1. 将APK传输到手机
2. 打开文件管理器
3. 点击APK文件
4. 允许安装未知来源应用
5. 安装

---

## 🎯 推荐流程

对于首次使用Android开发的用户：

1. ✅ **使用Android Studio** - 最简单，图形界面
2. ✅ **等待Gradle同步** - 自动处理依赖
3. ✅ **点击Build APK** - 一键编译
4. ✅ **安装测试** - 直接安装到手机

总耗时：首次约30分钟，后续编译约2-3分钟

---

## 📞 需要帮助？

如果遇到编译问题，请提供以下信息：
1. Android Studio版本
2. JDK版本（`java -version`）
3. 错误日志（Build → Make Project的输出）
4. 操作系统版本

---

**祝编译成功！** 🎉
