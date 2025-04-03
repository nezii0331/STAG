#!/bin/bash

# Initialize an empty array to store the checked files
CHECKED_FILES=()

# Compile all Java source files using Maven
echo "Compiling all Java files..."
mvn compile

# Find all Java files under the specified directory and run STRANGE on each
echo "Running STRANGE on all source files..."
while IFS= read -r file; do
    CHECKED_FILES+=("$file")  # Add file to the checked list
    echo "Checking $file ..."
    ./mvnw exec:java@strange -Dexec.args="$file"
done < <(find src/main/java/edu/uob -name "*.java")

# Output a summary of all checked files
echo "----------------------------------------"
echo "Summary of Checked Files:"
for file in "${CHECKED_FILES[@]}"; do
    echo "  - $file"
done

echo "All checks complete!"