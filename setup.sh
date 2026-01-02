#!/usr/bin/env bash
set -euo pipefail

JDK_VERSION="17"
JDK_DIR=".jdk"

cd "$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"

mkdir -p "$JDK_DIR"
JAVA_ALREADY_INSTALLED=false
if [ -x "$JDK_DIR/current/bin/java" ] || ls "$JDK_DIR"/jdk-${JDK_VERSION}* >/dev/null 2>&1; then
  JAVA_ALREADY_INSTALLED=true
fi

if [ "$JAVA_ALREADY_INSTALLED" = false ]; then
  if command -v uname >/dev/null 2>&1; then
    OS_RAW="$(uname -s)"
  else
    OS_RAW="unknown"
  fi
  ARCH_RAW="$(uname -m 2>/dev/null || echo unknown)"

  case "$OS_RAW" in
    Linux*)   ADOPTIUM_OS="linux"   ; EXT="tar.gz" ;;
    Darwin*)  ADOPTIUM_OS="mac"      ; EXT="tar.gz" ;;
    MINGW*|MSYS*|CYGWIN*) ADOPTIUM_OS="windows" ; EXT="zip" ;;
    *)
      echo "Installing Java - Error"
      exit 1
      ;;
  esac

  case "$ARCH_RAW" in
    x86_64|amd64)     ADOPTIUM_ARCH="x64" ;;
    aarch64|arm64)    ADOPTIUM_ARCH="aarch64" ;;
    *)
      echo "Installing Java - Error"
      exit 1
      ;;
  esac

  URL="https://api.adoptium.net/v3/binary/latest/${JDK_VERSION}/ga/${ADOPTIUM_OS}/${ADOPTIUM_ARCH}/jdk/hotspot/normal/eclipse"
  FILE="$JDK_DIR/openjdk-${JDK_VERSION}-${ADOPTIUM_OS}-${ADOPTIUM_ARCH}.${EXT}"

  echo "Installing Java - Downloading..."

  if command -v curl >/dev/null 2>&1; then
    if ! curl -fsSL "$URL" -o "$FILE" >/dev/null 2>&1; then
      echo "Installing Java - Error"
      exit 1
    fi
  elif command -v wget >/dev/null 2>&1; then
    if ! wget -q -O "$FILE" "$URL" >/dev/null 2>&1; then
      echo "Installing Java - Error"
      exit 1
    fi
  else
    echo "Installing Java - Error"
    exit 1
  fi

  echo "Installing Java - Extracting..."

  if [ "$EXT" = "zip" ]; then
    if ! unzip -o "$FILE" -d "$JDK_DIR" >/dev/null 2>&1; then
      echo "Installing Java - Error"
      exit 1
    fi
  else
    if ! tar -xzf "$FILE" -C "$JDK_DIR" >/dev/null 2>&1; then
      echo "Installing Java - Error"
      exit 1
    fi
  fi

  rm -f "$FILE" || true
fi

cd "$JDK_DIR"
EXTRACTED_DIR="$(ls -d jdk-${JDK_VERSION}* 2>/dev/null | head -n1 || ls -d * 2>/dev/null | head -n1)"

if [ -z "$EXTRACTED_DIR" ]; then
  echo "Installing Java - Error"
  exit 1
fi

ln -sfn "$EXTRACTED_DIR" current 2>/dev/null || true

echo "(1/3) Installing Java - Done"

cd ".."

if [ ! -x "./gradlew" ]; then
  echo "(2/3) Preparing IDE - Skipped (no ./gradlew)"
  echo "(3/3) Preparing Test Server - Skipped (no ./gradlew)"
  exit 0
fi

echo
echo "IDE setup:"
echo "  1) VS Code (prepareRuns)"
echo "  2) IntelliJ (genIntellijRuns)"

IDE_CHOICE=""
while [ "$IDE_CHOICE" != "1" ] && [ "$IDE_CHOICE" != "2" ]; do
  read -r -p "Choose 1 or 2: " IDE_CHOICE
done

echo "(2/3) Preparing IDE..."

if [ "$IDE_CHOICE" = "1" ]; then
  ./gradlew prepareRuns
else
  ./gradlew genIntellijRuns
fi

echo "(2/3) Preparing IDE - Done"

echo "(3/3) Preparing Test Server - First run..."

# First run: generate eula.txt if needed (expected to fail once due to EULA)
if [ ! -f "run/server/eula.txt" ]; then
  set +e
  ./gradlew runServer --args nogui > /dev/null 2>&1
  set -e
fi

EULA_FILE="run/server/eula.txt"
if [ ! -f "$EULA_FILE" ]; then
  echo "Could not find $EULA_FILE after first server run."
  exit 1
fi

echo "(3/3) Preparing Test Server - Accepting EULA..."

# Accept EULA
if grep -q "^eula=false" "$EULA_FILE" 2>/dev/null; then
  if sed --version >/dev/null 2>&1; then
    sed -i 's/^eula=false/eula=true/' "$EULA_FILE"
  else
    sed -i '' 's/^eula=false/eula=true/' "$EULA_FILE"
  fi
elif ! grep -q "^eula=true" "$EULA_FILE" 2>/dev/null; then
  echo "eula=true" >> "$EULA_FILE"
fi

echo "(3/3) Preparing Test Server - Second run..."

# Second run: start server, wait for server.properties, then stop it

set +e
./gradlew runServer --args nogui > /dev/null 2>&1 &
SERVER_PID=$!
set -e

PROP_FILE="run/server/server.properties"
WAITED=0
MAX_WAIT="${SERVER_PROP_TIMEOUT:-300}"

while [ ! -f "$PROP_FILE" ] && kill -0 "$SERVER_PID" 2>/dev/null && [ "$WAITED" -lt "$MAX_WAIT" ]; do
  sleep 5
  WAITED=$((WAITED + 5))
done


if [ -f "$PROP_FILE" ]; then
  # Give the properties file a bit of extra time to fully flush
  sleep 5
  kill "$SERVER_PID" 2>/dev/null || true
else
  echo "Warning: $PROP_FILE not created within ${MAX_WAIT}s or server exited early; stopping server if still running."
  kill "$SERVER_PID" 2>/dev/null || true
fi

set +e
wait "$SERVER_PID" 2>/dev/null || true
set -e

echo "(3/3) Preparing Test Server - Editing properties..."

# Update server.properties according to README (up to step 3.5)
PROP_FILE="run/server/server.properties"
if [ -f "$PROP_FILE" ]; then
  set_prop() {
    local key="$1"; local value="$2"; local file="$3"
    if grep -q "^${key}=" "$file" 2>/dev/null; then
      if sed --version >/dev/null 2>&1; then
        sed -i "s/^${key}=.*/${key}=${value}/" "$file"
      else
        sed -i '' "s/^${key}=.*/${key}=${value}/" "$file"
      fi
    else
      echo "${key}=${value}" >> "$file"
    fi
  }

  set_prop "online-mode" "false" "$PROP_FILE"
  set_prop "gamemode" "1" "$PROP_FILE"
  set_prop "level-seed" "700" "$PROP_FILE"
else
  echo "Warning: $PROP_FILE not found; skipping server.properties tweaks."
fi

# Remove generated world so changes like level-seed take effect on next start
WORLD_DIR="run/server/world"
if [ -d "$WORLD_DIR" ]; then
  rm -rf "$WORLD_DIR"
fi

echo "(3/3) Preparing Test Server - Done"
