#!/bin/bash

set -e

# Save the original directory
ORIGINAL_DIR=$(pwd)

# Set trap to return to the original directory on exit
trap 'cd "$ORIGINAL_DIR"' EXIT

# Change to the script's directory
cd "$(dirname "$0")"

git clone https://github.com/Netcracker/qubership-profiler-agent.git profiler_agent || true
cd profiler_agent
./gradlew :installer:installerZip :profiler:build
cd ..
mkdir -p installer && rm -rf installer/*
unzip -oq profiler_agent/installer/build/distributions/qubership-profiler-installer-3.1.3-SNAPSHOT.zip -d installer
