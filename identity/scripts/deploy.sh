#!/bin/sh

set -ae
. ../.env
set +a

IDENTITY_TARGET=target/identity-1.0.0.jar

if [ -f $IDENTITY_TARGET ]; then
  echo "[deploy] - Found package: $IDENTITY_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $IDENTITY_TARGET