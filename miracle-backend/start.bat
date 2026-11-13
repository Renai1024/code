@echo off
chcp 65001 >nul
echo ========================================
echo 报表服务 - 启动脚本
echo ========================================

set APP_NAME=miracle-admin
set JAR_NAME=%APP_NAME%.jar
set LOG_FILE=%APP_NAME%.log
set SERVER_PORT=18088

echo 正在检查 %JAR_NAME% 是否存在...
if not exist "%JAR_NAME%" (
    echo 错误: 找不到 %JAR_NAME% 文件！
    echo 请确保已将打包好的 jar 文件放置在当前目录。
    pause
    exit /b 1
)

echo 正在检查端口 %SERVER_PORT% 是否已被占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
    set PID=%%a
)

if defined PID (
    echo 端口 %SERVER_PORT% 已被进程 %PID% 占用
    echo 如需重启，请先运行 stop.bat
    pause
    exit /b 0
)

echo 正在启动服务...
echo 日志文件: %LOG_FILE%
echo 服务端口: %SERVER_PORT%

start /b javaw -jar "%JAR_NAME%" > "%LOG_FILE%" 2>&1

timeout /t 5 /nobreak >nul

echo 检查服务启动状态...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT% "') do (
    set PID=%%a
)

if defined PID (
    echo ========================================
    echo 服务启动成功！
    echo PID: %PID%
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
