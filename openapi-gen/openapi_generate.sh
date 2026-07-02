#!/bin/bash

set -e

rm -rf ./loginid-fido2

git clone git@gitlab.com:loginid/software/loginid-fido2.git
cp ./loginid-fido2/api/rest/openapi3.yaml ./openapi.yaml
node ./pre_openapi_gen.mjs
cd ..
rm -rf .api/generated/api/generated
./gradlew openApiGenerate

rm -rf ./loginid-fido2
