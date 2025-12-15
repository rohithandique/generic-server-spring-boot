#!/bin/bash

# Resolve the project root directory (assuming this script is inside the 'scripts' folder)
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"

# Check if .env file exists
if [[ ! -f "$ENV_FILE" ]]; then
  echo "Error: .env file not found at $ENV_FILE" >&2
  exit 1
fi

# Extract SONAR_TOKEN from .env
# This looks for the key, splits by =, removes quotes (single or double), and removes Windows carriage returns
SONAR_TOKEN=$(grep "^SONAR_TOKEN=" "$ENV_FILE" | cut -d'=' -f2- | tr -d '"' | tr -d "'" | tr -d '\r')

if [ -z "$SONAR_TOKEN" ]; then
  echo "Error: SONAR_TOKEN not found or empty in .env file."
  exit 1
fi

# Navigate to project root and run the Gradle command
cd "$PROJECT_ROOT" || exit
./gradlew clean build jacocoTestReport sonar -Dsonar.token="$SONAR_TOKEN"