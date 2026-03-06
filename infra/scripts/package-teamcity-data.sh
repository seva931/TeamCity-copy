#!/usr/bin/env bash
set -euo pipefail

SRC_DIR="${1:-teamcity-data}"
ARCHIVE_NAME="${2:-teamcity-data.tar.gz}"

if [ ! -d "$SRC_DIR" ]; then
  echo "Directory '$SRC_DIR' not found"
  exit 1
fi

tar -czf "$ARCHIVE_NAME" "$SRC_DIR"
echo "Created archive: $ARCHIVE_NAME"