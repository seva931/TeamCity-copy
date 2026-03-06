# run.ps1
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\run.ps1
#   powershell -ExecutionPolicy Bypass -File .\run.ps1 profile1 profile2

$ErrorActionPreference = "Stop"

# Чтобы кириллица в консоли не ломалась:
# (Если запускаешь в Windows PowerShell 5.1, лучше открыть терминал с UTF-8 или использовать pwsh)
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Сбор профилей как: --profile p1 --profile p2 ...
$PROFILE_ARGS = @()
foreach ($p in $args) {
  $PROFILE_ARGS += "--profile"
  $PROFILE_ARGS += $p
}

Write-Host ">>> Остановка docker compose"
docker compose down

Write-Host ">>> Docker pull все образы браузеров"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$BrowsersJson = Join-Path $ScriptDir "browsers.json"

if (!(Test-Path $BrowsersJson)) {
  Write-Error "Файл не найден: $BrowsersJson"
  exit 1
}

try {
  $jsonText = Get-Content -Raw -Encoding UTF8 $BrowsersJson
  $json = $jsonText | ConvertFrom-Json
} catch {
  Write-Error "Не удалось прочитать JSON: $BrowsersJson. Ошибка: $($_.Exception.Message)"
  exit 1
}

function Get-Images($node) {
  $result = New-Object System.Collections.Generic.List[string]
  if ($null -eq $node) { return $result }

  # PSCustomObject (основной случай для ConvertFrom-Json)
  if ($node -is [psobject] -and $node.PSObject.Properties.Count -gt 0) {
    foreach ($prop in $node.PSObject.Properties) {
      if ($prop.Name -eq "image" -and $prop.Value) {
        $result.Add([string]$prop.Value)
      }
      foreach ($img in (Get-Images $prop.Value)) { $result.Add($img) }
    }
    return $result
  }

  # Hashtable/Dictionary (на всякий)
  if ($node -is [System.Collections.IDictionary]) {
    foreach ($key in $node.Keys) {
      $val = $node[$key]
      if ($key -eq "image" -and $val) { $result.Add([string]$val) }
      foreach ($img in (Get-Images $val)) { $result.Add($img) }
    }
    return $result
  }

  # Array/List
  if ($node -is [System.Collections.IEnumerable] -and -not ($node -is [string])) {
    foreach ($item in $node) {
      foreach ($img in (Get-Images $item)) { $result.Add($img) }
    }
    return $result
  }

  return $result
}

$images = Get-Images $json | Where-Object { $_ -and $_.Trim() -ne "" } | Sort-Object -Unique

if (!$images -or $images.Count -eq 0) {
  Write-Error "В $BrowsersJson не найдено ни одного image"
  exit 1
}

foreach ($image in $images) {
  Write-Host ">>> docker pull $image"
  docker pull $image
}

Write-Host ">>> Запуск Docker Compose $($PROFILE_ARGS -join ' ')"
docker compose @PROFILE_ARGS up -d