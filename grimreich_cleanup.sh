#!/bin/bash
# GrimReich Cleanup and Build Script

echo "Cleaning up legacy assets and temporary files..."

# Remove any stray legacy references if they somehow reappear
find . -type d -name "darklandsmobile" -exec rm -rf {} +

# Clean build artifacts
./gradlew clean

echo "Cleanup complete."
