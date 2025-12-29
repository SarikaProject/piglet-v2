@echo off
echo Starting PigletV2...
start http://localhost:8080/piglet-test-report.html
timeout /t 1 /nobreak >nul
docker run --rm -p 8080:8080 pigletv2:latest