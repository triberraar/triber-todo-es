#!/bin/bash
set -ev
if [ ! -z "$TRAVIS_TAG" ]; then
	docker login -u="$DOCKER_USER" -p="$DOCKER_PASS"
    docker build --build-arg tag=$TRAVIS_TAG -t triberraar/triber-todo-gateway:latest -t triberraar/triber-todo-gateway:$TRAVIS_TAG .
    docker push triberraar/triber-todo-gateway
fi