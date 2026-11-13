@echo off
chcp 65001 >nul
echo ========================================
echo 报表服务 - 停止脚本
echo ========================================

set APP_NAME=miracle-admin
set JAR_NAME=%APP_NAME%.jar
set SERVER_PORT=18088

echo 正在查找占用端口 %SERVER_PORT% 的进程...
set FOUND=0
set PID=

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
    set PID=%%a
    set FOUND=1
)

if "%FOUND%"=="0" (
    echo ========================================
    echo 端口 %SERVER_PORT% 未被占用，服务未运行
    echo ========================================
    pause
    exit /b 0
)

echo 找到运行中的服务，PID: %PID%
echo 正在停止服务...

taskkill /F /PID %PID% >nul 2>&1

if %errorlevel% equ 0 (
    timeout /t 2 /nobreak >nul

    echo 再次检查端口状态...
    set STILL_RUNNING=0
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
        set STILL_RUNNING=1
    )

    if "%STILL_RUNNING%"=="0" (
        echo ========================================
        echo 服务已成功停止
        echo ========================================
    ) else (
        echo ========================================
        echo 服务停止失败，请手动检查
        echo ========================================
    )
) else (
    echo ========================================
    echo 服务停止失败，请检查权限
    echo ========================================
)

pause
