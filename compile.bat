@echo off
REM ========================================
REM 照片水印应用编译脚本
REM ========================================

echo.
echo ========================================
echo   照片水印应用 - 编译脚本
echo ========================================
echo.

REM 检查Java环境
echo [1/5] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java环境！
    echo 请安装JDK 17或更高版本
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)
echo [成功] Java环境正常

REM 下载gradle-wrapper.jar
echo.
echo [2/5] 准备Gradle环境...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo 正在下载gradle-wrapper.jar...
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/nickmcdowall/gradle-wrapper-jar/blob/master/gradle-wrapper.jar?raw=true' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        echo [错误] gradle-wrapper.jar下载失败
        echo 请手动下载: https://github.com/nickmcdowall/gradle-wrapper-jar/blob/master/gradle-wrapper.jar?raw=true
        echo 并保存到: gradle\wrapper\gradle-wrapper.jar
        pause
        exit /b 1
    )
)
echo [成功] Gradle环境准备完成

REM 清理项目
echo.
echo [3/5] 清理项目...
call gradlew.bat clean

REM 编译项目
echo.
echo [4/5] 编译项目...
call gradlew.bat assembleDebug

REM 检查编译结果
echo.
echo [5/5] 检查编译结果...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ========================================
    echo   编译成功！
    echo ========================================
    echo.
    echo APK文件位置:
    echo %cd%\app\build\outputs\apk\debug\app-debug.apk
    echo.
    
    REM 创建输出目录
    if not exist "output" mkdir output
    
    REM 复制APK到输出目录
    copy /Y "app\build\outputs\apk\debug\app-debug.apk" "output\WatermarkApp-v1.0.apk" >nul
    echo APK已复制到: %cd%\output\WatermarkApp-v1.0.apk
    echo.
    
    REM 打开输出目录
    explorer output
) else (
    echo.
    echo ========================================
    echo   编译失败！
    echo ========================================
    echo.
    echo 请检查编译日志，确保所有依赖项正确安装
)

pause
