#!/bin/bash
#$1 -- image
./gradlew clean build
docker build "$(dirname $0)" -f "$(dirname $0)"/Dockerfile --platform linux/amd64 -t $1
docker push $1
