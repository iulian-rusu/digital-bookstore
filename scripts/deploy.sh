#!/bin/sh

set -a
. ./.env
set +a

IDENTITY_TARGET=identity/target/identity-1.0.0.jar

if [ -f $IDENTITY_TARGET ]; then
  echo "[deploy] - Found package: $IDENTITY_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $IDENTITY_TARGET

BOOK_LIBRARY_TARGET=book-library/target/book-library-1.0.0.jar

if [ -f $BOOK_LIBRARY_TARGET ]; then
  echo "[deploy] - Found package: $BOOK_LIBRARY_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $BOOK_LIBRARY_TARGET

ORDERS_TARGET=orders/target/orders-1.0.0.jar

if [ -f $ORDERS_TARGET ]; then
  echo "[deploy] - Found package: $ORDERS_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $ORDERS_TARGET