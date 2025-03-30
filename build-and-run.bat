@echo off
setlocal

REM Lista dos projetos
set PROJECTS=reservation-api reservation-batch reservation-notification

echo Starting projects build......

for %%P in (%PROJECTS%) do (
    echo Building %%P...
    cd %%P
    call mvnw.cmd clean package
    cd ..
)

echo Build finished. Starting docker compose...

docker compose up --build
