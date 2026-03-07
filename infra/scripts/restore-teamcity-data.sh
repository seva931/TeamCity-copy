#!/usr/bin/env bash
set -euo pipefail

ARCHIVE_URL="${1:?Provide archive URL}"
ARCHIVE_NAME="teamcity-data.tar.gz"

rm -rf teamcity-data "$ARCHIVE_NAME"
mkdir -p teamcity-data

curl -fL "$ARCHIVE_URL" -o "$ARCHIVE_NAME"
tar -xzf "$ARCHIVE_NAME"

echo "Restored teamcity-data from $ARCHIVE_URL"