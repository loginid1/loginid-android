#!/usr/bin/env bash
set -euo pipefail

TARGET="${1:-auth}"

case "$TARGET" in
    auth|mfa) ;;
    *)
        echo "Usage: $0 [auth|mfa]"
        exit 1
        ;;
esac

API_JAR="api/build/libs/api.jar"
CORE_AAR="core/build/outputs/aar/core-release.aar"
TARGET_AAR="$TARGET/build/outputs/aar/${TARGET}-release.aar"

echo "Building merged AAR for target: $TARGET"

OUT=build/merged
echo "Preparing build directory..."
rm -rf "$OUT"
mkdir -p "$OUT"

# Extract AARs
echo "Extracting AARs..."
unzip -q "$CORE_AAR" -d "$OUT/core"
unzip -q "$TARGET_AAR" -d "$OUT/$TARGET"

# Extract all jars
echo "Extracting class files..."
mkdir "$OUT/classes"

for j in \
    "$OUT/core/classes.jar" \
    "$OUT/$TARGET/classes.jar" \
    "$API_JAR"
do
    (
        cd "$OUT/classes"
        jar xf "$OLDPWD/$j"
    )
done

# Build merged classes.jar
echo "Creating merged classes.jar..."
(
    cd "$OUT/classes"
    jar cf ../classes.jar .
)

# Merge ProGuard rules
echo "Merging ProGuard rules..."
cat \
    "$OUT/core/proguard.txt" \
    "$OUT/$TARGET/proguard.txt" \
    > "$OUT/proguard.txt"

# Use core manifest
echo "Copying manifest and metadata..."
cp "$OUT/$TARGET/AndroidManifest.xml" "$OUT/AndroidManifest.xml"

# Copy metadata
cp "$OUT/$TARGET/R.txt" "$OUT/R.txt"
mkdir -p "$OUT/META-INF/com/android/build/gradle"
cp "$OUT/$TARGET/META-INF/com/android/build/gradle/aar-metadata.properties" \
   "$OUT/META-INF/com/android/build/gradle/"

# Package final AAR
echo "Packaging final AAR..."
(
    cd "$OUT"
    zip -qr "../loginid-${TARGET}-sdk.aar" \
        AndroidManifest.xml \
        classes.jar \
        R.txt \
        proguard.txt \
        META-INF
)

echo "Created build/loginid-${TARGET}-sdk.aar"
