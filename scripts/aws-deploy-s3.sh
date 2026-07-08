#!/bin/bash
set -e

MODULE="${CI_COMMIT_TAG%%-*}"
VERSION="${CI_COMMIT_TAG#*-}"

echo "Obtaining version from tag..."
echo "Module: $MODULE"
echo "Version: $VERSION"

if [ -f "$MODULE/build/outputs/aar/$MODULE-release.aar" ]; then
    FILE_PATH="$MODULE/build/outputs/aar/$MODULE-release.aar"
elif [ -f "$MODULE/build/libs/$MODULE.jar" ]; then
    FILE_PATH="$MODULE/build/libs/$MODULE.jar"
else
    echo "No artifact found"
    exit 1
fi

LATEST_PATH="loginid-android/$MODULE/latest/$MODULE-release.aar"
S3_VERSIONED_PATH="loginid-android/$MODULE/$VERSION/$MODULE-release.aar"

echo "Uploading $FILE_PATH to s3://$S3_BUCKET/$LATEST_PATH"
aws s3 cp "$FILE_PATH" "s3://$S3_BUCKET/$LATEST_PATH"

echo "Uploading $FILE_PATH to s3://$S3_BUCKET/$S3_VERSIONED_PATH"
aws s3 cp "$FILE_PATH" "s3://$S3_BUCKET/$S3_VERSIONED_PATH"
