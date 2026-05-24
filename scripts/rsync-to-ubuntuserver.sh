#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_dir="$(cd "$script_dir/.." && pwd)"
target="${1:-ubuntuserver:/home/nathan/coinbasekafkaservice}"

if ! command -v rsync >/dev/null 2>&1; then
  echo "rsync is required but was not found in PATH" >&2
  exit 1
fi

rsync -az --delete \
  --exclude '*.zip' \
  --exclude '.DS_Store' \
  "$repo_dir/" "$target/"

echo "$target"
