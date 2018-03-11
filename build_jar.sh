#!/bin/bash
jenv versions

./gradlew clean build
IMAGE=`docker build -q .`
echo $IMAGE
CONTAINER=`docker run -d $IMAGE`
docker logs -f $CONTAINER