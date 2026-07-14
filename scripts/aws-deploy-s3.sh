#!/bin/bash
set -e

VERSION="${CI_COMMIT_TAG}"

echo "Obtaining version from tag..."
echo "Version: $VERSION"

upload_sdk() {
    local sdk_name=$1
    local sdk_path="build/loginid-${sdk_name}-sdk.aar"

    if [ ! -f "$sdk_path" ]; then
        echo "$sdk_path not found"
        exit 1
    fi

    echo "Uploading ${sdk_name} SDK"
    local latest_path="loginid-android/${sdk_name}/latest/loginid-${sdk_name}-sdk.aar"
    local versioned_path="loginid-android/${sdk_name}/${VERSION}/loginid-${sdk_name}-sdk.aar"

    echo "Uploading $sdk_path to s3://$S3_BUCKET/$latest_path"
    aws s3 cp "$sdk_path" "s3://$S3_BUCKET/$latest_path"

    echo "Uploading $sdk_path to s3://$S3_BUCKET/$versioned_path"
    aws s3 cp "$sdk_path" "s3://$S3_BUCKET/$versioned_path"
}

if [ "$#" -eq 0 ]; then
    echo "Usage: $0 <sdk...>"
    echo "Example: $0 auth mfa"
    exit 1
fi

for sdk in "$@"; do
    upload_sdk "$sdk"
done
