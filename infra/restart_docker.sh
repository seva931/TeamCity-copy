#!/bin/bash

set -e

PROFILE_ARGS=()
for p in "$@"; do
  PROFILE_ARGS+=(--profile "$p")
done

echo ">>> Остановка docker compose"
docker compose down

echo ">>> Docker pull все образы браузеров"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BROWSERS_JSON="$SCRIPT_DIR/browsers.json"

if [[ ! -f "$BROWSERS_JSON" ]]; then
  echo "Файл не найден: $BROWSERS_JSON"
  exit 1
fi

if ! command -v jq >/dev/null 2>&1; then
  echo "Нужен jq для чтения $BROWSERS_JSON"
  exit 1
fi

images_found=0
while IFS= read -r image; do
  [[ -z "$image" ]] && continue
  images_found=1
  echo ">>> docker pull $image"
  docker pull "$image"
done < <(jq -r '.. | .image? // empty' "$BROWSERS_JSON" | sort -u)

if [[ "$images_found" -eq 0 ]]; then
  echo "В $BROWSERS_JSON не найдено ни одного image"
  exit 1
fi

echo ">>> Запуск Docker Compose ${PROFILE_ARGS[*]}"
docker compose "${PROFILE_ARGS[@]}" up -d