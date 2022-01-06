#!/bin/sh

set -ae
. ../.env
set +a

SHARED_TARGET=target/shared-1.0.0.jar

if [ -f $SHARED_TARGET ]; then
  echo "[deploy] - Found package: $SHARED_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $SHARED_TARGET