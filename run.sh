#!/usr/bin/env bash
set -e

echo "============================================================"
echo "  APEX HORIZON BANK - OBJECT-ORIENTED BANKING SYSTEM"
echo "============================================================"
echo ""

mkdir -p bin

echo "Compiling Java source files..."
javac -d bin $(find src -name "*.java")

echo "Compilation successful! Starting application..."
echo ""
java -cp bin com.bank.Main "$@"
