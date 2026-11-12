#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

if ! command -v javac >/dev/null 2>&1; then
    echo "[ERROR] Khong tim thay javac. Hay cai JDK 17+ va them vao PATH."
    exit 1
fi

mkdir -p bin
find src -name "*.java" -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d bin
exec java -cp bin Main "$@"
