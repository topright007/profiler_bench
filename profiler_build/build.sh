#!/bin/bash

set -e

# Save the original directory
ORIGINAL_DIR=$(pwd)

# Set trap to return to the original directory on exit
trap 'cd "$ORIGINAL_DIR"' EXIT

# Change to the script's directory
cd "$(dirname "$0")"

git clone https://github.com/topright007/qubership-profiler-agent.git profiler_agent || true
cd profiler_agent
./gradlew :installer:installerZip :profiler:build
cd ..
mkdir -p installer && rm -rf installer/*
unzip -oq profiler_agent/installer/build/distributions/qubership-profiler-installer-3.1.3-SNAPSHOT.zip -d installer
cp config.xml installer/config/default/75post/netcracker.generic.xml

git clone https://github.com/topright007/qubership-profiler-backend.git profiler_backend || true
cd profiler_backend
git checkout strip_netcracker_ui_modules
git pull
cd apps/query
npm install
chmod +x build.sh && ./build.sh
cp netcracker-cloud-profiler-ui-*.tgz ../collector/cloud-profiler-ui.tgz

cd ../collector
rm -rf target/cloud-profiler-ui
mkdir -p target/cloud-profiler-ui
tar -xvzf cloud-profiler-ui.tgz -C target/cloud-profiler-ui

UI_RESOURCES_DIR=src/main/resources/META-INF/resources
rm -rf "$UI_RESOURCES_DIR/static"
cp -fR target/cloud-profiler-ui/package/build/* "$UI_RESOURCES_DIR/"

if [[ ! -d "$UI_RESOURCES_DIR/static/css" || ! -d "$UI_RESOURCES_DIR/static/js" ]]; then
  echo "ERROR: UI static directories are missing after sync: expected static/css and static/js"
  exit 1
fi

./mvnw --batch-mode clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -V -B
docker build . -t "profiler-collector:latest"

# Build maintenance (migrate + cron for dynamic tables)
cd ../../
docker build -f apps/maintenance/Dockerfile . -t "profiler-maintenance:latest" 
