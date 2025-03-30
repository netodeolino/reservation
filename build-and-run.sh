#!/bin/bash

set -e

PROJECTS=("reservation-api" "reservation-batch" "reservation-notification")

echo "Starting projects build..."

for PROJECT in "${PROJECTS[@]}"; do
  echo "Building $PROJECT..."
  (cd "$PROJECT" && ./mvnw clean package)
done

echo "Build finished. Starting docker compose..."

docker compose up --build
