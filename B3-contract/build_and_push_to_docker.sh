#!/bin/bash
#$1 -- image
"$(dirname $0)"/../gradlew clean build
docker build "$(dirname $0)" -f "$(dirname $0)"/Dockerfile --platform linux/amd64 -t $1
docker push $1
