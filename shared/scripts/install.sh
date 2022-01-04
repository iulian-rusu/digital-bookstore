#!/bin/sh

set -ae
. ../.env
set +a

INSTALL_TARGET=target/shared-1.0.0.jar

if [ -f $INSTALL_TARGET ]; then
  echo "[deploy] - Found package: $INSTALL_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

./mvnw install:install-file