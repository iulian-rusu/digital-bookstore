#!/bin/sh

set -ae
. ../.env
set +a

ORDERS_TARGET=target/orders-1.0.0.jar

if [ -f $ORDERS_TARGET ]; then
  echo "[deploy] - Found package: $ORDERS_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $ORDERS_TARGET