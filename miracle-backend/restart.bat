@echo off
chcp 65001 >nul
echo ========================================
echo 报表服务 - 重启脚本
echo ========================================

set APP_NAME=miracle-admin
set JAR_NAME=%APP_NAME%.jar
set SERVER_PORT=18088
set LOG_FILE=%APP_NAME%.log

echo 正在查找占用端口 %SERVER_PORT% 的进程...
set FOUND=0
set OLD_PID=

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
    set OLD_PID=%%a
    set FOUND=1
)

if "%FOUND%"=="1" (
    echo 找到运行中的服务，PID: %OLD_PID%
    echo 正在停止服务...
    taskkill /F /PID %OLD_PID% >nul 2>&1

    if %errorlevel% equ 0 (
        echo 服务已停止
        timeout /t 3 /nobreak >nul
    ) else (
        echo 服务停止失败，请手动检查
        pause
        exit /b 1
    )
) else (
    echo 端口 %SERVER_PORT% 未被占用，服务未运行
)

echo.
echo 正在启动服务...
echo 服务端口: %SERVER_PORT%

start /b javaw -jar "%JAR_NAME%" > "%LOG_FILE%" 2>&1

timeout /t 5 /nobreak >nul

echo 检查服务启动状态...
set NEW_PID=
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
    set NEW_PID=%%a
)

if defined NEW_PID (
    echo ========================================
    echo 服务重启成功！
    echo 旧 PID: %OLD_PID%
    echo 新 PID: %NEW_PID%
    echo 日志文件: %LOG_FILE%
    echo 访问地址: http://localhost:%SERVER_PORT%
    echo ========================================
    echo.
    echo 服务已在后台运行，关闭此窗口不会影响服务。
) else (
    echo ========================================
    echo 服务启动失败，请查看日志文件: %LOG_FILE%
    echo ========================================
)

pause
